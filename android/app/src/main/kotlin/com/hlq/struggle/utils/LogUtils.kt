package com.hlq.struggle.utils

import android.util.Log
import io.flutter.BuildConfig

/**
 * @author：HLQ_Struggle
 * @date：2020/6/27
 * @desc：
 */
object LogUtils {
    private const val LOG_TAG = "HLQ_Struggle"
    fun logE(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(LOG_TAG, msg)
        }
    }
}