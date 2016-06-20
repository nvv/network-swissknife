package com.nsak.android.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.adapters.CommonResultAdapter;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;
import com.nsak.android.network.Host;
import com.nsak.android.ui.widget.DividerItemDecoration;
import com.nsak.android.utils.CommandLineUtils;

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

    protected View mRootView;
    protected CommonResultAdapter mAdapter;
    protected Host mSelectedHost;

    //protected int mCommand;

    protected View mToolbar;

    @InjectView(R.id.results_view)
    protected RecyclerView mRecyclerView;

    private CompositeSubscription mSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSelectedHost = getArguments().getParcelable(NetworkScanActivity.ARG_SELECTED_HOST);
        mRootView = inflater.inflate(R.layout.fragment_common_results, container, false);
//        mCommand = getArguments().getInt(EXTRA_COMMAND);

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
        mSubscription.unsubscribe();
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        return createSwipeRightAnimator(mRootView, enter);
    }

    protected void updateToolbar() {
        if (getActivity() != null) {
            mToolbar = LayoutInflater.from(getActivity()).inflate(R.layout.common_result_toolbar, null);
            ((NetworkScanActivityInterface) getActivity()).setViewToolbar(mToolbar);
        }
    }

    private void doResult() {
//        Observable<CommandLineUtils.CommandLineCommandOutput> command = mCommand == EXTRA_COMMAND_PING ?
//                NetworkUtils.pingCommand(mSelectedHost.ipAddress) : NetworkUtils.tracerouteCommand(mSelectedHost.ipAddress);
        Subscription subscription = getCommand().observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<CommandLineUtils.CommandLineCommandOutput>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>>>>>>> on completed  ");

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CommandLineUtils.CommandLineCommandOutput output) {
                        System.out.println(">>>>>  " + output.outputLine);
                        onOutputReceived(output);
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

    public static CommonResultsFragment newInstance(int command) {
        switch (command) {
            case EXTRA_COMMAND_PING: default:
                return new PingFragment();
            case EXTRA_COMMAND_TRACEROUTE:
                return new TracerouteFragment();
        }
    }

    protected abstract Observable<CommandLineUtils.CommandLineCommandOutput> getCommand();

    protected abstract void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output);
}
