package com.nsak.android.ui.widget;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.nsak.android.R;
import com.nsak.android.ui.view.LabeledEditTextLayout;
import com.nsak.android.utils.TextUtils;

/**
 * @author Vlad Namashko
 */
public class PingSettingsWindow extends PopupWindow {

    protected View mLayout;

    public PingSettingsWindow(View anchor, View layout) {
        super(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mLayout = layout;
        setContentView(mLayout);
        setOutsideTouchable(true);
        setTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.PopupWindowAnimation);

        final LabeledEditTextLayout packetSize = (LabeledEditTextLayout) mLayout.findViewById(R.id.ping_packet_size);
        final LabeledEditTextLayout count = (LabeledEditTextLayout) mLayout.findViewById(R.id.ping_count);
        final LabeledEditTextLayout timeout = (LabeledEditTextLayout) mLayout.findViewById(R.id.ping_timeout);
        final LabeledEditTextLayout interval = (LabeledEditTextLayout) mLayout.findViewById(R.id.pings_interval);
        final LabeledEditTextLayout ttl = (LabeledEditTextLayout) mLayout.findViewById(R.id.time_to_live);

        mLayout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mLayout.findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
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
            }
        });
    }

    private String getString(int id) {
        return mLayout.getResources().getString(id);
    }

    public class PingSettings {
        public String timeOut;
        public String count;
        public String packetSize;
        public String pingsInterval;
        public String ttl;
    }

    public static interface PingSettingsListener {
        void apply(PingSettings settings);
    }
}
