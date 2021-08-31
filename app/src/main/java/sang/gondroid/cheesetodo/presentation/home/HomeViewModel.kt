package sang.gondroid.cheesetodo.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import sang.gondroid.cheesetodo.data.firebase.HandleFireStore
import sang.gondroid.cheesetodo.data.firebase.HandlerFirebaseAuth
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil

class HomeViewModel(
    private val handlerFirebaseAuth: HandlerFirebaseAuth,
    private val handlerFireStore: HandleFireStore,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    // MyState의 상태가 초기화되지 않은 값으로 초기화
    private var _jobStateLiveData = MutableLiveData<JobState>()
    val jobStateLiveData : LiveData<JobState>
        get() = _jobStateLiveData

    override fun fetchData(): Job = viewModelScope.launch(ioDispatcher) {

        when (val myState = handlerFirebaseAuth.validateToken()) {
            is JobState.Login -> {

                when(val membershipState = handlerFireStore.validateMembership()) {

                    is JobState.True -> _jobStateLiveData.postValue(myState)
                    is JobState.False -> _jobStateLiveData.postValue(membershipState)
                    is JobState.Error -> _jobStateLiveData.postValue(membershipState)
                    else -> LogUtil.w(Constants.TAG, "$THIS_NAME validateMembership() else : $membershipState")
                }
            }
            is JobState.Success.NotRegistered -> _jobStateLiveData.postValue(myState)
            is JobState.Error -> _jobStateLiveData.postValue(myState)
            else -> LogUtil.w(Constants.TAG, "$THIS_NAME validateToken() else : $myState")
        }
    }
}
