package com.nsak.android.animation.animator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


/**
 * @author Vlad Namashko
 */
public class HorizonatlTranslationAplhaAnimator extends TranslationAplhaAnimator {

    @Override
    public void runPendingAnimations() {
        if (!mViewHolders.isEmpty()) {
            int animationDuration = 600;
            AnimatorSet animator;
            View target;
            for (final RecyclerView.ViewHolder viewHolder : mViewHolders) {
                target = viewHolder.itemView;
                target.setPivotX(target.getMeasuredWidth() / 2);
                target.setPivotY(target.getMeasuredHeight() / 2);

                animator = new AnimatorSet();

                animator.playTogether(
                        ObjectAnimator.ofFloat(target, "translationX", -target.getMeasuredWidth(), 0.0f),
                        ObjectAnimator.ofFloat(target, "alpha", target.getAlpha(), 1.0f)
                );

                animator.setTarget(target);
                animator.setDuration(animationDuration);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setStartDelay((animationDuration * viewHolder.getPosition()) / 10);

                animator.start();
            }
        }
    }

}

