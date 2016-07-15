package com.nsak.android.fragments;

import android.app.Dialog;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.App;
import com.nsak.android.R;
import com.nsak.android.network.data.PingData;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.ui.view.LabeledEditTextLayout;
import com.nsak.android.ui.widget.PingSettingsWindow;
import com.nsak.android.utils.CommandLineUtils;
import com.nsak.android.utils.Extensions;
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

    private PingSettings mSettings = new PingSettings();
    private ViewGroup mPanelContent;

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

    @Override
    protected void onCommandCompleted() {
        switchViewVisibilityDelayed(mPanelContent);
        mStopCommand.setVisibility(View.GONE);
    }

    public void initEditOptions() {
        mPanelContent = (ViewGroup) mToolbar.findViewById(R.id.edit_content);
        mPanelContent.setVisibility(ViewGroup.VISIBLE);
        mPanelContent.addView(LayoutInflater.from(getActivity()).inflate(R.layout.ping_edit_content, null));

        mActionSettings.setVisibility(View.VISIBLE);

        mAddtionalSettings.setVisibility(View.VISIBLE);

        mAddtionalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.ping_settings_view, null);
                dialog.setContentView(view);
                dialog.show();

                final LabeledEditTextLayout packetSize = (LabeledEditTextLayout) view.findViewById(R.id.ping_packet_size);
                final LabeledEditTextLayout count = (LabeledEditTextLayout) view.findViewById(R.id.ping_count);
                final LabeledEditTextLayout timeout = (LabeledEditTextLayout) view.findViewById(R.id.ping_timeout);
                final LabeledEditTextLayout interval = (LabeledEditTextLayout) view.findViewById(R.id.pings_interval);
                final LabeledEditTextLayout ttl = (LabeledEditTextLayout) view.findViewById(R.id.time_to_live);

                ttl.getTextView().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Integer ttlText = Extensions.tryParse(s.toString(), 0);
                        if (ttlText > 255) {
                            ttl.setText(String.valueOf(255));
                        }
                    }
                });

                packetSize.setText(mSettings.packetSize);
                count.setText(mSettings.count);
                timeout.setText(mSettings.timeOut);
                interval.setText(mSettings.pingsInterval);
                ttl.setText(mSettings.ttl);

                view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                view.findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String packetSizeText = packetSize.getText();
                        String countText = count.getText();
                        String timeoutText = timeout.getText();
                        String intervalText = interval.getText();
                        String ttlText = ttl.getText();

                        boolean validate = true;

                        if (TextUtils.isNullOrEmpty(packetSizeText)) {
                            packetSize.setError(getString(R.string.error_empty_field));
                            validate = false;
                        }

                        if (TextUtils.isNullOrEmpty(timeoutText)) {
                            timeout.setError(getString(R.string.error_empty_field));
                            validate = false;
                        }

                        if (TextUtils.isNullOrEmpty(intervalText)) {
                            interval.setError(getString(R.string.error_empty_field));
                            validate = false;
                        }

                        if (TextUtils.isNullOrEmpty(ttlText)) {
                            ttl.setError(getString(R.string.error_empty_field));
                            validate = false;
                        }

                        if (validate) {
                            mSettings.count = countText;
                            mSettings.packetSize = packetSizeText;
                            mSettings.timeOut = timeoutText;
                            mSettings.ttl = ttlText;
                            mSettings.pingsInterval = intervalText;

                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        View doAction = mToolbar.findViewById(R.id.do_action);

        final LabeledEditTextLayout text = (LabeledEditTextLayout) mToolbar.findViewById(R.id.ping_url);
        text.setText(App.sInstance.getSettings().getLastSelectedHost());

        doAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = text.getText();

                text.setError(null);

                if (TextUtils.isNullOrEmpty(address)) {
                    text.setError(getString(R.string.error_empty_field));
                    return;
                }

                hideKeyboard(text);

                switchViewVisibilityDelayed(mPanelContent);

                mAddress = address;

                mCommandArgs.clear();

                int packetSize = Integer.parseInt(mSettings.packetSize);

                mCommandArgs.add("-s");
                mCommandArgs.add(String.valueOf(packetSize));

                mCommandArgs.add("-W");
                mCommandArgs.add(mSettings.timeOut);

                if (!TextUtils.isNullOrEmpty(mSettings.count)) {
                    mCommandArgs.add("-c");
                    mCommandArgs.add(String.valueOf(Integer.parseInt(mSettings.count) + 1));
                }

                mCommandArgs.add("-i");
                mCommandArgs.add(mSettings.pingsInterval);

                mCommandArgs.add("-t");
                mCommandArgs.add(mSettings.ttl);

                App.sInstance.getSettings().setLastSelectedHost(mAddress);
                mStopCommand.setVisibility(View.VISIBLE);
                doResult();
            }
        });
    }

    public class PingSettings {
        public String timeOut = "2";
        public String count;
        public String packetSize = "64";
        public String pingsInterval = "1";
        public String ttl = "64";
    }

}
