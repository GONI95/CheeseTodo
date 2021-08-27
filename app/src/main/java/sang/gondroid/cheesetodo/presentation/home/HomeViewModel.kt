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
    private var _myStateLiveData = MutableLiveData<JobState>()
    val jobStateLiveData : LiveData<JobState>
        get() = _myStateLiveData

    override fun fetchData(): Job = viewModelScope.launch(ioDispatcher) {
        val myState = handlerFirebaseAuth.validateToken()
        val membershipState = handlerFireStore.validateMembership()

        if (myState is JobState.Login && membershipState is JobState.True) _myStateLiveData.postValue(JobState.Login(myState.idData, myState.nameData))

        else if(membershipState is JobState.False) _myStateLiveData.postValue(membershipState)

        else if(myState is JobState.Success.NotRegistered) _myStateLiveData.postValue(myState)

        else if(myState is JobState.Error) _myStateLiveData.postValue(myState)

        else if(membershipState is JobState.Error) _myStateLiveData.postValue(membershipState)

        else LogUtil.w(Constants.TAG, "$THIS_NAME validateMembership() else : $membershipState, $myState")
    }
}
