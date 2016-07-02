package com.nsak.android.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.adapters.CommonResultAdapter;
import com.nsak.android.fragments.intf.ActivityInterface;
import com.nsak.android.ui.widget.DividerItemDecoration;
import com.nsak.android.utils.CommandLineUtils;

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

    protected View mRootView;
    protected CommonResultAdapter mAdapter;
    protected String mAddress;

    protected View mToolbar;

    @InjectView(R.id.results_view)
    protected RecyclerView mRecyclerView;

    protected View mActionSettings;
    protected View mProgress;

    private CompositeSubscription mSubscription;

    protected Handler mHandler;

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
        mAdapter = new CommonResultAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mSubscription = new CompositeSubscription();

        doResult();
        return mRootView;
    }

    public void onDestroy() {
        super.onDestroy();
        mSubscription.clear();
        mSubscription.unsubscribe();
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
            ((ActivityInterface) getActivity()).setViewToolbar(mToolbar);
            mActionSettings = mToolbar.findViewById(R.id.action_settings);
            mProgress = mToolbar.findViewById(R.id.progress);

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
        mActionSettings.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = mActionSettings.getLayoutParams();
        params.width = 1;
        mActionSettings.setLayoutParams(params);
    }

    protected void switchViewVisibility(ViewGroup content) {
        content.setVisibility(content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
        mProgress.setVisibility(View.VISIBLE);
        Subscription subscription = getCommand().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<CommandLineUtils.CommandLineCommandOutput>() {
                    @Override
                    public void onCompleted() {
                        mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SocketTimeoutException && mAttempts < 3) {
                            mAttempts++;
                            doResult();
                        }
                        mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(CommandLineUtils.CommandLineCommandOutput output) {
                        onOutputReceived(output);
                    }
                });
        mSubscription.add(subscription);
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
        }
    }

    protected abstract Observable<CommandLineUtils.CommandLineCommandOutput> getCommand();

    protected abstract void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output);

    public abstract void initEditOptions();
}
