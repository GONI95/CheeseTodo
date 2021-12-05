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
     * Gon : Firebase Auth 인증 시스템에 로그인을 시도
     *       SharedPreference로부터 Firebase ID Toeken(자격증명) 값의 유무에 따라 Firebase Authentication에 지정된 자격증명을 통해 로그인을 시도합니다.
     *
     *       Firebase Authentication에 google email 등록 후 실질적인 로그인 작업 요청
     *       1. getCredential() : Goodle 로그인 ID 또는 AccessToken을 래핑하는 인스턴스를 반환
     *       2. signInWithCredential() : 지정된 User Token으로 Firebase 인증 시스템에 로그인하며, 성공 시 getCurrentUser로 사용자 정보를 가져올 수 있습니다.
     *       [update - 21.12.01]
     */
    suspend fun signInWithCredential( result: (JobState) -> Unit ) = withContext(ioDispatchers){
        LogUtil.v(Constants.TAG, "$THIS_NAME signInWithCredential() called")

        val idData = appPreferenceManager.getIdTokenString()
        val nameData = appPreferenceManager.getDisPlayNameString()

        if (!idData.isNullOrEmpty() && !nameData.isNullOrEmpty()) {
            try {
                val credential = GoogleAuthProvider.getCredential(idData, null)

                firebaseAuth.signInWithCredential(credential).await().let { result ->
                    result.user?.let {
                        firebaseUser = it
                    } // firebase 인증 시스템에 로그인 할 때 마다 현재 User를 초기화
                }

                result(JobState.Signin(idData, nameData))
            }catch (e : Exception) {
                result(JobState.Error(R.string.request_error, e))
            }
        } else {
            firebaseAuth.signOut()
            result(JobState.Success.NotRegistered)
        }
    }

    /**
     * Gon : Firebase 인증 시스템에 로그인한 User인 현재 User 정보를 Firebase 인증 시스템으로부터 삭제하는 메서드
     *
     *       FirebaseAuth.getInstance().currentUser.delete() : Firebase Auth Database에서 현재 사용자 정보를 삭제
     *       [update - 21.11.17]
     */
    suspend fun deleteCurrentUser( result: (JobState) -> Unit ) = withContext(ioDispatchers) {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCurrentUser() called : ${currentCoroutineContext()}")

        firebaseUser.let { user ->
            try {
                user.delete().addOnCompleteListener { task ->
                    result(if (task.isSuccessful) JobState.True else JobState.False)
                }.await()

            } catch (e : Exception) {
                result(JobState.Error(R.string.request_error, e))
            }
        }
    }
}