package com.nsak.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;

/**
 * @author Vlad Namashko.
 */
public class HostDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((NetworkScanActivityInterface) getActivity()).setViewToolbar(R.layout.toolbar_titled, true);
        View view = inflater.inflate(R.layout.host_details, container, false);
        return view;
    }

}
