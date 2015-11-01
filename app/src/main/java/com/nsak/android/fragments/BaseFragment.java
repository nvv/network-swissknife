package com.nsak.android.fragments;

import android.app.Fragment;

/**
 * @author Vlad Namashko.
 */
public abstract class BaseFragment extends Fragment {

    public abstract boolean isBackOnToolbar();

    public abstract void onMovedToForeground();

    public abstract void resetViewAndPerformAction(Runnable action);
}
