package com.nsak.android.fragments;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.adapters.CommonResultAdapter;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;
import com.nsak.android.network.Host;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.ui.widget.DividerItemDecoration;
import com.nsak.android.utils.CommandLineUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Vlad Namashko
 */

public class CommonResultsFragment extends BaseFragment {

    public static final String EXTRA_COMMAND = "EXTRA_COMMAND";
    public static final int EXTRA_COMMAND_PING = 0;
    public static final int EXTRA_COMMAND_TRACEROUTE = 1;

    protected View mRootView;
    protected CommonResultAdapter mAdapter;
    protected Host mSelectedHost;

    protected int mCommand;

    @InjectView(R.id.results_view)
    protected RecyclerView mRecyclerView;

    private CompositeSubscription mSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSelectedHost = getArguments().getParcelable(NetworkScanActivity.ARG_SELECTED_HOST);
        mRootView = inflater.inflate(R.layout.fragment_common_results, container, false);
        updateToolbar();
        ButterKnife.inject(this, mRootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CommonResultAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mSubscription = new CompositeSubscription();

        mCommand = getArguments().getInt(EXTRA_COMMAND);
        doResult();
        return mRootView;
    }

    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        return createSwipeRightAnimator(mRootView, enter);
    }

    private void updateToolbar() {
        if (getActivity() != null) {
            View toolbar = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_titled, null);
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(mSelectedHost.ipAddress);
            //mProgressBar = toolbar.findViewById(R.id.progress);
            //mProgressBar.setVisibility(View.VISIBLE);

            ((NetworkScanActivityInterface) getActivity()).setViewToolbar(toolbar);
        }
    }

    private void doResult() {
        Observable<CommandLineUtils.CommandLineCommandOutput> command = mCommand == EXTRA_COMMAND_PING ?
                NetworkUtils.pingCommand(mSelectedHost.ipAddress) : NetworkUtils.tracerouteCommand(mSelectedHost.ipAddress);
        Subscription subscription = command.subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<CommandLineUtils.CommandLineCommandOutput>() {
            @Override
            public void onCompleted() {
                //mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(CommandLineUtils.CommandLineCommandOutput output) {
                mAdapter.addItem(output.outputLine);
            }
        });
        mSubscription.add(subscription);
    }

    @Override
    public boolean isBackOnToolbar() {
        return true;
    }

    @Override
    public void onMovedToForeground() {

    }

    @Override
    public void resetViewAndPerformAction(Runnable action) {
        action.run();
    }
}
