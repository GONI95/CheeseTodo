package sang.gondroid.myapplication.presentation.todocategory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.domain.usecase.InsertTodoUseCase
import sang.gondroid.myapplication.presentation.base.BaseViewModel
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.JobState
import java.lang.Exception

class InsertTodoViewModel(
    private val insertTodoUseCase: InsertTodoUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    private var _jobState : MutableLiveData<JobState> = MutableLiveData()
    val jobState : LiveData<JobState>
        get() = _jobState

    fun insertData(todoModel: TodoModel) = viewModelScope.launch(ioDispatcher) {
        try {
            val returnId = insertTodoUseCase.invoke(todoModel)
            Log.d(Constants.TAG,"$THIS_NAME insertData() : Insert 작업 반환값 $returnId")

            _jobState.postValue(JobState.SUCCESS)

        } catch (e : Exception) {
            _jobState.postValue(JobState.ERROR)
            println(e)
        }
    }
}