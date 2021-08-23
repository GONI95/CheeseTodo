package sang.gondroid.cheesetodo.util

import android.content.Context
import android.util.Log
import sang.gondroid.cheesetodo.BuildConfig

object LogUtil {

    /** Log Level Error  */
    fun e(TAG: String?, message: String?) {
        if (BuildConfig.DEBUG) Log.e(TAG, message!!)
    }

    /** Log Level Warning  */
    fun w(TAG: String?, message: String?) {
        if (BuildConfig.DEBUG) Log.w(TAG, message!!)
    }

    /** Log Level Information  */
    fun i(TAG: String?, message: String?) {
        if (BuildConfig.DEBUG) Log.i(TAG, message!!)
    }

    /** Log Level Debug  */
    fun d(TAG: String?, message: String?) {
        if (BuildConfig.DEBUG) Log.d(TAG, message!!)
    }

    /** Log Level Verbose  */
    fun v(TAG: String?, message: String?) {
        if (BuildConfig.DEBUG) Log.v(TAG, message!!)
    }
}