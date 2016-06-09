package com.nsak.android.fragments;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Bundle;
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
import com.nsak.android.adapters.PortsAdapter;
import com.nsak.android.db.PortServiceDbAdapter;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;
import com.nsak.android.network.Host;
import com.nsak.android.network.Port;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.ui.widget.DividerItemDecoration;
import com.nsak.android.ui.widget.PopupActionWindow;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

    protected View mProgressBar;

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
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Animator animator = ObjectAnimator.ofObject(mRecyclerView, "x", new IntEvaluator(),
                enter ? width : 0, enter ? 0 : width).setDuration(350);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
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
            mProgressBar = toolbar.findViewById(R.id.progress);
            mProgressBar.setVisibility(View.VISIBLE);
            /*
            final View settings = toolbar.findViewById(R.id.icon_settings);
            settings.setVisibility(View.VISIBLE);
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.host_actions, null);
                    PopupActionWindow popupActionWindow = new PopupActionWindow(settings, view);
                    popupActionWindow.handleActionClick(R.id.scan_ports, new Runnable() {
                        @Override
                        public void run() {
                            final PortScanFragment fragment = new PortScanFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(NetworkScanActivity.ARG_SELECTED_HOST, mSelectedHost);
                            fragment.setArguments(bundle);
                            ((NetworkScanActivityInterface) getActivity()).replaceFragment(fragment);
                        }
                    });
                    popupActionWindow.handleActionClick(R.id.ping_host, null);
                    popupActionWindow.handleActionClick(R.id.traceroute, null);
                    popupActionWindow.show();
                }
            });
            */
            ((NetworkScanActivityInterface) getActivity()).setViewToolbar(toolbar);
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
