package com.nsak.android.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.adapters.CommonResultAdapter;
import com.nsak.android.adapters.IpInfoResultAdapter;
import com.nsak.android.network.data.IpInfoData;
import com.nsak.android.network.utils.NetworkCalculator;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.ui.view.LabeledEditTextLayout;
import com.nsak.android.utils.CommandLineUtils;
import com.nsak.android.utils.Extensions;
import com.nsak.android.utils.TextUtils;

import rx.Observable;

/**
 * @author Vlad Namashko
 */

public class IpCalculatorFragment extends CommonResultsFragment {

    private LabeledEditTextLayout mIpView;
    private LabeledEditTextLayout mMaskView;

    @Override
    protected void updateToolbar() {
        super.updateToolbar();
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.ip_calculator);
    }

    @Override
    protected Observable<CommandLineUtils.CommandLineCommandOutput> getCommand() {
        String mask = mMaskView.getText();
        if (!mask.contains(".")) {
            mask = NetworkCalculator.cidrToMask(Extensions.tryParse(mask, 0));
        }

        return NetworkUtils.ipCalculatorCommand(mIpView.getText(), mask);
    }

    protected void initAdapter() {
        mAdapter = new IpInfoResultAdapter();
    }

    @Override
    protected void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output) {
        IpInfoData data = (IpInfoData) output.mData;
        ((IpInfoResultAdapter) mAdapter).addItem(data);
    }

    @Override
    public void initEditOptions() {
        final ViewGroup content = (ViewGroup) mToolbar.findViewById(R.id.edit_content);
        content.setVisibility(ViewGroup.VISIBLE);
        content.addView(LayoutInflater.from(getActivity()).inflate(R.layout.ip_calculator_edit_content, null));

        mActionSettings.setVisibility(View.VISIBLE);

        View doAction = mToolbar.findViewById(R.id.do_action);

        mIpView = (LabeledEditTextLayout) mToolbar.findViewById(R.id.ip_address);
        mMaskView = (LabeledEditTextLayout) mToolbar.findViewById(R.id.mask);

        doAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = mIpView.getText();
                String mask = mMaskView.getText();
                mAddress = " ";

                mIpView.setError(null);
                mMaskView.setError(null);

                boolean hasError = false;
                if (!NetworkCalculator.isCorrectIp(address)) {
                    mIpView.setError(getString(R.string.ip_address_wrong));
                    hasError = true;
                }

                if (mask.contains(".") && !NetworkCalculator.isCorrectMask(mask)) {
                    mMaskView.setError(getString(R.string.mask_wrong));
                    hasError = true;
                } else if (!mask.contains(".") && (Extensions.tryParse(mask, -1) < 0 || Extensions.tryParse(mask, -1) > 32)) {
                    mMaskView.setError(getString(R.string.mask_wrong));
                    hasError = true;
                }

                if (!hasError) {
                    doResult();
                }
            }
        });
    }
}
