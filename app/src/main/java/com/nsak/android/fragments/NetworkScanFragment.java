package com.nsak.android.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.App;
import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.adapters.HostsAdapter;
import com.nsak.android.db.HostDbAdapter;
import com.nsak.android.db.NetworkDbAdapter;
import com.nsak.android.event.HostSelectedEvent;
import com.nsak.android.event.NetworkInfoDiscoveredEvent;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;
import com.nsak.android.network.Host;
import com.nsak.android.network.NetworkScanner;
import com.nsak.android.network.wifi.WifiInfo;
import com.nsak.android.network.wifi.WifiManager;
import com.nsak.android.ui.widget.DividerItemDecoration;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Vlad Namashko.
 */
public class NetworkScanFragment extends BaseFragment {

    public static final int MSG_NETWORK_DISCOVERED = 0;
    public static final int MSG_HOST_SELECTED = 1;

    @InjectView(R.id.hosts_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.network_ssid)
    TextView mNetworkSsid;
    @InjectView(R.id.scanned_hosts)
    TextView mScannedHosts;

    private HostsAdapter mAdapter;
    private NetworkScanner mNetworkScanner;

    private int mTotalHostCount;
    private int mScannedHost;

    private String mSsid;

    private CompositeSubscription mScanNetworkSubscription;

    private List<Host> mReacheableHosts = new LinkedList<>();
    private long mCurrentNetworkId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network_scan, container, false);
        mScanNetworkSubscription = new CompositeSubscription();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateToolbar();
        initView();
    }

    private void initView() {
        ButterKnife.inject(this, getActivity().findViewById(android.R.id.content));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new HostsAdapter(mIncomingHandler);
        mRecyclerView.setAdapter(mAdapter);

        try {
            WifiManager manager = App.sInstance.getWifiManager();
            final WifiInfo wifiInfo = new WifiInfo(manager.getDhcpInfo(), manager.getConnectionInfo());

            mSsid = wifiInfo.getSsid();
            mNetworkSsid.setText(mSsid);

            mNetworkScanner = new NetworkScanner();

            mCurrentNetworkId = NetworkDbAdapter.saveNetwork(wifiInfo);
            mAdapter.setItems(HostDbAdapter.getHosts(mCurrentNetworkId, wifiInfo));

            mScanNetworkSubscription.add(mNetworkScanner.scanNetwork(wifiInfo, mIncomingHandler).
                    onBackpressureBuffer().
                    subscribeOn(Schedulers.computation()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<Host>() {
                        @Override
                        public void onCompleted() {
                            HostDbAdapter.saveHosts(mCurrentNetworkId, mReacheableHosts);
                            mScanNetworkSubscription.unsubscribe();
                            mScannedHosts.setText(getString(R.string.network_scanned));
                        }

                        @Override
                        public void onError(Throwable e) {
                            mScanNetworkSubscription.unsubscribe();
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Host host) {
                            mScannedHost++;
                            if (host.isReachable) {
                                mAdapter.addItem(host);
                                mReacheableHosts.add(host);
                            }

                            int per = 0;
                            if (mTotalHostCount != 0) {
                                per = 100 * mScannedHost / mTotalHostCount;
                            }
                            mScannedHosts.setText(mScannedHost + "/" + mTotalHostCount + " (" + per + "%)");
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScan();
    }

    private void stopScan() {
        mScanNetworkSubscription.unsubscribe();
        if (mNetworkScanner != null) {
            mNetworkScanner.destroy();
        }
        HostDbAdapter.saveHosts(mCurrentNetworkId, mReacheableHosts);
    }

    private Handler mIncomingHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NETWORK_DISCOVERED:
                    mTotalHostCount = ((NetworkInfoDiscoveredEvent) msg.obj).hostsCount;
                    break;
                case MSG_HOST_SELECTED:
                    stopScan();
                    HostSelectedEvent event = (HostSelectedEvent) msg.obj;
                    HostDetailsFragment fragment = new HostDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(NetworkScanActivity.ARG_SELECTED_HOST, event.host);
                    bundle.putInt(NetworkScanActivity.ARG_SELECTED_ITEM_TOP, event.sharedView.getTop());
                    bundle.putInt(NetworkScanActivity.ARG_SELECTED_ITEM_BOTTOM, event.sharedView.getHeight());
                    fragment.setArguments(bundle);
                    ((NetworkScanActivityInterface) getActivity()).replaceFragment(fragment);
                    break;
            }
        }
    };

    @Override
    public boolean isBackOnToolbar() {
        return false;
    }

    @Override
    public void onMovedToForeground() {
        updateToolbar();
        ButterKnife.inject(this, getActivity().findViewById(android.R.id.content));
        mNetworkSsid.setText(mSsid);
    }

    @Override
    public void resetViewAndPerformAction(Runnable action) {
        action.run();
    }

    private void updateToolbar() {
        if (getActivity() != null) {
            ((NetworkScanActivityInterface) getActivity()).setViewToolbar(
                    LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_network_scan, null));
        }
    }
}
