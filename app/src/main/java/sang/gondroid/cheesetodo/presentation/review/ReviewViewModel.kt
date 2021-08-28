package sang.gondroid.cheesetodo.presentation.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.data.firebase.HandleFireStore
import sang.gondroid.cheesetodo.domain.usecase.firestore.GetReviewTodoUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.JobState

class ReviewViewModel(
    private val getReviewTodoUseCase: GetReviewTodoUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    // JobState 상태가 초기화되지 않은 값으로 초기화
    private var _jobStateLiveData = MutableLiveData<JobState>()
    val jobStateLiveData : LiveData<JobState>
        get() = _jobStateLiveData

    override fun fetchData(): Job = viewModelScope.launch(ioDispatcher) {
        val getReviewTodoState = getReviewTodoUseCase.invoke()

        if (getReviewTodoState is JobState.True.Result<*>) _jobStateLiveData.postValue(getReviewTodoState)

        else if(getReviewTodoState is JobState.Error) _jobStateLiveData.postValue(getReviewTodoState)

        else _jobStateLiveData.postValue(getReviewTodoState)
    }

}