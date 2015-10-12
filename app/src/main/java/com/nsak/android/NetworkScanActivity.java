package com.nsak.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.nsak.android.adapters.HostsAdapter;
import com.nsak.android.event.HostSelectedEvent;
import com.nsak.android.event.NetworkHostScannedEvent;
import com.nsak.android.event.NetworkInfoDiscoveredEvent;
import com.nsak.android.fragments.PortScanFragment;
import com.nsak.android.network.Host;
import com.nsak.android.network.NetworkScanner;
import com.nsak.android.network.wifi.WifiInfo;
import com.nsak.android.network.wifi.WifiManager;
import com.nsak.android.ui.widget.DividerItemDecoration;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Vlad Namashko.
 */
public class NetworkScanActivity extends BaseActivity {

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
        mAdapter = new HostsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        try {
            WifiManager manager = App.sInstance.getWifiManager();
            WifiInfo wifiInfo = new WifiInfo(manager.getDhcpInfo(), manager.getConnectionInfo());

            networkSsid.setText(wifiInfo.getSsid());

            mNetworkScanner = new NetworkScanner();
            mScanNetworkSubscription.add(mNetworkScanner.scanNetwork(wifiInfo).
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

                            scannedHosts.setText(mScannedHost + "/" + mTotalHostCount + " (" + (100 * mScannedHost / mTotalHostCount) + "%)");
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(final NetworkInfoDiscoveredEvent event) {
        mTotalHostCount = event.hostsCount;
    }

    public void onEvent(HostSelectedEvent event) {
        stopScan();
        Intent intent = new Intent(this, CommonResultsActivity.class);
        intent.putExtra(Constants.EXTRA_HOST_TO_SCAN, event.host.ipAddress);
        startActivity(intent);
    }

}
