package sang.gondroid.cheesetodo.presentation.todocategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.domain.usecase.DeleteTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.UpdateTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.firestore.InsertReviewTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.firestore.ValidateReviewTodoExistUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import java.lang.Exception

class DetailTodoViewModel(private val updateTodoUseCase: UpdateTodoUseCase,
                          private val deleteTodoUseCase: DeleteTodoUseCase,
                          private val insertReviewTodoUseCase: InsertReviewTodoUseCase,
                          private val validateReviewTodoExistUseCase: ValidateReviewTodoExistUseCase,
                          private val firebaseAuth: FirebaseAuth,
                          private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    private var _jobState : MutableLiveData<JobState> = MutableLiveData()
    val jobState : LiveData<JobState>
        get() = _jobState

    fun updateData(todoModel: TodoModel) = viewModelScope.launch {
        LogUtil.v(Constants.TAG, "$THIS_NAME updateData() called")

        try {
            launch(Dispatchers.IO) {
                updateTodoUseCase.invoke(todoModel)
            }.join()

            _jobState.postValue(JobState.True)

        } catch (e: Exception) {
            LogUtil.e(Constants.TAG, "$THIS_NAME updateData() JobState.Error")
            _jobState.postValue(JobState.Error(R.string.request_error, e))
        }
    }

    fun deleteData(id: Long?) = viewModelScope.launch {
        LogUtil.v(Constants.TAG, "$THIS_NAME deleteData() called")

        try {
            launch(Dispatchers.IO) {
                id?.let { deleteTodoUseCase.invoke(it) }
            }.join()

            _jobState.postValue(JobState.True)

        } catch (e: Exception) {
            LogUtil.e(Constants.TAG, "$THIS_NAME deleteData() JobState.Error")
            _jobState.postValue(JobState.Error(R.string.request_error, e))
        }
    }

    /**
     * Firestore의 ReviewTodo 컬렉션에 추가할 ReviewTodoModel 생성
     */
    private fun createReviewTodoModel(model: TodoModel, firebaseUser: FirebaseUser) : ReviewTodoModel {
        val currentMillis = System.currentTimeMillis()

        return ReviewTodoModel(
            id = null,
            modelId = model.id!!,
            userEmail = firebaseUser.email!!,
            userName = firebaseUser.displayName!!,
            userPhoto = firebaseUser.photoUrl.toString(),
            category = model.category,
            passOrNot = false,
            date = currentMillis,
            title = model.title,
            todo = model.todo,
            difficult = model.difficult!!,
            comments = null
        )
    }

    /**
     * Firebase 인증 시스템에 로그인한 User가 있으면 ReviewTodo 컬렉션에 document를 추가하는 작업을 요청
     */
    fun uploadReviewTodo(model: TodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.v(Constants.TAG, "$THIS_NAME uploadReviewTodo() called")

        firebaseAuth.currentUser?.let { firebaseUser ->
            val existState = validateReviewTodoExistUseCase.invoke(model)

            if (existState is JobState.True) {
                val reviewTodoModel = createReviewTodoModel(model, firebaseUser)
                insertReviewTodoUseCase.invoke(reviewTodoModel)
            }
            else if (existState is JobState.False) _jobState.postValue(existState)
            else _jobState.postValue(existState)
        } ?: kotlin.run { _jobState.postValue(JobState.Uninitialized) }
    }
}