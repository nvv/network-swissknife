package com.nsak.android.network.data;

import android.util.Pair;

import com.nsak.android.utils.CommandLineCommandOutputData;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Vlad Namashko
 */

public class IpInfoData implements CommandLineCommandOutputData {

    private String mName;
    private String mValue;
    private List<Pair<String, String>> mExtraValues;

    public IpInfoData(String name, String value) {
        mName = name;
        mValue = value;
    }

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }

    public void addExtraValue(String name, String value) {
        if (mExtraValues == null) {
            mExtraValues = new LinkedList<>();
        }
        mExtraValues.add(new Pair<>(name, value));
    }

    public List<Pair<String, String>> getAdditionalValue() {
        return mExtraValues;
    }
}
