package com.nsak.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.network.db.VendorDbAdapter;
import com.nsak.android.ui.widget.DividerItemDecoration;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WifiAnlayzerActivity extends BaseDrawerActivity {

    private static int MSG_SCAN = 1980;
    @InjectView(R.id.list)
    RecyclerView mRecyclerView;
    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiReciever;
    private List<ScanResult> mWifiScanList;
    private ScanResultAdapter mAdapter;

    private boolean mIsScaning;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SCAN && mIsScaning == false) {
                startScan();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_anlayzer);

        initView();

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiReciever = new WifiScanReceiver();
        startScan();

    }

    private void startScan() {
        if (!mIsScaning) {
            mIsScaning = true;
            mWifiManager.startScan();
        }
    }

    private void initView() {
        ButterKnife.inject(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ScanResultAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    protected void onPause() {
        mHandler.removeMessages(MSG_SCAN);
        unregisterReceiver(mWifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(mWifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        startScan();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wifi_anlayzer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class WifiScanReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            mIsScaning = false;
            mWifiScanList = mWifiManager.getScanResults();
            if (!isFinishing()) {
                mAdapter.notifyDataSetChanged();
                mHandler.sendEmptyMessageDelayed(MSG_SCAN, 2500);
            }
        }
    }

    class ScanResultViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.ssid)
        TextView ssid;
        @InjectView(R.id.bssid)
        TextView bssid;
        @InjectView(R.id.freq)
        TextView freq;
        @InjectView(R.id.level)
        TextView level;
        @InjectView(R.id.manuf)
        TextView manuf;

        public ScanResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(ScanResult scanResult) {
            ssid.setText(scanResult.SSID);
            bssid.setText(scanResult.BSSID);
            freq.setText(String.format("%dMHz / CH: %d", scanResult.frequency, convertFrequencyToChannel(scanResult.frequency)));
            level.setText(String.format("%ddB / Level: %d", scanResult.level, WifiManager.calculateSignalLevel(scanResult.level, 10)));
            manuf.setText(VendorDbAdapter.getVendor(Integer.parseInt(scanResult.BSSID.substring(0, 8).replace(":", ""), 16)));
        }

        public int convertFrequencyToChannel(int freq) {
            if (freq >= 2412 && freq <= 2484) {
                return (freq - 2412) / 5 + 1;
            } else if (freq >= 5170 && freq <= 5825) {
                return (freq - 5170) / 5 + 34;
            } else {
                return -1;
            }
        }
    }

    class ScanResultAdapter extends RecyclerView.Adapter<ScanResultViewHolder> {
        @Override
        public ScanResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_scan_result, parent, false);
            return new ScanResultViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ScanResultViewHolder holder, int position) {
            ScanResult scanResult = mWifiScanList.get(position);
            holder.bind(scanResult);
        }

        @Override
        public int getItemCount() {
            return mWifiScanList != null ? mWifiScanList.size() : 0;
        }
    }


}
