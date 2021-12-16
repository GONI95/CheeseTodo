package sang.gondroid.cheesetodo.presentation.todocategory

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.domain.usecase.DeleteTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.GetTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.UpdateTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.firestore.InsertReviewTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.firestore.ValidateReviewTodoExistUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import java.lang.Exception
import kotlin.properties.Delegates

class DetailTodoViewModel(
    private val bundle: Bundle,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val getTodoUseCase: GetTodoUseCase,
    private val insertReviewTodoUseCase: InsertReviewTodoUseCase,
    private val validateReviewTodoExistUseCase: ValidateReviewTodoExistUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    private var _updateDataLiveData = MutableLiveData<JobState>()
    val updateDataLiveData : LiveData<JobState>
        get() = _updateDataLiveData

    private var _JobStateLiveData = MutableLiveData<JobState>()
    val JobStateLiveData : LiveData<JobState>
        get() = _JobStateLiveData

    private var _todoModelLiveData = MutableLiveData<TodoModel>()
    val todoModelLiveData : LiveData<TodoModel>
        get() = _todoModelLiveData

    private var todoModelId by Delegates.notNull<Long>()

    init {
        todoModelId = bundle.let {
            it.getSerializable("TodoItemData") as TodoModel
        }.id!!
        LogUtil.v(Constants.TAG, "$THIS_NAME fetchData() $todoModelId")
    }

    override fun fetchData() = viewModelScope.launch {
        todoModelId.let {
            val result = getTodoUseCase.invoke(it)

            LogUtil.v(Constants.TAG, "$THIS_NAME fetchData() $result")
            _todoModelLiveData.postValue(result)
        }
    }

    fun updateData(todoModel: TodoModel) = viewModelScope.launch {
        LogUtil.v(Constants.TAG, "$THIS_NAME updateData() called")

        try {
            updateTodoUseCase.invoke(todoModel).let {
                _updateDataLiveData.postValue(JobState.True)
            }
            fetchData()

        } catch (e: Exception) {
            LogUtil.e(Constants.TAG, "$THIS_NAME updateData() JobState.Error")
            _updateDataLiveData.postValue(JobState.Error(R.string.request_error, e))
        }
    }

    fun deleteData(id: Long) = viewModelScope.launch {
        LogUtil.v(Constants.TAG, "$THIS_NAME deleteData() called")

        try {
            deleteTodoUseCase.invoke(id).let {
                _JobStateLiveData.postValue(JobState.True)
            }

        } catch (e: Exception) {
            LogUtil.e(Constants.TAG, "$THIS_NAME deleteData() JobState.Error")
            _JobStateLiveData.postValue(JobState.Error(R.string.request_error, e))
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
            memberEmail = firebaseUser.email!!,
            memberName = firebaseUser.displayName!!,
            memberPhoto = firebaseUser.photoUrl.toString(),
            category = model.category,
            date = currentMillis,
            title = model.title,
            todo = model.todo,
            difficult = model.difficult!!
        )
    }

    /**
     * Firebase 인증 시스템에 로그인한 User가 있으면 ReviewTodo 컬렉션에 document를 추가하는 작업을 요청
     */
    fun uploadReviewTodo(model: TodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.v(Constants.TAG, "$THIS_NAME uploadReviewTodo() called")

        firebaseAuth.currentUser?.let { firebaseUser ->
            val existState = validateReviewTodoExistUseCase.invoke(model)
            val reviewTodoModel = createReviewTodoModel(model, firebaseUser)

            when(existState) {
                is JobState.True -> {
                    val insertState = insertReviewTodoUseCase.invoke(reviewTodoModel)

                    _JobStateLiveData.postValue(insertState)
                }
                else -> _JobStateLiveData.postValue(existState)
            }
        } ?: kotlin.run { _JobStateLiveData.postValue(JobState.Uninitialized) }
    }
}