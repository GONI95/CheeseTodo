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
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.MyState
import sang.gondroid.cheesetodo.util.UserRank
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
        Log.d(Constants.TAG, "$THIS_NAME getFireStoreString() : ${context.getString(stringId)}")
        return context.getString(stringId)
    }

    /**
     * Email Field를 기준으로 Firestore 테이블에서 회원 조회
     */
    suspend fun validateMembership() : MyState = withContext(ioDispatchers) {
        Log.d(Constants.TAG, "$THIS_NAME validateMembership() called")

        firebaseAuth.currentUser?.let { firebaseUser = it }

        firebaseUser.email.let { email ->
            try {
                val result = firestore.collection(getFireStoreString(R.string.user_collection))
                    .whereEqualTo(getFireStoreString(R.string.user_email), email).get().await()

                if (result.isEmpty) {
                    Log.d(Constants.TAG, "$THIS_NAME validateMembership() ${joinMembership()}")
                    return@withContext joinMembership()
                }
                else {
                    Log.d(Constants.TAG, "$THIS_NAME validateMembership() True")
                    return@withContext MyState.True
                }

            } catch (e : Exception) {
                return@withContext MyState.Error(R.string.request_error, e)
            }
        }
    }

    /**
     * FireStore에 회원 추가
     */
    suspend fun joinMembership() : MyState = withContext(ioDispatchers) {
        Log.d(Constants.TAG, "$THIS_NAME joinMembership() called")

        var myState : MyState = MyState.Uninitialized

        firebaseUser.email.let { email ->
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
                            if (task.isSuccessful) {
                                myState = MyState.True
                            } else {
                                MyState.False
                            }
                    }.await()

                return@withContext myState

            } catch (e: Exception) {
                return@withContext MyState.Error(R.string.request_error, e)
            }
        }
    }
}