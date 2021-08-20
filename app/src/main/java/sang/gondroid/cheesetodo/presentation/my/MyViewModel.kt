package sang.gondroid.cheesetodo.presentation.my

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import sang.gondroid.cheesetodo.data.firebase.CheckFirebaseAuth
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.MyState

class MyViewModel(
    private val appPreferenceManager: AppPreferenceManager,
    private val ioDispatchers : CoroutineDispatcher,
    private val checkFirebaseAuth: CheckFirebaseAuth
) : BaseViewModel() {

    private val THIS_NAME = this::class.simpleName

    // MyState의 상태가 초기화되지 않은 값으로 초기화
    private var _myStateLiveData = MutableLiveData<MyState>(MyState.Uninitialized)
    val myStateLiveData : LiveData<MyState>
        get() = _myStateLiveData

    override fun fetchData(): Job = viewModelScope.launch(ioDispatchers) {
        Log.d(Constants.TAG, "$THIS_NAME fetchData() called")
        _myStateLiveData.postValue(MyState.Loading)

        val myState = checkFirebaseAuth.checkToken()

        if (myState is MyState.Login) {
            _myStateLiveData.postValue(MyState.Login(myState.idData, myState.nameData)) // idToken이 저장된 경우 : Login 상태
        } else {
            _myStateLiveData.postValue(MyState.Success.NotRegistered)
        }
    }

    fun saveToken(idToken: String, disPlayName: String) = viewModelScope.launch(ioDispatchers){
        withContext(Dispatchers.IO) {
            Log.d(Constants.TAG, "$THIS_NAME saveToken() called")
            appPreferenceManager.putIdToken(idToken)
            appPreferenceManager.setUserNameString(disPlayName)

            fetchData()
        }
    }
}