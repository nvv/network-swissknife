package com.nsak.android.fragments;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
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

public class TracerouteFragment extends CommonResultsFragment {

    @Override
    protected void updateToolbar() {
        super.updateToolbar();
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.traceroute);
    }

    @Override
    protected Observable<CommandLineUtils.CommandLineCommandOutput> getCommand() {
        return NetworkUtils.tracerouteCommand(mAddress);
    }

    @Override
    protected void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output) {
        TracerouteData data = (TracerouteData) output.mData;
        if (data != null && data.getLevel() != 0) {
            mAdapter.addItem("#" + data.getLevel() + " " + data.getIpAddress() + " " + data.getHost() + " " + data.getTime());
        } else {
            mAdapter.addItem(output.outputLine);
        }
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

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
