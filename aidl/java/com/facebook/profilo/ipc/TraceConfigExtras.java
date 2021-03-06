// (c) Facebook, Inc. and its affiliates. Confidential and proprietary.

package com.facebook.profilo.ipc;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.facebook.profilo.config.Config;
import com.facebook.profilo.config.ConfigParams;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

// Trace config extra configuration parameters.
// The granularity of these extras applies to everything within the trace
// config, which means that they apply to all the providers.
public final class TraceConfigExtras implements Parcelable {
  public static final TraceConfigExtras EMPTY = new TraceConfigExtras(null, null, null);

  private final @Nullable TreeMap<String, Integer> mIntParams;
  private final @Nullable TreeMap<String, Boolean> mBoolParams;
  private final @Nullable TreeMap<String, int[]> mIntArrayParams;

  private final @Nullable Config mConfig;
  private final int mTraceConfigIdx;

  TraceConfigExtras(Parcel in) {
    mConfig = null;
    mTraceConfigIdx = -1;

    Bundle intParamsBundle = in.readBundle(getClass().getClassLoader());
    Set<String> intParamKeys = intParamsBundle.keySet();
    if (!intParamKeys.isEmpty()) {
      mIntParams = new TreeMap<>();
      for (String key : intParamKeys) {
        mIntParams.put(key, intParamsBundle.getInt(key));
      }
    } else {
      mIntParams = null;
    }

    Bundle boolParamsBundle = in.readBundle(getClass().getClassLoader());
    Set<String> boolParamKeys = boolParamsBundle.keySet();
    if (!boolParamKeys.isEmpty()) {
      mBoolParams = new TreeMap<>();
      for (String key : boolParamKeys) {
        mBoolParams.put(key, boolParamsBundle.getBoolean(key));
      }
    } else {
      mBoolParams = null;
    }

    Bundle intArrayParamsBundle = in.readBundle(getClass().getClassLoader());
    Set<String> intArrayParamKeys = intArrayParamsBundle.keySet();
    if (!intArrayParamKeys.isEmpty()) {
      mIntArrayParams = new TreeMap<>();
      for (String key : intArrayParamKeys) {
        mIntArrayParams.put(key, intArrayParamsBundle.getIntArray(key));
      }
    } else {
      mIntArrayParams = null;
    }
  }

  public TraceConfigExtras(Config config, int traceConfigIdx) {
    mConfig = config;
    mTraceConfigIdx = traceConfigIdx;
    mIntParams = null;
    mIntArrayParams = null;
    mBoolParams = null;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    TreeMap<String, Integer> intParams = mIntParams;
    TreeMap<String, Boolean> boolParams = mBoolParams;
    TreeMap<String, int[]> intArrayParams = mIntArrayParams;

    if (mTraceConfigIdx >= 0 && mConfig != null) {
      ConfigParams params = mConfig.getTraceConfigParams(mTraceConfigIdx);
      intParams = params.intParams;
      boolParams = params.boolParams;
      intArrayParams = params.intListParams;
    }

    Bundle intParamsBundle = new Bundle();
    if (intParams != null) {
      for (TreeMap.Entry<String, Integer> entry : intParams.entrySet()) {
        intParamsBundle.putInt(entry.getKey(), entry.getValue());
      }
    }
    dest.writeBundle(intParamsBundle);
    Bundle boolParamsBundle = new Bundle();
    if (boolParams != null) {
      for (TreeMap.Entry<String, Boolean> entry : boolParams.entrySet()) {
        boolParamsBundle.putBoolean(entry.getKey(), entry.getValue());
      }
    }
    dest.writeBundle(boolParamsBundle);
    Bundle intArrayParamsBundle = new Bundle();
    if (intArrayParams != null) {
      for (TreeMap.Entry<String, int[]> entry : intArrayParams.entrySet()) {
        intArrayParamsBundle.putIntArray(entry.getKey(), entry.getValue());
      }
    }
    dest.writeBundle(intArrayParamsBundle);
  }

  public TraceConfigExtras(
      @Nullable TreeMap<String, Integer> intParams,
      @Nullable TreeMap<String, Boolean> boolParams,
      @Nullable TreeMap<String, int[]> intArrayParams) {
    mIntParams = intParams;
    mBoolParams = boolParams;
    mIntArrayParams = intArrayParams;
    mConfig = null;
    mTraceConfigIdx = -1;
  }

  public int getIntParam(String key, int defaultValue) {
    if (mConfig != null) {
      return mConfig.optTraceConfigParamInt(mTraceConfigIdx, key, defaultValue);
    }
    if (mIntParams == null) {
      return defaultValue;
    }
    Integer value = mIntParams.get(key);
    return value == null ? defaultValue : value;
  }

  public boolean getBoolParam(String key, boolean defaultValue) {
    if (mConfig != null) {
      return mConfig.optTraceConfigParamBool(mTraceConfigIdx, key, defaultValue);
    }
    if (mBoolParams == null) {
      return defaultValue;
    }
    Boolean value = mBoolParams.get(key);
    return value == null ? defaultValue : value;
  }

  @Nullable
  public int[] getIntArrayParam(String key) {
    if (mConfig != null) {
      return mConfig.optTraceConfigParamIntList(mTraceConfigIdx, key);
    }
    if (mIntArrayParams == null) {
      return null;
    }
    return mIntArrayParams.get(key);
  }

  public static final Creator<TraceConfigExtras> CREATOR =
      new Creator<TraceConfigExtras>() {
        public TraceConfigExtras createFromParcel(Parcel in) {
          return new TraceConfigExtras(in);
        }

        public TraceConfigExtras[] newArray(int size) {
          return new TraceConfigExtras[size];
        }
      };

  @Override
  public int describeContents() {
    return 0;
  }
}
