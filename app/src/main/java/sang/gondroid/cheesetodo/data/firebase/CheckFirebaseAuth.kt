package sang.gondroid.cheesetodo.data.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.MyState

/**
 * ViewModel에서 메서드를 구현해 호출하면 Singleton이 아니어서, 모든 fragment에서 상태가 변할 때 마다 계속해서 checkMyState()를 호출해야하므로 class 생성
 */
class CheckFirebaseAuth(private val appPreferenceManager: AppPreferenceManager) {

    private val THIS_NAME = this::class.simpleName

    /**
     * 로그인 유무를 파악하기위한 메서드
     */
    fun checkToken(): MyState {
        Log.d(Constants.TAG, "$THIS_NAME checkMyState() called")

        if (!appPreferenceManager.getIdToken().isNullOrEmpty() && !appPreferenceManager.getUserNameString().isNullOrEmpty()) {

            return MyState.Login(appPreferenceManager.getIdToken()!!, appPreferenceManager.getUserNameString()!!)

        } else {

            return MyState.Success.NotRegistered
        }
    }
}