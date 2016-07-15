package com.nsak.android.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.adapters.BaseCommonResultAdapter;
import com.nsak.android.adapters.CommonResultAdapter;
import com.nsak.android.fragments.intf.ActivityInterface;
import com.nsak.android.ui.widget.DividerItemDecoration;
import com.nsak.android.utils.CommandLineUtils;
import com.nsak.android.utils.GlobalConfiguration;

import java.net.SocketTimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Vlad Namashko
 */

public abstract class CommonResultsFragment extends BaseFragment {

    public static final String EXTRA_COMMAND = "EXTRA_COMMAND";
    public static final int EXTRA_COMMAND_PING = 0;
    public static final int EXTRA_COMMAND_TRACEROUTE = 1;
    public static final int EXTRA_COMMAND_WHOIS = 2;
    public static final int EXTRA_COMMAND_MY_IP_ISP = 3;
    public static final int EXTRA_COMMAND_IP_CALCULATOR = 4;

    protected View mRootView;
    protected BaseCommonResultAdapter mAdapter;
    protected String mAddress;

    protected View mToolbar;

    @InjectView(R.id.results_view)
    protected RecyclerView mRecyclerView;

    protected ImageView mActionSettings;
    protected ProgressBar mProgress;
    protected View mStopCommand;

    private CompositeSubscription mSubscription;

    protected Handler mHandler;

    protected View mAddtionalSettings;

    protected int mAttempts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mAddress = getArguments().getString(NetworkScanActivity.ARG_SELECTED_HOST);
        }
        mHandler = new Handler();

        mRootView = inflater.inflate(R.layout.fragment_common_results, container, false);

        updateToolbar();
        ButterKnife.inject(this, mRootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        initAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mSubscription = new CompositeSubscription();

        doResult();
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.clear();
        mSubscription.unsubscribe();
    }

    protected void initAdapter() {
        mAdapter = new CommonResultAdapter();
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator = createSwipeRightAnimator(mRootView, enter);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mAddress == null) {
                    initEditOptions();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return animator;
    }

    protected void updateToolbar() {
        if (getActivity() != null) {
            mToolbar = LayoutInflater.from(getActivity()).inflate(R.layout.common_result_toolbar, null);

            mAddtionalSettings = mToolbar.findViewById(R.id.additional_settings);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = GlobalConfiguration.getDimensionSize(16);

            mToolbar.setLayoutParams(params);

            ((ActivityInterface) getActivity()).setViewToolbar(mToolbar);
            mActionSettings = (ImageView) mToolbar.findViewById(R.id.action_expand);
            mProgress = (ProgressBar) mToolbar.findViewById(R.id.common_result_progress);
            mStopCommand = mToolbar.findViewById(R.id.command_stop);

            final ViewGroup content = (ViewGroup) mToolbar.findViewById(R.id.edit_content);
            mActionSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchViewVisibility(content);
                }
            });

            if (mAddress != null) {
                hideSettingsIcon();
            }
        }
    }

    protected void hideSettingsIcon() {
        mActionSettings.setVisibility(View.GONE);
    }

    protected void switchViewVisibility(final ViewGroup content) {
        boolean isVisible = content.getVisibility() == View.VISIBLE;
        ObjectAnimator.ofFloat(mActionSettings, "rotation", isVisible ? 0 : 180,
                isVisible ? 180 : 0).setDuration(200).start();
        content.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    protected void switchViewVisibilityDelayed(final ViewGroup content) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchViewVisibility(content);
            }
        }, 250);
    }

    protected void doResult() {
        if (mAddress == null) {
            return;
        }

        mSubscription.clear();

        mAdapter.clearItems();

        mProgress.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        mProgress.setVisibility(View.VISIBLE);
        final Subscription subscription = getCommand().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<CommandLineUtils.CommandLineCommandOutput>() {
                    @Override
                    public void onCompleted() {
                        mProgress.setVisibility(View.GONE);
                        onCommandCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SocketTimeoutException && mAttempts < 3) {
                            mAttempts++;
                            doResult();
                        } else {
                            mStopCommand.setVisibility(View.GONE);
                            mProgress.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNext(CommandLineUtils.CommandLineCommandOutput output) {
                        onOutputReceived(output);
                    }
                });
        mSubscription.add(subscription);

        mStopCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscription.unsubscribe();
                mProgress.setVisibility(View.GONE);
                onCommandCompleted();
            }
        });
    }

    protected void hideKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean isBackOnToolbar() {
        return getArguments() != null;
    }

    @Override
    public void onMovedToForeground() {

    }

    protected void onCommandCompleted() {
    }

    @Override
    public void resetViewAndPerformAction(Runnable action) {
        action.run();
    }

    public static CommonResultsFragment newInstance(int command) {
        switch (command) {
            case EXTRA_COMMAND_PING: default:
                return new PingFragment();
            case EXTRA_COMMAND_TRACEROUTE:
                return new TracerouteFragment();
            case EXTRA_COMMAND_WHOIS:
                return new WhoisFragment();
            case EXTRA_COMMAND_MY_IP_ISP:
                return new IspFragment();
            case EXTRA_COMMAND_IP_CALCULATOR:
                return new IpCalculatorFragment();
        }
    }

    protected abstract Observable<CommandLineUtils.CommandLineCommandOutput> getCommand();

    protected abstract void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output);

    public abstract void initEditOptions();
}
