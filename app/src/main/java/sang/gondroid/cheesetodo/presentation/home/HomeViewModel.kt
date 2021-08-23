package sang.gondroid.cheesetodo.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.data.firebase.HandlerFirebaseAuth
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil

class HomeViewModel(
    private val handlerFirebaseAuth: HandlerFirebaseAuth
) : BaseViewModel() {

    private val THIS_NAME = this::class.simpleName

    // MyState의 상태가 초기화되지 않은 값으로 초기화
    private var _myStateLiveData = MutableLiveData<JobState>(JobState.Uninitialized)
    val jobStateLiveData : MutableLiveData<JobState>
        get() = _myStateLiveData

    override fun fetchData() = viewModelScope.launch {
        LogUtil.v(Constants.TAG, "$THIS_NAME fetchData() called")

        val myState = handlerFirebaseAuth.validateToken()

        if (myState is JobState.Login) _myStateLiveData.postValue(JobState.Login(myState.idData, myState.nameData))

        else if(myState is JobState.Error) _myStateLiveData.postValue(myState)

        else _myStateLiveData.postValue(myState)
    }
}
