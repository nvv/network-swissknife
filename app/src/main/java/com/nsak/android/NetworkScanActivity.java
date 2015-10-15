package com.nsak.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.nsak.android.adapters.HostsAdapter;
import com.nsak.android.event.HostSelectedEvent;
import com.nsak.android.event.NetworkInfoDiscoveredEvent;
import com.nsak.android.network.Host;
import com.nsak.android.network.NetworkScanner;
import com.nsak.android.network.wifi.WifiInfo;
import com.nsak.android.network.wifi.WifiManager;
import com.nsak.android.ui.widget.DividerItemDecoration;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Vlad Namashko.
 */
public class NetworkScanActivity extends BaseActivity {

    public static final int MSG_NETWORK_DISCOVERED = 0;
    public static final int MSG_HOST_SELECTED = 1;

    @InjectView(R.id.hosts_view)
    RecyclerView mRecyclerView;

    @InjectView(R.id.network_ssid)
    TextView networkSsid;
    @InjectView(R.id.scanned_hosts)
    TextView scannedHosts;

    private HostsAdapter mAdapter;
    private NetworkScanner mNetworkScanner;

    private int mTotalHostCount;
    private int mScannedHost;

    private CompositeSubscription mScanNetworkSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_scan);
        setupToolbarAndDrawer();
        mScanNetworkSubscription = new CompositeSubscription();
        initView();
    }

    private void initView() {
        ButterKnife.inject(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new HostsAdapter(mIncomingHandler);
        mRecyclerView.setAdapter(mAdapter);

        try {
            WifiManager manager = App.sInstance.getWifiManager();
            WifiInfo wifiInfo = new WifiInfo(manager.getDhcpInfo(), manager.getConnectionInfo());

            networkSsid.setText(wifiInfo.getSsid());

            mNetworkScanner = new NetworkScanner();
            mScanNetworkSubscription.add(mNetworkScanner.scanNetwork(wifiInfo, mIncomingHandler).
                    subscribeOn(Schedulers.computation()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<Host>() {
                        @Override
                        public void onCompleted() {
                            mScanNetworkSubscription.unsubscribe();
                            scannedHosts.setText("Scanned");
                        }

                        @Override
                        public void onError(Throwable e) {
                            mScanNetworkSubscription.unsubscribe();
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Host host) {
                            mScannedHost++;
                            if (host.isReacheble) {
                                mAdapter.addItem(host);
                            }

                            int per = 0;
                            if (mTotalHostCount != 0) {
                                per = 100 * mScannedHost / mTotalHostCount;
                            }
                            scannedHosts.setText(mScannedHost + "/" + mTotalHostCount + " (" + per + "%)");
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
        mNetworkScanner.destroy();
    }

    private Handler mIncomingHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NETWORK_DISCOVERED:
                    mTotalHostCount = ((NetworkInfoDiscoveredEvent) msg.obj).hostsCount;
                    break;
                case MSG_HOST_SELECTED:
                    stopScan();
                    Intent intent = new Intent(NetworkScanActivity.this, CommonResultsActivity.class);
                    intent.putExtra(Constants.EXTRA_HOST_TO_SCAN, ((HostSelectedEvent) msg.obj).host.ipAddress);
                    startActivity(intent);
                    break;
            }
        }
    };
}
