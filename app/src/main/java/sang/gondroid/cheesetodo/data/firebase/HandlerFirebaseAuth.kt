package sang.gondroid.cheesetodo.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.JobState
import java.lang.Exception

/**
 * ViewModel에서 메서드를 구현해 호출하면 Singleton이 아니어서, 모든 fragment에서 상태가 변할 때 마다 계속해서 checkJobState()를 호출해야하므로 class 생성
 */
class HandlerFirebaseAuth(private val appPreferenceManager: AppPreferenceManager, private val firebaseAuth: FirebaseAuth,
                          private val ioDispatchers: CoroutineDispatcher) {

    private val THIS_NAME = this::class.simpleName

    /**
     * FirebaseAuth :
     * FirebaseUser : Firebase 프로젝트의 사용자 데이터베이스에 있는 사용자의 프로필 정보를 의미
     */
    private lateinit var firebaseUser : FirebaseUser

    /**
     * 로그인 유무를 파악하기위한 메서드 : Login, NotRegistered 반환
     *
     * Firebase Authentication에 google email 등록 후 실질적인 로그인 작업 요청
     * 1. getCredential() : Goodle 로그인 ID 또는 AccessToken을 래핑하는 인스턴스를 반환
     * 2. signInWithCredential() : 지정된 User Token으로 Firebase 인증 시스템에 로그인하며, 성공 시 getCurrentUser로 사용자 정보를 가져올 수 있습니다.
     */
    suspend fun validateToken(): JobState = withContext(ioDispatchers){
        LogUtil.v(Constants.TAG, "$THIS_NAME validateToken() called")

        if (!appPreferenceManager.getIdToken().isNullOrEmpty() && !appPreferenceManager.getUserNameString().isNullOrEmpty()) {
            try {
                val credential = GoogleAuthProvider.getCredential(appPreferenceManager.getIdToken()!!, null)
                firebaseAuth.signInWithCredential(credential).await().let { result ->
                    result.user?.let {
                        firebaseUser = it
                    } // firebase 인증 시스템에 로그인 할 때 마다 현재 User를 초기화
                }

                LogUtil.d(Constants.TAG, "$THIS_NAME validateToken() : JobState.Login")
                return@withContext JobState.Login(appPreferenceManager.getIdToken()!!, appPreferenceManager.getUserNameString()!!)

            }catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME validateToken() : JobState.Error")
                return@withContext JobState.Error(R.string.request_error, e)
            }
        } else {
            LogUtil.d(Constants.TAG, "$THIS_NAME validateToken() : JobState.NotRegistered")

            firebaseAuth.signOut()
            return@withContext JobState.Success.NotRegistered
        }
    }


    /**
     * Firebase 인증 시스템에 로그인한 User인 현재 User 정보를 가져오는 메서드 : Registered, NotRegistered 반환
     */
    fun validateCurrentUser(): JobState {
        LogUtil.v(Constants.TAG, "$THIS_NAME getCurrentUser() called")

        try {
            if (firebaseAuth.currentUser != null) {
                LogUtil.d(Constants.TAG, "$THIS_NAME getCurrentUser() JobState.True")
                return JobState.True
            } else {
                LogUtil.d(Constants.TAG, "$THIS_NAME getCurrentUser() JobState.False")
                return JobState.False
            }
        } catch (e : Exception) {
            LogUtil.d(Constants.TAG, "$THIS_NAME getCurrentUser() MyState.Error")
            return JobState.Error(R.string.request_error, e)
        }

    }


    /**
     * Gon : Firebase 인증 시스템에 로그인한 User인 현재 User 정보를 Firebase 인증 시스템으로부터 삭제하는 메서드
     *
     *       FirebaseAuth.getInstance().currentUser.delete() : Firebase Auth Database에서 현재 사용자 정보를 삭제
     *       [update - 21.11.17]
     */
    suspend fun deleteCurrentUser( result: (JobState) -> Unit ) {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCurrentUser() called : ${currentCoroutineContext()}")

        firebaseUser.let { user ->
            try {
                user.delete().addOnCompleteListener { task ->
                    result(if (task.isSuccessful) JobState.True else JobState.False)
                }.await()

            } catch (e : Exception) {
                result(JobState.Error(R.string.request_error, e))
            }
        } ?: kotlin.run {
            result(JobState.Uninitialized)
        }
    }
}