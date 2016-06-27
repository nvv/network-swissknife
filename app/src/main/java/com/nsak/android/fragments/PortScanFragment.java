package com.nsak.android.fragments;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.adapters.PortsAdapter;
import com.nsak.android.db.PortServiceDbAdapter;
import com.nsak.android.fragments.intf.ActivityInterface;
import com.nsak.android.network.Host;
import com.nsak.android.network.Port;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.ui.widget.DividerItemDecoration;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Vlad Namashko.
 */
public class PortScanFragment extends BaseFragment {

    protected View mRootView;
    protected PortsAdapter mAdapter;
    protected Host mSelectedHost;

    @InjectView(R.id.ports_view)
    protected RecyclerView mRecyclerView;

    protected ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSelectedHost = getArguments().getParcelable(NetworkScanActivity.ARG_SELECTED_HOST);
        mRootView = inflater.inflate(R.layout.fragment_ports_scan, container, false);
        updateToolbar();
        ButterKnife.inject(this, mRootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new PortsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        scanPorts();
        return mRootView;
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        return createSwipeRightAnimator(mRecyclerView, enter);
    }

    private void scanPorts() {
        Observable.from(PortServiceDbAdapter.getPortsForScan()).filter(new Func1<Port, Boolean>() {
            @Override
            public Boolean call(Port port) {
                return NetworkUtils.udpScan(mSelectedHost.ipAddress, port.port, 200);
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Port>() {
            @Override
            public void onCompleted() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Port port) {
                mAdapter.addItem(new Port(port.port, port.service));
            }
        });
    }

    private void updateToolbar() {
        if (getActivity() != null) {
            View toolbar = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_titled, null);
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(mSelectedHost.ipAddress);
            mProgressBar = (ProgressBar) toolbar.findViewById(R.id.progress);
            mProgressBar.setVisibility(View.VISIBLE);

            mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

            ((ActivityInterface) getActivity()).setViewToolbar(toolbar);
        }
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
