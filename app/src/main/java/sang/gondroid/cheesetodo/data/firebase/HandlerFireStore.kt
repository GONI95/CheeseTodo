package sang.gondroid.cheesetodo.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.db.FireStoreMemberDTO
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

    private fun getFireStoreString(stringId : Int) : String {
        LogUtil.i(Constants.TAG, "$THIS_NAME getFireStoreString() : ${context.getString(stringId)}")
        return context.getString(stringId)
    }

    /**
     *  Gon : Email Field를 기준으로 Firestore 테이블에서 회원 조회
     *        1. Firestore Users 콜렉션에서 현재 Firebase Auth 사용자 정보와 동일한 정보가 있는지 확인
     *        1-1 정보가 있는 경우 : Firebase Auth 사용자 정보에 해당하는 Firestore Member 정보를 가져옵니다.
     *        1-2 정보가 없는 경우 : Firestore Member 콜렉션에 Firebase Auth 사용자 정보를 추가합니다.
     *        [update - 21.12.15]
     */
    suspend fun memberVerification() : JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME memberVerification() called")

        return@withContext firebaseAuth.currentUser?.let { firebaseUser ->
            return@let firebaseUser.email?.let { email ->
                try {
                    val result = firestore.collection(getFireStoreString(R.string.member_collection))
                        .whereEqualTo(getFireStoreString(R.string.member_email), email)
                        .get()
                        .await()

                    if (result.isEmpty) {
                        LogUtil.d(Constants.TAG, "$THIS_NAME memberVerification() createAccount() call")
                        createAccount(firebaseUser).let {
                            if (it !is JobState.True) return@withContext it
                        }
                    }

                    LogUtil.d(Constants.TAG, "$THIS_NAME memberVerification() getCurrentMember() call")
                    getCurrentMember(firebaseUser)

                } catch (e : Exception) {
                    LogUtil.e(Constants.TAG, "$THIS_NAME memberVerification() JobState.Error")
                    JobState.Error(R.string.request_error, e)
                }

            } ?: kotlin.run {
                LogUtil.d(Constants.TAG, "$THIS_NAME joinMember() firebaseUser.email Uninitialized")
                return@withContext JobState.Uninitialized
            }
        }?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME joinMember() firebaseUser.email Uninitialized")
            return@withContext JobState.Uninitialized
        }
    }

    /**
     *  Gon : Firestore Member Collection에 document(현재 사용자 정보)를 추가하는 메서드
     *        [update - 21.12.15]
     */
    suspend fun createAccount(firebaseUser: FirebaseUser): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME createAccount() called")

        var jobState : JobState = JobState.Uninitialized

        firebaseUser.email?.let { email ->
            try {
                firestore.collection(getFireStoreString(R.string.member_collection))
                    .document(email)
                    .set(hashMapOf(
                        getFireStoreString(R.string.member_email) to firebaseUser.email,
                        getFireStoreString(R.string.member_uid) to firebaseUser.uid,
                        getFireStoreString(R.string.member_photo) to firebaseUser.photoUrl.toString(),
                        getFireStoreString(R.string.member_name) to firebaseUser.displayName as String,
                        getFireStoreString(R.string.member_todo_count) to 0,
                        getFireStoreString(R.string.member_rank) to getFireStoreString(MemberRank.Level1.memberRankStringId),
                        getFireStoreString(R.string.member_score) to 0
                    )).addOnCompleteListener { task ->
                        jobState = if (task.isSuccessful) JobState.True else JobState.False
                    }.await()

                LogUtil.d(Constants.TAG, "$THIS_NAME createAccount() $jobState")
                return@withContext jobState

            } catch (e: Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME createAccount() JobState.Error")
                return@withContext JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME createAccount() firebaseAuth.currentUser Uninitialized")
            return@withContext jobState
        }
    }

    suspend fun getCurrentMember(firebaseUser: FirebaseUser): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME getCurrentMember() called")

        return@withContext firebaseUser.email?.let { email ->
            try {
                val result = firestore.collection(getFireStoreString(R.string.member_collection)).document(email).get().await()

                if (result.exists()) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME getCurrentMember() JobState.Registered")

                    return@let JobState.Success.Registered(
                        result.toObject(FireStoreMemberDTO::class.java)
                    )
                }
                else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME getCurrentMember() JobState.NotRegistered")
                    return@let JobState.Success.NotRegistered
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME getCurrentMember() JobState.Error")
                return@let  JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME joinMember() firebaseAuth.currentUser Uninitialized")
            return@withContext JobState.Uninitialized
        }
    }

    /**
     * Gon : Firestore Member Collection에서 현재 로그인한 회원 정보의 삭제하는 메서드
     *       [update - 21.11.17]
     */
    suspend fun deleteAccount() : JobState = withContext(ioDispatchers) {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteAccount() called")

        return@withContext firebaseAuth.currentUser?.email?.let { email ->
            return@let try {

                val result = deleteReviewTodoOwnedByMember(email)

                if (result is JobState.True) {
                    var jobState : JobState = JobState.Uninitialized

                    firestore.collection(getFireStoreString(R.string.member_collection))
                        .document(email)
                        .delete()
                        .addOnCompleteListener {
                            jobState = if (it.isSuccessful) {
                                LogUtil.d(Constants.TAG, "$THIS_NAME deleteAccount() JobState.True")
                                JobState.True
                            } else {
                                LogUtil.d(Constants.TAG, "$THIS_NAME deleteAccount() JobState.False")
                                JobState.False
                            }
                        }
                        .await()

                    jobState

                } else {
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteAccount() -> deleteReviewTodoOwnedByMember() : $result")
                    result
                }

            } catch (e : Throwable) {
                LogUtil.d(Constants.TAG, "$THIS_NAME deleteAccount() Error")
                JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME deleteAccount() firebaseUser.email Uninitialized")
            return@withContext JobState.Uninitialized
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
    suspend fun deleteReviewTodoOwnedByMember(email: String): JobState = withContext(ioDispatchers) {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() called : ${currentCoroutineContext()}")

        val taskResult = ArrayList<Boolean>()

        try {
            val reviewTodoDocumentList = firestore.collection(getFireStoreString(R.string.review_todo_collection))
                .whereEqualTo(getFireStoreString(R.string.member_email), email)
                .get().await().documents

            val dltCheckedMember = async {
                deleteCheckedMemberOwnedByMemeber(reviewTodoDocumentList).also {
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedMemberOwnedByMemeber() return : $it")
                }
            }

            val dltComments = async {
                deleteCommentsOwnedByMemeber(reviewTodoDocumentList).also {
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteCommentsOwnedByMemeber() return : $it")
                }
            }

            if (dltComments.await() && dltCheckedMember.await()) {
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

                LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() 멤버 컬랙션에서 회원 삭제 ${taskResult.isEmpty()}")
                return@withContext if (taskResult.isEmpty()) JobState.True else JobState.False
            } else {
                LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() JobState.False")
                return@withContext JobState.False
            }

        } catch (e : Throwable) {
            LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() JobState.Error")
            return@withContext JobState.Error(R.string.request_error, e)
        }
    }


    /**
     * Gon : Firestore ReviewTodo Collection에서 현재 로그인한 회원의 게시물의 comments collection의 모든 document를 삭제하는 메서드
     *
     *       Firestore는 기본적으로 document 삭제에 대한 작업을 한번에 할 수 없습니다.
     *       commentsDocumentList - 삭제할 회원의 Comments에 해당하는 document들을 찾아냅니다.
     *       commentsDocumentList의 정보를 확인하여 Comments의 document를 삭제합니다.
     *       taskResult - document 삭제 작업 중 실패할 경우에만 값을 가집니다.
     *       [update - 21.11.17]
     */
    private suspend fun deleteCommentsOwnedByMemeber(reviewTodoDocumentList: List<DocumentSnapshot>): Boolean {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCommentsOwnedByMemeber() called : ${currentCoroutineContext()}")
        try {
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
                            taskResult.add(task.isSuccessful)
                        }
                        .await()
                }

                LogUtil.d(Constants.TAG, "$THIS_NAME deleteCommentsOwnedByMemeber() ${!taskResult.contains(false)}")
                return !taskResult.contains(false)
            }

            return true

        } catch (e : Exception) {
            LogUtil.d(Constants.TAG, "$THIS_NAME deleteCommentsOwnedByMemeber() Error : false")
            return false
        }
    }

    /**
     * Gon : Firestore ReviewTodo Collection에서 현재 로그인한 회원의 게시물의 checked_member collection의 모든 document를 삭제하는 메서드
     *
     *       Firestore는 기본적으로 document 삭제에 대한 작업을 한번에 할 수 없습니다.
     *       checkedMemberDocumentList - 삭제할 회원의 checked_member에 해당하는 document들을 찾아냅니다.
     *       checkedMemberDocumentList의 정보를 확인하여 checked_member의 document를 삭제합니다.
     *       taskResult - document 삭제 작업 중 실패할 경우에만 값을 가집니다.
     *       [update - 21.11.17]
     */
    private suspend fun deleteCheckedMemberOwnedByMemeber(reviewTodoDocumentList: List<DocumentSnapshot>) : Boolean {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCommentsOwnedByMemeber() called")
        try {
            val taskResult = ArrayList<Boolean>()

            reviewTodoDocumentList.forEach { documentSnapshot ->

                val checkedMembersDocumentList = firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(documentSnapshot.id)
                    .collection(getFireStoreString(R.string.checked_member_collection))
                    .get().await().documents

                checkedMembersDocumentList.forEach {
                    firestore.collection(getFireStoreString(R.string.review_todo_collection))
                        .document(documentSnapshot.id)
                        .collection(getFireStoreString(R.string.checked_member_collection))
                        .document(it.id)
                        .delete()
                        .addOnCompleteListener { task ->
                            taskResult.add(task.isSuccessful)
                        }
                        .await()
                }

                LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedMemberOwnedByMemeber() ${!taskResult.contains(false)}")
                return !taskResult.contains(false)
            }

            return true

        } catch (e : Exception) {
            LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedMemberOwnedByMemeber() Error : false")
            return false
        }
    }

    /**
     *  Gon : Firestore ReviewTodo Collection에서 추가하려는 Todo의 정보와 동일한 ReviewTodo가 있는지 확인하는 메서드
     *        [update - 21.12.14]
     */
    suspend fun validateReviewTodoExist(model: TodoModel): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME validateReviewTodoExist() called")

        return@withContext firebaseAuth.currentUser?.let { firebaseUser ->
            try {
                return@let firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .whereEqualTo(getFireStoreString(R.string.member_email), firebaseUser.email)
                    .whereEqualTo(getFireStoreString(R.string.review_title), model.title)
                    .whereEqualTo(getFireStoreString(R.string.review_id), model.id)
                    .get()
                    .await()
                    .isEmpty
                    .run {
                        if (this) {
                            LogUtil.d(Constants.TAG, "$THIS_NAME validateReviewTodoExist() JobState.True")
                            JobState.True
                        } else {
                            LogUtil.d(Constants.TAG, "$THIS_NAME validateReviewTodoExist() JobState.False")
                            JobState.False
                        }
                    }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME validateReviewTodoExist() JobState.Error")
                return@let  JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME validateReviewTodoExist() JobState.Uninitialized")
            return@withContext JobState.Uninitialized
        }
    }

    /**
     * Gon : Firestore ReviewTodo Collection에 ReviewTodo document를 추가하는 메서드
     *       작업 성공 시 updateMemberReviewTodoCount()를 호출하고 결과를 반환합니다.
     *       [update - 21.12.14]
     */
    suspend fun insertReviewTodo(model: ReviewTodoDTO): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME insertReviewTodo() called")

        return@withContext firebaseAuth.currentUser?.let { firebaseUser ->
            return@let try {

                var jobState : JobState = JobState.Uninitialized

                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(firebaseUser.email + model.modelId)
                    .set(model)
                    .addOnCompleteListener {
                        jobState = if (it.isSuccessful) {
                            LogUtil.d(Constants.TAG, "$THIS_NAME insertReviewTodo() JobState.True")
                            JobState.True
                        } else {
                            LogUtil.d(Constants.TAG, "$THIS_NAME insertReviewTodo() JobState.False")
                            JobState.False
                        }
                    }
                    .await()

                jobState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME insertReviewTodo() JobState.Error : $e")
                JobState.Error(R.string.request_error, e)
            }
        }  ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME insertReviewTodo() JobState.Uninitialized")
            return@withContext JobState.Uninitialized
        }
    }

    /**
     *  Gon : Firestore Member Collection에 ReviewTodo를 등록한 회원의 memberTodoCount field를 증가시키기 위한 메서드 입니다.
     *        insertReviewTodo()의 반환값이 JobState.True인 경우 호출됩니다.
     *        [update - 21.12.14]
     */
    suspend fun updateMemberReviewTodoCount(model: ReviewTodoModel): JobState = withContext(ioDispatchers) {
        return@withContext try {
            LogUtil.v(Constants.TAG, "$THIS_NAME updateMemberReviewTodoCount() called")

            val result = getReviewTodoWriterMember(model.memberEmail)

            if (result is JobState.True.Result<*>) {
                val memberTodoCount = (result.data as DocumentSnapshot).get(getFireStoreString(R.string.member_todo_count)) as Long + 1

                var jobState : JobState = JobState.Uninitialized

                firestore.collection(getFireStoreString(R.string.member_collection))
                    .document(model.memberEmail)
                    .update(getFireStoreString(R.string.member_todo_count), memberTodoCount)
                    .addOnCompleteListener {
                        jobState = if (it.isSuccessful) {
                            LogUtil.d(Constants.TAG, "$THIS_NAME updateMemberReviewTodoCount() JobState.True")
                            JobState.True
                        } else {
                            LogUtil.d(Constants.TAG, "$THIS_NAME updateMemberReviewTodoCount() JobState.False")
                            JobState.False
                        }
                    }
                    .await()

                jobState
            } else {
                LogUtil.d(Constants.TAG, "$THIS_NAME updateMemberReviewTodoCount() -> getReviewTodoWriterMember() : $result")
                result
            }

        } catch (e : Exception) {
            LogUtil.e(Constants.TAG, "$THIS_NAME updateMemberReviewTodoCount() JobState.Error : $e")
            JobState.Error(R.string.request_error, e)
        }
    }


        /**
         * Gon : Rx를 이용해 Firestore ReviewTodo Collection에서 document가 변경될 때 마다 반환하는 메소드
         *       ReviewTodo Collection은 read에 한해서 true이기 때문에 현재 사용자 검증은 하지않음
         *       [update - 21.11.18]
         */
        suspend fun getReviewTodo() : Observable<JobState.True.Result<List<ReviewTodoDTO>>> = withContext(ioDispatchers) {
            return@withContext Observable.create { emitter ->
                    firestore.collection(getFireStoreString(R.string.review_todo_collection))
                        .addSnapshotListener { value, error ->
                            LogUtil.i(Constants.TAG, "$THIS_NAME getReviewTodo() value : $value, error : $error")

                            if (error != null)
                                emitter.onError(error)
                            else {
                                value?.let { querySnapshot ->
                                    val tasks = querySnapshot.toObjects(ReviewTodoDTO::class.java)
                                        .sortedByDescending { it.date }
                                    emitter.onNext(JobState.True.Result(tasks))

                                } ?: emitter.onError(Exception())
                            }
                        }
                }
        }


    /**
     *  Gon : Firestore ReviewTodo Collection의 comments collection에 document(댓글 정보)를 추가하는 메서드
     *        [update - 21.12.14]
     */
    suspend fun insertComment(commentDTO: CommentDTO, reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME insertComment() called")

        return@withContext firebaseAuth.currentUser?.let { _ ->
            return@let try {
                var jobState : JobState = JobState.Uninitialized

                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(reviewTodoModel.memberEmail + reviewTodoModel.modelId)
                    .collection(getFireStoreString(R.string.review_comments_collection))
                    .document()
                    .set(commentDTO)
                    .addOnCompleteListener {
                        jobState = if (it.isSuccessful) {
                            LogUtil.d(Constants.TAG, "$THIS_NAME insertComment() JobState.True")
                            JobState.True
                        } else {
                            LogUtil.d(Constants.TAG, "$THIS_NAME insertComment() JobState.False")
                            JobState.False
                        }
                    }.await()

                jobState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME insertComment() JobState.Error")
                return@let JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME insertComment() JobState.Uninitialized")
            return@withContext JobState.Uninitialized
        }
    }

    /**
     *  Gon : Rx를 이용해 Firestore ReviewTodo Collection의 comments collection의 document가 변경될 때 마다 반환하는 메소드
     *        [update - 21.12.14]
     */
    suspend fun getComments(model: ReviewTodoModel): Observable<JobState.True.Result<List<CommentDTO>>> = withContext(ioDispatchers) {
        return@withContext Observable.create { emitter ->
            firebaseAuth.currentUser?.let { _ ->
                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(model.memberEmail + model.modelId)
                    .collection(getFireStoreString(R.string.review_comments_collection))
                    .addSnapshotListener { value, error ->
                        LogUtil.i(Constants.TAG, "$THIS_NAME getComments() value : $value, error : $error")

                        if (error != null)
                            emitter.onError(error)
                        else {
                            value?.let { querySnapshot ->
                                val tasks = querySnapshot.toObjects(CommentDTO::class.java)
                                    .sortedByDescending { it.date }
                                emitter.onNext(JobState.True.Result(tasks))

                            } ?: emitter.onError(Exception())
                        }
                    }
            }
        }
    }

    /**
     *  Gon : Firestore ReviewTodo Collection의 checked_member collection에 document(현재 사용자 정보)를 추가하는 메서드
     *        [update - 21.12.14]
     */
    suspend fun insertCheckedMember(reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME insertCheckedMember() called")

        return@withContext firebaseAuth.currentUser?.let { firebaseUser ->
            return@let try {
                var jobState : JobState = JobState.Uninitialized

                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(reviewTodoModel.memberEmail + reviewTodoModel.modelId)
                    .collection(getFireStoreString(R.string.checked_member_collection))
                    .document(firebaseUser.email.toString())
                    .set(mapOf(getFireStoreString(R.string.member_email) to firebaseUser.email))
                    .addOnCompleteListener {
                        jobState = if (it.isSuccessful) {
                            LogUtil.d(Constants.TAG, "$THIS_NAME insertCheckedMember() JobState.True")
                            JobState.True
                        } else {
                            LogUtil.d(Constants.TAG, "$THIS_NAME insertCheckedMember() JobState.False")
                            JobState.False
                        }
                    }.await()

                jobState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME insertCheckedMember() JobState.Error : $e")
                JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME insertCheckedMember() JobState.Uninitialized")
            JobState.Uninitialized
        }
    }

    /**
     *  Gon : Rx를 이용해 Firestore ReviewTodo Collection의 checked_member collection의 size가 변경될 때 마다 반환하는 메서드
     *        [update - 21.12.14]
     */
    suspend fun getCheckedMemberCount(model: ReviewTodoModel) : Observable<JobState.True.Result<Int>> = withContext(ioDispatchers) {

        return@withContext Observable.create { emitter ->
            firebaseAuth.currentUser?.let { _ ->
                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(model.memberEmail + model.modelId)
                    .collection(getFireStoreString(R.string.checked_member_collection))
                    .addSnapshotListener { value, error ->
                        LogUtil.i(Constants.TAG, "$THIS_NAME getCheckedMemberCount() value : $value, error : $error")

                        if (error != null)
                            emitter.onError(error)
                        else {
                            value?.let { querySnapshot ->
                                querySnapshot.documents.size.let { size ->
                                    emitter.onNext(JobState.True.Result(size))
                                }
                            } ?: emitter.onError(Exception())
                        }
                    }
            }
        }
    }

    /**
     *  Gon : Firestore ReviewTodo Collection의 checked_member collection에서 현재 사용자가 passButton을 클릭한 이력이 있는 확인하는 메서드
     *        [update - 21.12.14]
     */
    suspend fun getCheckedCurrentMember(model: ReviewTodoModel) : JobState = withContext(ioDispatchers) {
        return@withContext firebaseAuth.currentUser?.email?.let { firebaseUserEmail ->
            return@let try {
                LogUtil.v(Constants.TAG, "$THIS_NAME getCheckedCurrentUser()")

                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(model.memberEmail + model.modelId)
                    .collection(getFireStoreString(R.string.checked_member_collection))
                    .whereEqualTo(getFireStoreString(R.string.member_email), firebaseUserEmail)
                    .get()
                    .await()
                    .run {
                        if (!this.isEmpty) {
                            LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedCurrentMember() JobState.True.Result : true")
                            JobState.True.Result(true)
                        } else {
                            LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedCurrentMember() JobState.True.Result : false")
                            JobState.True.Result(false)
                        }
                    }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME getCheckedCurrentMember() JobState.Error : $e")
                JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedCurrentMember() JobState.Uninitialized")
            JobState.Uninitialized
        }
    }

    /**
     *  Gon : Firestore ReviewTodo Collection의 checked_member collection에서 현재 사용자의 정보를 삭제하는 메서드
     *        [update - 21.12.14]
     */
    suspend fun deleteCheckedMember(model: ReviewTodoModel) : JobState = withContext(ioDispatchers) {
        return@withContext firebaseAuth.currentUser?.email?.let { firebaseUserEmail ->
            try {
                LogUtil.v(Constants.TAG, "$THIS_NAME deleteCheckedMember()")
                var jobState : JobState = JobState.Uninitialized

                firestore.collection(getFireStoreString(R.string.review_todo_collection))
                    .document(model.memberEmail + model.modelId)
                    .collection(getFireStoreString(R.string.checked_member_collection))
                    .document(firebaseUserEmail)
                    .delete()
                    .addOnCompleteListener {
                        jobState = if (it.isSuccessful) {
                            LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedMember() JobState.True")
                            JobState.True
                        } else {
                            LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedMember() JobState.False")
                            JobState.False
                        }
                    }.await()

                jobState

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME deleteCheckedMember() JobState.Error : $e")
                JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedMember() JobState.Uninitialized")
            JobState.Uninitialized
        }
    }

    /**
     *  Gon : insertCheckedMember()의 반환값이 JobState.True이면 memberScore, memberRank를 변경하는 메서드 입니다.
     *        [update - 21.12.14]
     */
    suspend fun updateMemberScore(model: ReviewTodoModel): JobState = withContext(ioDispatchers) {
        return@withContext firebaseAuth.currentUser?.let { _ ->
            return@let try {
                LogUtil.v(Constants.TAG, "$THIS_NAME updateMemberScore()")
                var jobState : JobState = JobState.Uninitialized

                val result = getReviewTodoWriterMember(model.memberEmail)

                if (result is JobState.True.Result<*>) {
                    val memberScore = (result.data as DocumentSnapshot).get(getFireStoreString(R.string.member_score)) as Long + 10L

                    firestore.collection(getFireStoreString(R.string.member_collection))
                        .document(model.memberEmail)
                        .update(getFireStoreString(R.string.member_score), memberScore, getFireStoreString(R.string.member_rank), memberScore.toUserRank())
                        .addOnCompleteListener {
                            jobState = if (it.isSuccessful) {
                                LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedMember() JobState.True")
                                JobState.True
                            } else {
                                LogUtil.d(Constants.TAG, "$THIS_NAME deleteCheckedMember() JobState.False")
                                JobState.False
                            }
                        }.await()

                    jobState

                } else {
                    LogUtil.d(Constants.TAG, "$THIS_NAME updateMemberScore() -> getReviewTodoWriterMember() : $result")
                    result
                }

            } catch (e : Exception) {
                LogUtil.e(Constants.TAG, "$THIS_NAME updateMemberScore() JobState.Error : $e")
                JobState.Error(R.string.request_error, e)
            }
        } ?: kotlin.run {
            LogUtil.d(Constants.TAG, "$THIS_NAME updateMemberScore() JobState.Uninitialized")
            JobState.Uninitialized
        }
    }

    /**
     *  Gon : Firestore Member Collection에서 매개변수와 memberEmail field가 동일한 document를 반환하는 메소드
     *        [update - 21.12.14]
     */
    private suspend fun getReviewTodoWriterMember(memberEmail : String) : JobState = withContext(ioDispatchers) {
        return@withContext try {
            LogUtil.v(Constants.TAG, "$THIS_NAME getReviewTodoWriterMember() called")

            firestore.collection(getFireStoreString(R.string.member_collection))
                .document(memberEmail)
                .get()
                .await()
                .run {
                    if (this.exists()) {
                        LogUtil.v(Constants.TAG, "$THIS_NAME getReviewTodoWriterMember() : JobState.True")
                        JobState.True.Result(this)
                    } else {
                        LogUtil.v(Constants.TAG, "$THIS_NAME getReviewTodoWriterMember() : JobState.False")
                        JobState.False
                    }
                }

        } catch (e : Exception) {
            LogUtil.e(Constants.TAG, "$THIS_NAME getReviewTodoWriterMember() JobState.Error : $e")
            JobState.Error(R.string.request_error, e)
        }
    }
}