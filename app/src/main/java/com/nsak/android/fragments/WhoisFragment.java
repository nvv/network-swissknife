package com.nsak.android.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.App;
import com.nsak.android.R;
import com.nsak.android.network.data.TracerouteData;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.ui.view.LabeledEditTextLayout;
import com.nsak.android.utils.CommandLineUtils;
import com.nsak.android.utils.TextUtils;

import rx.Observable;

/**
 * @author Vlad Namashko
 */

public class WhoisFragment extends CommonResultsFragment {

    @Override
    protected void updateToolbar() {
        super.updateToolbar();
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.title_whois);
    }

    @Override
    protected Observable<CommandLineUtils.CommandLineCommandOutput> getCommand() {
        return NetworkUtils.whoisCommand(mAddress);
    }

    @Override
    protected void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output) {
        /*
        String[] data = output.outputLine.split("\n");
        for (String item : data) {
            mAdapter.addItem(item);
        }
        */
        mAdapter.addItem(output.outputLine);
    }

    @Override
    public void initEditOptions() {
        final ViewGroup content = (ViewGroup) mToolbar.findViewById(R.id.edit_content);
        content.setVisibility(ViewGroup.VISIBLE);
        content.addView(LayoutInflater.from(getActivity()).inflate(R.layout.tracerout_edit_content, null));

        mActionSettings.setVisibility(View.VISIBLE);
        View doAction = mToolbar.findViewById(R.id.do_action);

        final LabeledEditTextLayout text = (LabeledEditTextLayout) mToolbar.findViewById(R.id.traceroute_url);

        text.setText(App.sInstance.getSettings().getLastSelectedHost());

        doAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = text.getText();

                if (TextUtils.isNullOrEmpty(address)) {
                    text.setError(getString(R.string.error_empty_field));
                    return;
                }

                hideKeyboard(text);

                switchViewVisibilityDelayed(content);

                text.setError(null);
                mAddress = address;

                App.sInstance.getSettings().setLastSelectedHost(mAddress);
                doResult();
            }
        });
    }
}
