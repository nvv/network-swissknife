package com.nsak.android.animator;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko.
 */
public abstract class TranslationAplhaAnimator extends RecyclerView.ItemAnimator {

    protected List<RecyclerView.ViewHolder> mViewHolders = new ArrayList<>();

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setAlpha(0.0f);
        return mViewHolders.add(viewHolder);
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    public void endAnimations() {
    }

    @Override
    public boolean isRunning() {
        return !mViewHolders.isEmpty();
    }

}
