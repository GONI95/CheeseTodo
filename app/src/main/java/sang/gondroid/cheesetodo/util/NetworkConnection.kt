package sang.gondroid.cheesetodo.util

import android.content.Context
import android.net.*
import androidx.lifecycle.LiveData
import java.lang.Exception

/**
 * 퍼미션 요청 : <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *
 * ConnectivityManager.CONNECTIVITY_ACTION 인텐트를 받아 네트워크 연결 상태 정보를 확인할 수 있지만,
 * Android 7(Nougat) 이상의 버전을 타겟팅하는 앱은 이 인텐트를 받을 수 없습니다.
 *
 * BroadcastReceiver로 네트워크 연결 정보를 받을 수 없고, registerNetworkCallback() API 리스너를 등록하여 받을 수 있도록 변경되었습니다.
 */
class NetworkConnection(private val context: Context) : LiveData<Boolean>() {

    private val THIS_NAME = this::class.simpleName


    /**
     * Gon : 네트워크 연결 상태와 유형을 확인할 수 있습니다.
     *       [update - 21.11.29]
     */
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Gon : NetworkRequest 클래스는 네트워크 상태가 변경되었을 때 콜백을 호출합니다.
     *       [update - 21.11.29]
     */
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    /**
     * Gon : LiveData 객체에 활성 상태의 관찰자가 있는 경우 호출됩니다.
     *       관찰자가 있는 경우 네트워크 상태 변경을 수신할 수 있도록 콜백을 등록합니다.
     *       [update - 21.11.29]
     */
    override fun onActive() {
        super.onActive()
        updateConnection()

        try {
            LogUtil.d(Constants.TAG, "$THIS_NAME onActive() registerDefaultNetworkCallback 등록")
            connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())

        } catch (e : Exception) {
            LogUtil.d(Constants.TAG, "$THIS_NAME onActive() $e")
        }
    }

    /**
     * Gon : LiveData 객체에 활성 상태의 관찰자가 없는 경우 호출됩니다.
     *       관찰자가 있는 경우 네트워크 상태 변경을 수신하는 콜백을 해제합니다.
     *       [update - 21.11.29]
     */
    override fun onInactive() {
        super.onInactive()

        try {
            LogUtil.d(Constants.TAG, "$THIS_NAME onInactive() try")
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback())
        } catch (e : Exception) {
            LogUtil.d(Constants.TAG, "$THIS_NAME onInactive() $e")
        }
    }

    /**
     * Gon : 콜백 등록, 해제를 위한 NetworkCallback 객체를 반환하는 메서드 입니다.
     *       onLost() : 네트워크 연결이 끊어진 경우 호출됩니다.
     *       onAvailable() : 네트워크 연결될 때 호출됩니다.
     *       [update - 21.11.29]
     */
    private fun connectivityManagerCallback() : ConnectivityManager.NetworkCallback {
        networkCallback = object : ConnectivityManager.NetworkCallback()
        {
            override fun onLost(network: Network)
            {
                LogUtil.d(Constants.TAG, "$THIS_NAME connectivityManagerCallback() onLost")
                super.onLost(network)
                postValue(false)
            }

            override fun onAvailable(network: Network)
            {
                LogUtil.d(Constants.TAG, "$THIS_NAME connectivityManagerCallback() onAvailable")
                super.onAvailable(network)
                postValue(true)
            }
        }
        return networkCallback
    }

    /**
     * Gon : connectivityManager로 부터 현재 네트워크 연결 상태를 읽어오는 메서드
     *       [update - 21.11.30]
     */
    private fun updateConnection() {
        LogUtil.d(Constants.TAG, "$THIS_NAME updateConnection() called")


        val networkCapabilities = connectivityManager.activeNetwork
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities)

        activeNetwork?.let {
            postValue(when {
                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            })
        } ?:  postValue(false)
    }

}