package sang.gondroid.cheesetodo.data.firebase

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.db.FireStoreMembershipDTO
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.*
import java.lang.Exception
import java.security.*

class HandlerFireStore(
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
    private fun getFireStoreString(stringId : Int) : String {
        LogUtil.i(Constants.TAG, "$THIS_NAME getFireStoreString() : ${context.getString(stringId)}")
        return context.getString(stringId)
    }

    /**
     * Email Field를 기준으로 Firestore 테이블에서 회원 조회
     */
    suspend fun validateMembership() : JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME validateMembership() called")

        return@withContext firebaseAuth.currentUser?.email.let { email ->
            try {
                val result = firestore.collection(getFireStoreString(R.string.user_collection))
                    .whereEqualTo(getFireStoreString(R.string.user_email), email).get().await()

                if (result.isEmpty) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME validateMembership() ${joinMembership()}")
                    return@let joinMembership()
                }
                else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME validateMembership() JobState.True")
                    return@let JobState.True
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME validateMembership() JobState.Error")
                return@let JobState.Error(R.string.request_error, e)
            }
        }
    }

    /**
     * FireStore에 회원 추가
     */
    suspend fun joinMembership() : JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME joinMembership() called")

        var jobState : JobState = JobState.Uninitialized

        return@withContext firebaseAuth.currentUser.let { firebaseUser ->
            try {
                firestore.collection(getFireStoreString(R.string.user_collection))
                    .document(firebaseUser?.email!!)
                    .set(hashMapOf(
                        getFireStoreString(R.string.user_email) to firebaseUser.email,
                        getFireStoreString(R.string.user_uid) to firebaseUser.uid,
                        getFireStoreString(R.string.user_photo) to firebaseUser.photoUrl.toString(),
                        getFireStoreString(R.string.user_name) to firebaseUser.displayName as String,
                        getFireStoreString(R.string.user_todo_count) to 0,
                        getFireStoreString(R.string.user_rank) to getFireStoreString(UserRank.Level1.userRankStringId),
                        getFireStoreString(R.string.user_score) to 0
                    )).addOnCompleteListener { task ->
                        jobState = if (task.isSuccessful) JobState.True else JobState.False
                    }.await()

                LogUtil.d(Constants.TAG, "$THIS_NAME joinMembership() $jobState")
                return@let jobState

            } catch (e: Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME joinMembership() JobState.Error")
                return@let JobState.Error(R.string.request_error, e)
            }
        }
    }

    /**
     * Gon : Firestore ReviewTodo Collection에서 현재 로그인한 회원의 게시물을 삭제하는 메서드
     *
     *       Firestore는 기본적으로 document 삭제에 대한 작업을 한번에 할 수 없습니다.
     *       reviewTodoDocumentList - 삭제할 회원의 ReviewTodo에 해당하는 document들을 찾아냅니다.
     *       reviewTodoDocumentList를 넘기며 두 메서드를 호출하여, 정상동작한 경우, ReviewTodo의 document를 삭제합니다.
     *       taskResult - document 삭제 작업 중 실패할 경우에만 값을 가집니다.
     *       [update - 21.11.17]
     */
    suspend fun deleteReviewTodoOwnedByMember() : JobState = withContext(ioDispatchers) {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() called : ${currentCoroutineContext()}")
        val taskResult = ArrayList<Boolean>()

        firebaseAuth.currentUser?.let { firebaseUser ->
            firebaseUser.email.let { email ->
                try {
                    val reviewTodoDocumentList = firestore.collection(getFireStoreString(R.string.review_todo_collection))
                        .whereEqualTo(getFireStoreString(R.string.user_email), email)
                        .get().await().documents

                    val dltCheckedUser = async {
                        deleteCheckedUserOwnedByMemeber(reviewTodoDocumentList).also {
                            LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedUserOwnedByMemeber() return : $it")
                        }
                    }

                    val dltComments = async {
                        deleteCommentsOwnedByMemeber(reviewTodoDocumentList).also {
                            LogUtil.d(Constants.TAG, "$THIS_NAME deleteCommentsOwnedByMemeber() return : $it")
                        }
                    }

                    if (dltComments.await() && dltCheckedUser.await()) {
                        reviewTodoDocumentList.forEach { documentSnapshot ->
                            firestore.collection(getFireStoreString(R.string.review_todo_collection))
                                .document(documentSnapshot.id)
                                .delete()
                                .addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        taskResult.add(task.isSuccessful)
                                    }
                                }.await()
                        }

                        return@withContext if (taskResult.isEmpty()) JobState.True else JobState.False
                    }

                    return@withContext JobState.False

                } catch (e : Throwable) {
                    return@withContext JobState.Error(R.string.request_error, e)
                }
            }
        } ?: kotlin.run {
            return@withContext JobState.Uninitialized
        }
    }

    /**
     * Gon : Firestore ReviewTodo Collection에서 현재 로그인한 회원의 게시물의 Comments를 삭제하는 메서드
     *
     *       Firestore는 기본적으로 document 삭제에 대한 작업을 한번에 할 수 없습니다.
     *       commentsDocumentList - 삭제할 회원의 Comments에 해당하는 document들을 찾아냅니다.
     *       commentsDocumentList의 정보를 확인하여 Comments의 document를 삭제합니다.
     *       taskResult - document 삭제 작업 중 실패할 경우에만 값을 가집니다.
     *       [update - 21.11.17]
     */
    private suspend fun deleteCommentsOwnedByMemeber(reviewTodoDocumentList: List<DocumentSnapshot>): Boolean {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCommentsOwnedByMemeber() called : ${currentCoroutineContext()}")
        val taskResult = ArrayList<Boolean>()

        reviewTodoDocumentList.forEach { documentSnapshot ->

            val commentsDocumentList = firestore.collection(getFireStoreString(R.string.review_todo_collection))
                .document(documentSnapshot.id)
                .collection(getFireStoreString(R.string.review_comments_collection))
                .get().await().documents

            commentsDocumentList.forEach {
                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(documentSnapshot.id)
                    .collection(getFireStoreString(R.string.review_comments_collection))
                    .document(it.id)
                    .delete()
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            taskResult.add(task.isSuccessful)
                        }
                    }
                    .await()
            }

            return taskResult.isEmpty()
        }

        return false
    }

    /**
     * Gon : Firestore ReviewTodo Collection에서 현재 로그인한 회원의 게시물의 CheckedUser를 삭제하는 메서드
     *
     *       Firestore는 기본적으로 document 삭제에 대한 작업을 한번에 할 수 없습니다.
     *       checkedUsersDocumentList - 삭제할 회원의 checked_user에 해당하는 document들을 찾아냅니다.
     *       checkedUsersDocumentList의 정보를 확인하여 checked_user의 document를 삭제합니다.
     *       taskResult - document 삭제 작업 중 실패할 경우에만 값을 가집니다.
     *       [update - 21.11.17]
     */
    private suspend fun deleteCheckedUserOwnedByMemeber(reviewTodoDocumentList: List<DocumentSnapshot>) : Boolean {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCommentsOwnedByMemeber() called")
        val taskResult = ArrayList<Boolean>()

        reviewTodoDocumentList.forEach { documentSnapshot ->

            val checkedUsersDocumentList = firestore.collection(getFireStoreString(R.string.review_todo_collection))
                .document(documentSnapshot.id)
                .collection(getFireStoreString(R.string.checked_user_collection))
                .get().await().documents

            checkedUsersDocumentList.forEach {
                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(documentSnapshot.id)
                    .collection(getFireStoreString(R.string.checked_user_collection))
                    .document(it.id)
                    .delete()
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            taskResult.add(task.isSuccessful)
                        }
                    }
                    .await()
            }

            return taskResult.isEmpty()
        }

        return false
    }

    /**
     * Gon : Firestore Users Collection에서 현재 로그인한 회원 정보의 삭제하는 메서드
     * [update - 21.11.17]
     */
    suspend fun deleteMemberInfo() : JobState {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteMemberInfo() called")
        var jobState : JobState = JobState.Uninitialized

        firebaseAuth.currentUser?.let { firebaseUser ->
            firebaseUser.email.let { email ->
                return try {
                    firestore.collection(getFireStoreString(R.string.user_collection))
                        .document(email!!).delete().addOnCompleteListener { task ->
                            jobState = if (task.isSuccessful) JobState.True else JobState.False
                        }.await()

                    jobState
                } catch (e : Throwable) {
                    JobState.Error(R.string.request_error, e)
                }
            }
        } ?: kotlin.run {
            return jobState
        }
    }

    suspend fun getCurrentMembership() : JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME getCurrentMembership() called")

        return@withContext firebaseAuth.currentUser?.email.let { email ->
            try {
                val result = firestore.collection(getFireStoreString(R.string.user_collection)).document(email!!).get().await()

                if (result.exists()) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME getCurrentMembership() JobState.Registered")

                    return@let JobState.Success.Registered(
                        result.toObject(FireStoreMembershipDTO::class.java)
                    )
                }
                else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME getCurrentMembership() JobState.NotRegistered")
                    return@let JobState.Success.NotRegistered
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME getCurrentMembership() JobState.Error")
                return@let  JobState.Error(R.string.request_error, e)
            }
        }
    }

    suspend fun validateReviewTodoExist(model: TodoModel): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME validateReviewTodoExist() called")

        return@withContext firebaseAuth.currentUser.let { firebaseUser ->
            try {

                val result = firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .whereEqualTo(getFireStoreString(R.string.user_email), firebaseUser?.email)
                    .whereEqualTo(getFireStoreString(R.string.review_title), model.title)
                    .whereEqualTo(getFireStoreString(R.string.review_id), model.id)
                    .get().await()

                if (result.isEmpty) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME validateReviewTodoExist() JobState.True")
                    return@let JobState.True
                }
                else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME validateReviewTodoExist() JobState.False")
                    return@let JobState.False
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME validateReviewTodoExist() JobState.Error")
                return@let  JobState.Error(R.string.request_error, e)
            }
        }
    }

    suspend fun insertReviewTodo(model: ReviewTodoDTO): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME insertReviewTodo() called")
        var jobState : JobState = JobState.Uninitialized

        return@withContext firebaseAuth.currentUser.let { firebaseUser ->
            try {
                LogUtil.v(Constants.TAG, "$THIS_NAME insertReviewTodo() model : ${model}")

                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(firebaseUser?.email + model.modelId)
                    .set(model)
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                            jobState = JobState.True
                        else
                            jobState = JobState.False
                    }.await()

                LogUtil.d(Constants.TAG, "$THIS_NAME insertReviewTodo() JobState : ${jobState}")
                return@let jobState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME insertReviewTodo() JobState.Error : $e")
                return@let  JobState.Error(R.string.request_error, e)
            }
        }
    }

    /**
     *  Gon : insertReviewTodo()의 반환값이 JobState.True이면 userTodoCount를 증가시키기 위한 메서드 입니다.
     */
    suspend fun updateMembershipUserTodoCount(model: ReviewTodoModel): JobState = withContext(ioDispatchers) {
        var jobState : JobState = JobState.Uninitialized

        return@withContext firebaseAuth.currentUser?.email.let { _ ->
            try {
                LogUtil.v(Constants.TAG, "$THIS_NAME updateMembershipUserTodoCount()")

                getReviewTodoWriterMembership(model.userEmail)?.let {
                    val userTodoCount = it.get(getFireStoreString(R.string.user_todo_count)) as Long + 1

                    firestore.collection(getFireStoreString(R.string.user_collection))
                        .document(model.userEmail)
                        .update(getFireStoreString(R.string.user_todo_count), userTodoCount)
                        .addOnCompleteListener { task ->
                            jobState = if (task.isSuccessful) JobState.True else JobState.False
                        }.await()
                }

                LogUtil.d(Constants.TAG, "$THIS_NAME updateMembershipUserTodoCount() JobState : $jobState")
                return@let jobState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME updateMembershipUserTodoCount() JobState.Error : $e")
                return@let  JobState.Error(R.string.request_error, e)
            }
        }
    }

    suspend fun getReviewTodo() : JobState = withContext(ioDispatchers) {
        return@withContext firebaseAuth.currentUser.let { firebaseUser ->
            try {
                LogUtil.v(Constants.TAG, "$THIS_NAME getReviewTodo()")

                val result = firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .get().await()

                if (!result.isEmpty) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME getReviewTodo() JobState.True")
                    return@let JobState.True.Result<List<ReviewTodoDTO>>( result.toObjects(ReviewTodoDTO::class.java).sortedByDescending { it.date } )
                }
                else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME getReviewTodo() JobState.False")
                    return@let JobState.False
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME getReviewTodo() JobState.Error : $e")
                return@let  JobState.Error(R.string.request_error, e)
            }
        }
    }

    suspend fun insertComment(commentDTO: CommentDTO, reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME insertComment() called")

        return@withContext firebaseAuth.currentUser.let { firebaseUser ->
            try {
                val result =

                    firestore.collection(getFireStoreString(R.string.review_todo_collection))
                        .document(reviewTodoModel.userEmail + reviewTodoModel.modelId)
                        .collection(getFireStoreString(R.string.review_comments_collection))
                        .document()
                        .set(commentDTO)

                if (result.isSuccessful) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME insertComment() JobState.True")
                    return@let JobState.True
                }
                else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME insertComment() JobState.False")
                    return@let JobState.False
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME insertComment() JobState.Error")
                return@let JobState.Error(R.string.request_error, e)
            }
        }
    }

    suspend fun getComments(model: ReviewTodoModel): Observable<List<CommentDTO>> = withContext(ioDispatchers) {
        return@withContext Observable.create { emitter ->
            firebaseAuth.currentUser.let { firebaseUser ->
                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(model.userEmail + model.modelId)
                    .collection(getFireStoreString(R.string.review_comments_collection))
                    .addSnapshotListener { value, error ->
                        if (error != null)
                            emitter.onError(error)
                        else {
                            value?.let { querySnapshot ->
                                val tasks = querySnapshot.toObjects(CommentDTO::class.java)
                                    .sortedByDescending { it.date }
                                emitter.onNext(tasks)

                            } ?: emitter.onError(Exception())
                        }
                    }
            }
        }
    }

    suspend fun insertCheckedUser(reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME insertCheckedUser() called")

        return@withContext firebaseAuth.currentUser.let { firebaseUser ->
            try {
                var state : JobState = JobState.Uninitialized

                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(reviewTodoModel.userEmail + reviewTodoModel.modelId)
                    .collection(getFireStoreString(R.string.checked_user_collection))
                    .document(firebaseUser!!.email.toString())
                    .set(mapOf(getFireStoreString(R.string.user_email) to firebaseUser.email))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            state = JobState.True
                        }
                        else
                            state = JobState.False
                    }.await()

                LogUtil.v(Constants.TAG, "$THIS_NAME insertCheckedUser() JobState : ${state}")
                return@let state

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME insertCheckedUser() JobState.Error : $e")
                return@let JobState.Error(R.string.request_error, e)
            }
        }
    }

    suspend fun getCheckedUserCount(model: ReviewTodoModel) : Observable<Int> = withContext(ioDispatchers) {

        return@withContext Observable.create { emitter ->
            firebaseAuth.currentUser.let {
                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(model.userEmail + model.modelId)
                    .collection(getFireStoreString(R.string.checked_user_collection))
                    .addSnapshotListener { value, error ->
                        LogUtil.v(Constants.TAG, "$THIS_NAME getCheckedUserCount() : ${value}")

                        if (error != null)
                            emitter.onError(error)
                        else {
                            value?.let { querySnapshot ->
                                querySnapshot.documents.size.let { size ->
                                    emitter.onNext(size)
                                }
                            } ?: emitter.onError(Exception())
                        }
                    }
            }

        }
    }
    suspend fun getCheckedCurrentUser(model: ReviewTodoModel) : JobState = withContext(ioDispatchers) {

        return@withContext firebaseAuth.currentUser?.email.let { firebaseUserEmail ->
            try {
                LogUtil.v(Constants.TAG, "$THIS_NAME getCheckedCurrentUser()")

                val result = firestore.collection(getFireStoreString(R.string.review_todo_collection))
                        .document(model.userEmail + model.modelId)
                        .collection(getFireStoreString(R.string.checked_user_collection))
                        .whereEqualTo(getFireStoreString(R.string.user_email), firebaseUserEmail)
                        .get()
                        .await()

                if (!result.isEmpty) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedCurrentUser() JobState.True")
                    return@let JobState.True
                }
                else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME getCheckedCurrentUser() JobState.False")
                    return@let JobState.False
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME getCheckedCurrentUser() JobState.Error : $e")
                return@let  JobState.Error(R.string.request_error, e)
            }
        }
    }

    suspend fun deleteCheckedUser(model: ReviewTodoModel) : JobState = withContext(ioDispatchers) {
        var jobState : JobState = JobState.Uninitialized

        return@withContext firebaseAuth.currentUser?.email.let { firebaseUserEmail ->
            try {
                LogUtil.v(Constants.TAG, "$THIS_NAME deleteCheckedUser()")

                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(model.userEmail + model.modelId)
                    .collection(getFireStoreString(R.string.checked_user_collection))
                    .document(firebaseUserEmail.toString())
                    .delete()
                    .addOnCompleteListener { task ->

                        jobState = if (task.isSuccessful) JobState.True else JobState.False

                    }.await()

                return@let jobState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME deleteCheckedUser() JobState.Error : $e")
                return@let  JobState.Error(R.string.request_error, e)
            }
        }
    }

    private suspend fun getReviewTodoWriterMembership(userEmail : String) : DocumentSnapshot? = withContext(ioDispatchers) {

        return@withContext firebaseAuth.currentUser.let {
            try {
                LogUtil.v(Constants.TAG, "$THIS_NAME getReviewTodoWriterMembership()")

                val result = firestore.collection(getFireStoreString(R.string.user_collection))
                    .document(userEmail)
                    .get()
                    .await()

                if (result.exists()) {
                    LogUtil.v(Constants.TAG, "$THIS_NAME getReviewTodoWriterMembership() : JobState.True")
                    return@let result
                } else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME getReviewTodoWriterMembership() : JobState.False")
                    return@let null
                }
            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME getReviewTodoWriterMembership() JobState.Error : $e")
                return@let  null
            }
        }
    }

    /**
     *  Gon : insertCheckedUser()의 반환값이 JobState.True이면 userScore를 증가시키기 위한 메서드 입니다.
     */
    suspend fun updateMembershipUserScore(model: ReviewTodoModel): JobState = withContext(ioDispatchers) {
        var jobState : JobState = JobState.Uninitialized

        return@withContext firebaseAuth.currentUser?.email.let { firebaseUserEmail ->
            try {
                LogUtil.v(Constants.TAG, "$THIS_NAME updateUserScore()")

                getReviewTodoWriterMembership(model.userEmail)?.let {
                    val userScore = it.get(getFireStoreString(R.string.user_score)) as Long + 10L

                    firestore.collection(getFireStoreString(R.string.user_collection))
                        .document(model.userEmail)
                        .update(getFireStoreString(R.string.user_score), userScore, getFireStoreString(R.string.user_rank), userScore.toUserRank())
                        .addOnCompleteListener { task ->
                            jobState = if (task.isSuccessful) JobState.True else JobState.False
                        }.await()
                }

                return@let jobState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME updateUserScore() JobState.Error : $e")
                return@let  JobState.Error(R.string.request_error, e)
            }
        }
    }
}