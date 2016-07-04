package com.nsak.android.adapters;

import android.support.v7.widget.RecyclerView;

/**
 * @author Vlad Namashko
 */

public abstract class BaseCommonResultAdapter<T, V> extends RecyclerView.Adapter {

    public abstract void clearItems();

    public abstract void addItem(V v);
}
