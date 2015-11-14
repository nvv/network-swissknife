package com.nsak.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.animation.evaluator.ViewBottomEvaluator;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;
import com.nsak.android.network.Gateway;
import com.nsak.android.network.Host;
import com.nsak.android.utils.TextUtils;
import com.transitionseverywhere.Scene;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionInflater;
import com.transitionseverywhere.TransitionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.nsak.android.NetworkScanActivity.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * @author Vlad Namashko.
 */
public class HostDetailsFragment extends BaseFragment {

    private View mRootView;
    private Host mSelectedHost;

    // in scenes
    private Scene mInitScene;
    private Scene mInitIntermediateScene;
    private Scene mHostDetailsScene;

    // out scenes
    private Scene mOutIntermediateScene;

    private Transition mChangeBoundsTransition;
    private Transition mFadeTransition;
    private Transition mOutTransition;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.US);

    @Optional @InjectView(R.id.host_ip) TextView hostIp;
    @Optional @InjectView(R.id.host_name) TextView hostName;
    @Optional @InjectView(R.id.host_mac) TextView hostMac;
    @Optional @InjectView(R.id.host_vendor) TextView hostVendor;
    @Optional @InjectView(R.id.host_name_label) TextView hostNameLabel;
    @Optional @InjectView(R.id.netbios_name) TextView netBiosName;
    @Optional @InjectView(R.id.first_discovered) TextView firstDiscovered;
    @Optional @InjectView(R.id.last_seen) TextView lastSeen;
    @Optional @InjectView(R.id.netbios_name_label) TextView netBiosNameLabel;
    @Optional @InjectView(R.id.network_info_label) TextView networkInfoLabel;
    @Optional @InjectView(R.id.device_info_label) TextView deviceInfoLabel;
    @Optional @InjectView(R.id.general_info_label) TextView generalInfoLabel;
    @Optional @InjectView(R.id.wifi_info_label) TextView wifiInfoLabel;
    @Optional @InjectView(R.id.network_info_section_labels) View networkInfoSectionLabels;
    @Optional @InjectView(R.id.device_info_section_labels) View deviceInfoSectionLabels;
    @Optional @InjectView(R.id.general_info_section) View generalInfoSection;
    @Optional @InjectView(R.id.wifi_info_section) View wifiInfoSection;
    @Optional @InjectView(R.id.status) TextView status;
    @Optional @InjectView(R.id.host_type) TextView type;
    @Optional @InjectView(R.id.wifi_network_ssid) TextView ssid;
    @Optional @InjectView(R.id.wifi_network_bssid) TextView bssid;
    @Optional @InjectView(R.id.wifi_network_gateway) TextView gatewayView;
    @Optional @InjectView(R.id.wifi_network_dns) TextView dns;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSelectedHost = getArguments().getParcelable(NetworkScanActivity.ARG_SELECTED_HOST);
        mRootView = inflater.inflate(R.layout.host_details_fragment, container, false);

        mChangeBoundsTransition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_bounds_transform_transition_500);
        mFadeTransition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.fade_transition_250);
        mOutTransition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.change_transform_fade_transition_500);

        mInitScene = Scene.getSceneForLayout((ViewGroup) mRootView,
                R.layout.network_host,
                getActivity());

        mInitIntermediateScene = Scene.getSceneForLayout((ViewGroup) mRootView,
                R.layout.host_details_intermediate_state,
                getActivity());

        mHostDetailsScene = Scene.getSceneForLayout((ViewGroup) mRootView,
                R.layout.host_details,
                getActivity());

        mOutIntermediateScene = Scene.getSceneForLayout((ViewGroup) mRootView,
                R.layout.host_details_intermediate_state_out,
                getActivity());

        mInitScene.enter();

        initView();
        updateToolbar();
        return mRootView;
    }

    private void initView() {
        ButterKnife.inject(this, mRootView);
        hostIp.setText(mSelectedHost.ipAddress);
        hostName.setText(mSelectedHost.getName());
        hostMac.setText(mSelectedHost.macAddress);
        hostVendor.setText(mSelectedHost.nicVendor);
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        int itemTop = getArg(ARG_SELECTED_ITEM_TOP);
        int itemBottom = getArg(ARG_SELECTED_ITEM_BOTTOM);

        Animator heightAnim = ValueAnimator.ofObject(new ViewBottomEvaluator(mRootView), enter ? itemBottom: height, enter ? height : itemBottom);
        Animator topAnim = ObjectAnimator.ofObject(mRootView, "y", new IntEvaluator(), enter ? itemTop : 0, enter ? 0 : itemTop);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(heightAnim, topAnim);
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());

        if (enter) {
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    doAfterTransitionEnd(mFadeTransition, new Runnable() {
                        @Override
                        public void run() {
                            doAfterTransitionEnd(mChangeBoundsTransition, new Runnable() {
                                @Override
                                public void run() {
                                    // need to be done in post to ensure that scene transition is finished.
                                    mRootView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            networkInfoSectionLabels.setVisibility(View.VISIBLE);
                                            deviceInfoSectionLabels.setVisibility(View.VISIBLE);

                                            hostName.setText(mSelectedHost.hostname);
                                            if (TextUtils.isNullOrEmpty(mSelectedHost.hostname)) {
                                                hostNameLabel.setVisibility(View.GONE);
                                                hostName.setVisibility(View.GONE);
                                            }
                                            netBiosName.setText(mSelectedHost.netBiosName);
                                            if (TextUtils.isNullOrEmpty(mSelectedHost.netBiosName)) {
                                                netBiosNameLabel.setVisibility(View.GONE);
                                                netBiosName.setVisibility(View.GONE);
                                                netBiosNameLabel.invalidate();
                                            }

                                            firstDiscovered.setText(mDateFormat.format(new Date(mSelectedHost.firstDiscovered)));
                                            lastSeen.setText(mDateFormat.format(new Date(mSelectedHost.lastSeen)));

                                            status.setText(mSelectedHost.isReachable ? R.string.status_up : R.string.status_down);

                                            type.setText(mSelectedHost.deviceType == Host.TYPE_GATEWAY ? R.string.gateway : R.string.device);

                                            if (mSelectedHost.deviceType == Host.TYPE_GATEWAY) {
                                                wifiInfoLabel.setVisibility(View.VISIBLE);
                                                wifiInfoSection.setVisibility(View.VISIBLE);

                                                Gateway gateway = (Gateway) mSelectedHost;
                                                ssid.setText(gateway.ssid);
                                                bssid.setText(gateway.bssid);
                                                gatewayView.setText(gateway.gateway);
                                                dns.setText(gateway.dns);
                                            }

                                            networkInfoLabel.setVisibility(View.VISIBLE);
                                            deviceInfoLabel.setVisibility(View.VISIBLE);

                                            generalInfoSection.setVisibility(View.VISIBLE);
                                            generalInfoLabel.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            });
                            TransitionManager.go(mHostDetailsScene, mChangeBoundsTransition);
                            initView();
                        }
                    });
                    TransitionManager.go(mInitIntermediateScene, mFadeTransition);
                    initView();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        return set;
    }

    private int getArg(String name) {
        return getArguments().getInt(name);
    }

    @Override
    public boolean isBackOnToolbar() {
        return true;
    }

    @Override
    public void onMovedToForeground() {
        updateToolbar();
    }

    private void updateToolbar() {
        if (getActivity() != null) {
            View toolbar = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_titled, null);
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(mSelectedHost.ipAddress);
            ((NetworkScanActivityInterface) getActivity()).setViewToolbar(toolbar);
        }
    }

    @Override
    public void resetViewAndPerformAction(final Runnable action) {
        doAfterTransitionEnd(mOutTransition, new Runnable() {
            @Override
            public void run() {
                doAfterTransitionEnd(mChangeBoundsTransition, new Runnable() {
                    @Override
                    public void run() {
                        action.run();
                    }
                });
                TransitionManager.go(mInitScene, mChangeBoundsTransition);
                initView();
            }
        });
        TransitionManager.go(mOutIntermediateScene, mOutTransition);
        initView();
    }

    private void doAfterTransitionEnd(Transition transition, final Runnable action) {
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                action.run();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    };
}
