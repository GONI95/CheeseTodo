package sang.gondroid.myapplication.presentation.todocategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.domain.usecase.DeleteTodoUseCase
import sang.gondroid.myapplication.domain.usecase.UpdateTodoUseCase
import sang.gondroid.myapplication.presentation.base.BaseViewModel
import sang.gondroid.myapplication.util.JobState
import java.lang.Exception

class DetailTodoViewModel(private val updateTodoUseCase: UpdateTodoUseCase,
                          private val deleteTodoUseCase: DeleteTodoUseCase
): BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    private var _jobState : MutableLiveData<JobState> = MutableLiveData()
    val jobState : LiveData<JobState>
        get() = _jobState

    fun updateData(todoModel: TodoModel) = viewModelScope.launch {
        try {
            launch(Dispatchers.IO) {
                updateTodoUseCase.invoke(todoModel)
            }.join()

            _jobState.postValue(JobState.SUCCESS)

        } catch (e: Exception) {
            _jobState.postValue(JobState.ERROR)
            println(e)
        }
    }

    fun deleteData(id: Long?) = viewModelScope.launch {
        try {
            launch(Dispatchers.IO) {
                id?.let { deleteTodoUseCase.invoke(it) }
            }.join()

            _jobState.postValue(JobState.SUCCESS)

        } catch (e: Exception) {
            _jobState.postValue(JobState.ERROR)
            println(e)
        }
    }

    fun requestReview() { }
}