package com.nsak.android.fragments;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * @author Vlad Namashko.
 */
public abstract class BaseFragment extends Fragment {

    @NonNull
    protected Animator createSwipeRightAnimator(View rootView, boolean enter) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Animator animator = ObjectAnimator.ofObject(rootView, "x", new IntEvaluator(),
                enter ? width : 0, enter ? 0 : width).setDuration(350);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    public abstract boolean isBackOnToolbar();

    public abstract void onMovedToForeground();

    public abstract void resetViewAndPerformAction(Runnable action);
}
