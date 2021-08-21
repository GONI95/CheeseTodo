package sang.gondroid.cheesetodo.data.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.MyState
import sang.gondroid.cheesetodo.util.MyState.*
import java.lang.Exception

/**
 * ViewModel에서 메서드를 구현해 호출하면 Singleton이 아니어서, 모든 fragment에서 상태가 변할 때 마다 계속해서 checkMyState()를 호출해야하므로 class 생성
 */
class CheckFirebaseAuth(private val appPreferenceManager: AppPreferenceManager, private val firebaseAuth: FirebaseAuth) {

    private val THIS_NAME = this::class.simpleName
    private val firebaseUser by lazy { firebaseAuth.currentUser }

    /**
     * 로그인 유무를 파악하기위한 메서드 : Login, NotRegistered 반환
     *
     * Firebase Authentication에 google email 등록 후 실질적인 로그인 작업 요청
     * 1. getCredential() : Goodle 로그인 ID 또는 AccessToken을 래핑하는 인스턴스를 반환
     * 2. signInWithCredential() : 지정된 User Token으로 Firebase 인증 시스템에 로그인하며, 성공 시 getCurrentUser로 사용자 정보를 가져올 수 있습니다.
     */
    suspend fun checkToken(): MyState = withContext(Dispatchers.Main){
        Log.d(Constants.TAG, "$THIS_NAME checkToken() called")

        if (!appPreferenceManager.getIdToken().isNullOrEmpty() && !appPreferenceManager.getUserNameString().isNullOrEmpty()) {

            try {
                val credential = GoogleAuthProvider.getCredential(appPreferenceManager.getIdToken(), null)
                firebaseAuth.signInWithCredential(credential).await()

                Log.d(Constants.TAG, "$THIS_NAME checkToken() : Registered")
                return@withContext Login(appPreferenceManager.getIdToken()!!, appPreferenceManager.getUserNameString()!!)

            }catch (e : Exception) {

                Log.d(Constants.TAG, "$THIS_NAME checkToken() : Error")
                return@withContext Error(R.string.an_error_occurred, e)
            }


        } else {
            Log.d(Constants.TAG, "$THIS_NAME checkToken() : No Token")

            firebaseAuth.signOut()
            return@withContext Success.NotRegistered
        }
    }


    /**
     * Firebase Current User 정보를 가져오는 메서드 : Registered, NotRegistered 반환
     */
    fun validateCurrentUser(): MyState {
        Log.d(Constants.TAG, "$THIS_NAME validateCurrentUser() called")

        return firebaseUser?.let {
            try {
                Success.Registered(userName = it.displayName ?: "익명", userImageUri = it.photoUrl)
                    .also { Log.d(Constants.TAG, "$THIS_NAME validateCurrentUser() : 프로필 요청 성공 : Registered") }

            } catch (e : Exception) {
                Error(R.string.request_error, e)
            }
        } ?: Success.NotRegistered
            .also { Log.d(Constants.TAG, "$THIS_NAME validateCurrentUser() : 프로필 요청 실패 NotRegistered") }

    }
}