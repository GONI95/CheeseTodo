package sang.gondroid.cheesetodo.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.util.*
import java.lang.Exception
import java.util.function.BiPredicate

class HandleFireStore(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val ioDispatchers: CoroutineDispatcher,
    private val context : Context
) {
    private val THIS_NAME = this::class.simpleName

    init {
        Log.d(Constants.TAG, "$THIS_NAME init() : ${hashCode()}")
    }

    /**
     * FirebaseAuth :
     * FirebaseUser : Firebase 프로젝트의 사용자 데이터베이스에 있는 사용자의 프로필 정보를 의미
     */
    private lateinit var firebaseUser : FirebaseUser

    private fun getFireStoreString(stringId : Int) : String {
        LogUtil.i(Constants.TAG, "$THIS_NAME getFireStoreString() : ${context.getString(stringId)}")
        return context.getString(stringId)
    }

    /**
     * Email Field를 기준으로 Firestore 테이블에서 회원 조회
     */
    suspend fun validateMembership() : MyState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME validateMembership() called")

        firebaseAuth.currentUser?.let { firebaseUser = it }

        return@withContext firebaseUser.email.let { email ->
            try {
                val result = firestore.collection(getFireStoreString(R.string.user_collection))
                    .whereEqualTo(getFireStoreString(R.string.user_email), email).get().await()

                if (result.isEmpty) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME validateMembership() ${joinMembership()}")
                    return@let joinMembership()
                }
                else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME validateMembership() MyState.True")
                    return@let MyState.True
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME validateMembership() MyState.Error")
                return@let MyState.Error(R.string.request_error, e)
            }
        }
    }

    /**
     * FireStore에 회원 추가
     */
    suspend fun joinMembership() : MyState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME joinMembership() called")

        var myState : MyState = MyState.Uninitialized

        return@withContext firebaseUser.email.let { email ->
            try {
                firestore.collection(getFireStoreString(R.string.user_collection))
                    .document(email!!)
                    .set(hashMapOf(
                            getFireStoreString(R.string.user_email) to firebaseUser.email,
                            getFireStoreString(R.string.user_uid) to firebaseUser.uid,
                            getFireStoreString(R.string.user_photo) to firebaseUser.photoUrl.toString(),
                            getFireStoreString(R.string.user_name) to firebaseUser.displayName as String,
                            getFireStoreString(R.string.user_todo_count) to 0,
                            getFireStoreString(R.string.user_rank) to getFireStoreString(UserRank.Level1.userRankStringId),
                            getFireStoreString(R.string.user_score) to 0
                        )).addOnCompleteListener { task ->

                        myState = if (task.isSuccessful) MyState.True else MyState.False

                        }.await()

                LogUtil.d(Constants.TAG, "$THIS_NAME joinMembership() $myState")
                return@let myState

            } catch (e: Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME joinMembership() MyState.Error")
                return@let MyState.Error(R.string.request_error, e)
            }
        }
    }

    /**
     * Firebase 인증 시스템에 로그인한 User인 현재 User의 Email과 동일한 Firestore Users Collection에서 삭제하는 메서드
     */
    suspend fun disjoinMembership() : MyState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME disjoinMembership() called")

        var myState : MyState = MyState.Uninitialized

       return@withContext firebaseUser.email.let { email ->
            try {
                firestore.collection(getFireStoreString(R.string.user_collection)).document(email!!).delete().addOnCompleteListener { task ->

                    myState = if (task.isSuccessful) MyState.True else MyState.False

                }.await()

                LogUtil.d(Constants.TAG, "$THIS_NAME disjoinMembership() $myState")
                return@let myState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME disjoinMembership() MyState.Error")
                return@let  MyState.Error(R.string.request_error, e)
            }
        }
    }
}