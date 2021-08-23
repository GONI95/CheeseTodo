package sang.gondroid.cheesetodo.presentation.todocategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.domain.usecase.DeleteTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.UpdateTodoUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import java.lang.Exception

class DetailTodoViewModel(private val updateTodoUseCase: UpdateTodoUseCase,
                          private val deleteTodoUseCase: DeleteTodoUseCase
): BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    private var _jobState : MutableLiveData<JobState> = MutableLiveData(JobState.Uninitialized)
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
}