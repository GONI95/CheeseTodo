package sang.gondroid.cheesetodo.presentation.todocategory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.domain.usecase.InsertTodoUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import java.lang.Exception

class InsertTodoViewModel(
    private val insertTodoUseCase: InsertTodoUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    private var _jobState : MutableLiveData<JobState> = MutableLiveData(JobState.Uninitialized)
    val jobState : LiveData<JobState>
        get() = _jobState

    fun insertData(todoModel: TodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.v(Constants.TAG,"$THIS_NAME insertData() called")

        try {
            val returnId = insertTodoUseCase.invoke(todoModel)
            LogUtil.i(Constants.TAG,"$THIS_NAME insertData() : return value of Insert function $returnId")

            _jobState.postValue(JobState.True)

        } catch (e : Exception) {
            LogUtil.e(Constants.TAG, "$THIS_NAME insertData() JobState.Error")
            _jobState.postValue(JobState.Error(R.string.request_error, e))
        }
    }
}