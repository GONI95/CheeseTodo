package sang.gondroid.cheesetodo.presentation.my

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.firebase.CheckFirebaseAuth
import sang.gondroid.cheesetodo.data.firebase.HandleFireStore
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.MyState
import java.lang.Exception

class MyViewModel(
    private val appPreferenceManager: AppPreferenceManager,
    private val ioDispatchers: CoroutineDispatcher,
    private val checkFirebaseAuth: CheckFirebaseAuth,
    private val handlerFireStore: HandleFireStore,
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

        val myState = checkFirebaseAuth.validateToken()

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
            Log.d(Constants.TAG, "$THIS_NAME saveToken() called : $disPlayName")

            appPreferenceManager.putIdToken(idToken)
            appPreferenceManager.setUserNameString(disPlayName)

            fetchData()
        }
    }

    /**
     * Token이 Preference에 저장되어 있어, Firebase currentUser와 Firestore Users 정보를 검증
     */
    fun validateUser() = viewModelScope.launch(ioDispatchers) {
        Log.d(Constants.TAG, "$THIS_NAME validateUser() called")

        /**
         * 1. Firebase Current User 정보를 가져오는 메서드
         * 2. Firestore Users에서 현재 Firebase Auth 데이터베이스의 사용자 정보와 동일한 정보가 있는지 확인하고 Firestore에 추가하는 메서드
         */
        val membershipState = handlerFireStore.validateMembership()
        val userState = checkFirebaseAuth.validateCurrentUser()

        if (userState is MyState.Success.Registered<*, *> && membershipState is MyState.True) {
            Log.d(Constants.TAG, "$THIS_NAME validateUser() Complete")
            _myStateLiveData.postValue(userState)
        }
        else if(userState is MyState.Error) {
            Log.d(Constants.TAG, "$THIS_NAME validateUser() userState Error")
            _myStateLiveData.postValue(userState)

        } else {
            Log.d(Constants.TAG, "$THIS_NAME validateUser() membershipState Error")
            _myStateLiveData.postValue(membershipState)
        }
    }

    /**
     * Preference에 Token 삭제 후 fetchData() 호출
     */
    fun signOut() = viewModelScope.launch(ioDispatchers){
        Log.d(Constants.TAG, "$THIS_NAME signOut() called")

        withContext(Dispatchers.IO) {
            appPreferenceManager.removeIdToken()
            appPreferenceManager.removeUserNameString()
        }

        fetchData()
    }
}