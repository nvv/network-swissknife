package com.nsak.android.fragments;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.network.data.PingData;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.ui.view.LabeledEditTextLayout;
import com.nsak.android.utils.CommandLineUtils;
import com.nsak.android.utils.GlobalConfiguration;
import com.nsak.android.utils.TextUtils;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;

/**
 * @author Vlad Namashko
 */

public class PingFragment extends CommonResultsFragment {

    private List<String> mCommandArgs = new LinkedList<>();

    @Override
    protected void updateToolbar() {
        super.updateToolbar();
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.ping_host);
    }

    @Override
    protected Observable<CommandLineUtils.CommandLineCommandOutput> getCommand() {
        return NetworkUtils.pingCommand(mAddress, mCommandArgs.toArray(new String[mCommandArgs.size()]));
    }

    @Override
    protected void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output) {
        //PingData data = (PingData) output.mData;
        //if (data != null) {
        //    mAdapter.addItem(data.getTime() + " " + data.getSize());
        //} else {
        mAdapter.addItem(output.outputLine);
        //}
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

    public void initEditOptions() {
        final ViewGroup content = (ViewGroup) mToolbar.findViewById(R.id.edit_content);
        content.setVisibility(ViewGroup.VISIBLE);
        content.addView(LayoutInflater.from(getActivity()).inflate(R.layout.ping_edit_content, null));

        mActionSettings.setVisibility(View.VISIBLE);

        View doAction = mToolbar.findViewById(R.id.do_action);

        final LabeledEditTextLayout text = (LabeledEditTextLayout) mToolbar.findViewById(R.id.ping_url);
        final LabeledEditTextLayout packetSize = (LabeledEditTextLayout) mToolbar.findViewById(R.id.ping_packet_size);
        final LabeledEditTextLayout count = (LabeledEditTextLayout) mToolbar.findViewById(R.id.ping_count);
        final LabeledEditTextLayout timeout = (LabeledEditTextLayout) mToolbar.findViewById(R.id.ping_timeout);

        doAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = text.getText();
                String packetSizeText = packetSize.getText();
                String timeoutText = timeout.getText();

                text.setError(null);
                packetSize.setError(null);
                timeout.setError(null);

                if (TextUtils.isNullOrEmpty(address)) {
                    text.setError(getString(R.string.error_empty_field));
                    return;
                }

                if (TextUtils.isNullOrEmpty(packetSizeText)) {
                    packetSize.setError(getString(R.string.error_empty_field));
                    return;
                }

                if (TextUtils.isNullOrEmpty(timeoutText)) {
                    timeout.setError(getString(R.string.error_empty_field));
                    return;
                }

                hideKeyboard(text);

                switchViewVisibilityDelayed(content);

                mAddress = address;

                mCommandArgs.clear();

                int packetSize = Integer.parseInt(packetSizeText);
                if (packetSize > 64) {
                    packetSize = 64;
                }

                mCommandArgs.add("-s");
                mCommandArgs.add(String.valueOf(packetSize));

                mCommandArgs.add("-W");
                mCommandArgs.add(timeoutText);

                if (!TextUtils.isNullOrEmpty(count.getText())) {
                    mCommandArgs.add("-c");
                    mCommandArgs.add(String.valueOf(Integer.parseInt(count.getText()) + 1));
                }

                doResult();
            }
        });
    }

}
