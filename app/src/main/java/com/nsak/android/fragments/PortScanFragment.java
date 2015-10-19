package com.nsak.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsak.android.NetworkScanActivity;
import com.nsak.android.adapters.PortsAdapter;
import com.nsak.android.network.Port;
import com.nsak.android.network.db.PortServiceDbAdapter;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.ui.widget.DividerItemDecoration;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Vlad Namashko.
 */
public class PortScanFragment extends Fragment {

    protected RecyclerView mRecyclerView;

    protected PortsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = new RecyclerView(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new PortsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        scanPorts();

        return mRecyclerView;
    }

    private void scanPorts() {
        Observable.from(PortServiceDbAdapter.getPortsForScan()).filter(new Func1<Port, Boolean>() {
            @Override
            public Boolean call(Port port) {
                return NetworkUtils.udpScan(getArguments().getString(NetworkScanActivity.ARG_SELECTED_HOST), port.port, 200);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Port>() {
            @Override
            public void call(Port port) {
                mAdapter.addItem(new Port(port.port, port.service));
            }
        });
    }
}
