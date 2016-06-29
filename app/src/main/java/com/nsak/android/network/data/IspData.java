package com.nsak.android.network.data;

import com.nsak.android.App;
import com.nsak.android.R;
import com.nsak.android.utils.CommandLineCommandOutputData;
import com.nsak.android.utils.TextUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Vlad Namashko
 */

public class IspData implements CommandLineCommandOutputData {

    private static final Map<String, String> sKeyMap;

    static {
        sKeyMap = new HashMap<>();
        sKeyMap.put("ip", getString(R.string.ip_address));
        sKeyMap.put("hostname", getString(R.string.hostname));
        sKeyMap.put("city", getString(R.string.city));
        sKeyMap.put("region", getString(R.string.region));
        sKeyMap.put("country", getString(R.string.country));
        sKeyMap.put("loc", getString(R.string.location));
        sKeyMap.put("org", getString(R.string.organization));
        sKeyMap.put("postal", getString(R.string.postal_code));
        sKeyMap.put("phone", getString(R.string.phone));
    }

    private String mKey;
    private String mKeyReadable;
    private String mValue;

    public IspData(String key, String value) {
        mKey = key;
        mValue = value;

        if (sKeyMap.containsKey(mKey)) {
            mKeyReadable = sKeyMap.get(mKey);
        }

        if (mKey.equals("country")) {
            Locale locale = new Locale("", mValue);
            String displayCountry = locale.getDisplayCountry();
            if (!TextUtils.isNullOrEmpty(displayCountry)) {
                mValue = displayCountry;
            }
        }
    }

    private static String getString(int id) {
        return App.sInstance.getString(id);
    }


    public String getValue() {
        return mValue;
    }

    public String getKey() {
        return mKeyReadable != null ? mKeyReadable : mKey;
    }
}
