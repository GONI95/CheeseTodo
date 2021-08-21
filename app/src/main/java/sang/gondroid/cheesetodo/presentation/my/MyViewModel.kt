package sang.gondroid.cheesetodo.presentation.my

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import org.koin.ext.getScopeId
import sang.gondroid.cheesetodo.R
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


    /**
     * Preference에 저장된 Token 유무에 따라 현재 로그인이 되었는지 체크하는 작업 : Loading, Login, NotRegistered 핸들링
     */
    override fun fetchData(): Job = viewModelScope.launch(ioDispatchers) {
        Log.d(Constants.TAG, "$THIS_NAME fetchData() called")
        _myStateLiveData.postValue(MyState.Loading)

        val myState = checkFirebaseAuth.checkToken()

        if (myState is MyState.Login)
            _myStateLiveData.postValue(MyState.Login(myState.idData, myState.nameData)) // idToken이 저장된 경우 : Login 상태

        else if(myState is MyState.Error)
            _myStateLiveData.postValue(myState)

        else
            _myStateLiveData.postValue(myState)

    }

    /**
     * Preference에 Token 저장 후 fetchData() 호출
     */
    fun saveToken(idToken: String, disPlayName: String) = viewModelScope.launch(ioDispatchers){
        withContext(Dispatchers.IO) {
            Log.d(Constants.TAG, "$THIS_NAME saveToken() called")

            appPreferenceManager.putIdToken(idToken)
            appPreferenceManager.setUserNameString(disPlayName)

            fetchData()
        }
    }

    /**
     * Firebase Current User 정보를 가져오는 validateCurrentUser() : Registered, NotRegistered 핸들링
     */
    fun validateCurrentUser() = viewModelScope.launch(ioDispatchers) {
        Log.d(Constants.TAG, "$THIS_NAME validateCurrentUser() called")

            val myState = checkFirebaseAuth.validateCurrentUser()

            if (myState is MyState.Success.Registered<*, *>) _myStateLiveData.postValue(myState)
            else if(myState is MyState.Error) _myStateLiveData.postValue(myState)
            else _myStateLiveData.postValue(myState)

    }

    /**
     * Preference에 Token 삭제 후 fetchData() 호출
     */
    fun signOut() = viewModelScope.launch(ioDispatchers){
        withContext(Dispatchers.IO) {
            appPreferenceManager.removeIdToken()    // 토큰 지우기()
            appPreferenceManager.removeUserNameString()
        }

        fetchData()
    }
}