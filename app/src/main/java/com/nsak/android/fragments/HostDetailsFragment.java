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
import com.nsak.android.network.Host;
import com.transitionseverywhere.Scene;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionInflater;
import com.transitionseverywhere.TransitionManager;

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
    private Scene mInitScene;
    private Scene mHostDetailsScene;
    private Transition mSceneTransition;

    @InjectView(R.id.host_ip)
    TextView hostIp;
    @InjectView(R.id.host_name)
    TextView hostName;
    @InjectView(R.id.host_mac)
    TextView hostMac;
    @InjectView(R.id.host_vendor)
    TextView hostVendor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSelectedHost = getArguments().getParcelable(NetworkScanActivity.ARG_SELECTED_HOST);
        mRootView = inflater.inflate(R.layout.host_details_fragment, container, false);

        mSceneTransition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.host_details_init_interpolator);

        mInitScene = Scene.getSceneForLayout((ViewGroup) mRootView,
                R.layout.network_host,
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

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (enter) {
                    mHostDetailsScene = Scene.getSceneForLayout((ViewGroup) mRootView,
                            R.layout.host_details,
                            getActivity());

                    TransitionManager.go(mHostDetailsScene, mSceneTransition);

                    initView();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

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

        mSceneTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
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
        TransitionManager.go(mInitScene, mSceneTransition);
        initView();
    }
}
