package sang.gondroid.cheesetodo.presentation.my

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import sang.gondroid.cheesetodo.data.firebase.HandleFireStore
import sang.gondroid.cheesetodo.data.firebase.HandlerFirebaseAuth
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.MyState

class MyViewModel(
    private val appPreferenceManager: AppPreferenceManager,
    private val ioDispatchers: CoroutineDispatcher,
    private val handlerFirebaseAuth: HandlerFirebaseAuth,
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
        LogUtil.v(Constants.TAG, "$THIS_NAME fetchData() called")
        _myStateLiveData.postValue(MyState.Loading)

        val myState = handlerFirebaseAuth.validateToken()

        if (myState is MyState.Login) _myStateLiveData.postValue(MyState.Login(myState.idData, myState.nameData))

        else if(myState is MyState.Error) _myStateLiveData.postValue(myState)

        else _myStateLiveData.postValue(myState)

    }

    /**
     * Preference에 Token 저장 후 fetchData() 호출
     */
    fun saveToken(idToken: String, disPlayName: String) = viewModelScope.launch(ioDispatchers){
        withContext(Dispatchers.IO) {
            LogUtil.v(Constants.TAG, "$THIS_NAME saveToken() called")
            LogUtil.d(Constants.TAG, "$THIS_NAME saveToken() : $idToken, $disPlayName")

            appPreferenceManager.putIdToken(idToken)
            appPreferenceManager.setUserNameString(disPlayName)

            fetchData()
        }
    }

    /**
     * Token이 Preference에 저장되어 있어, Firebase currentUser와 Firestore Users 정보를 검증
     */
    fun validateMembership() = viewModelScope.launch(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME validateMembership() called")

        /**
         * 1. Firebase 인증 시스템 데이터베이스에서 현재 사용자 정보를 가져오는 메서드
         * 2. Firestore Users에서 현재 Firebase Auth 데이터베이스의 사용자 정보와 동일한 정보가 있는지 확인하고 Firestore에 추가하는 메서드
         */
        val membershipState = handlerFireStore.validateMembership()
        val userState = handlerFirebaseAuth.getCurrentUser()

        if (userState is MyState.Success.Registered<*, *> && membershipState is MyState.True) _myStateLiveData.postValue(userState)

        else if(membershipState is MyState.False) _myStateLiveData.postValue(membershipState)

        else if(membershipState is MyState.Error) _myStateLiveData.postValue(membershipState)

        else if(userState is MyState.Error) _myStateLiveData.postValue(userState)

        else LogUtil.w(Constants.TAG, "$THIS_NAME disjoinMembership() else : $membershipState, $userState")
    }

    /**
     * Preference에 Token 삭제 후 fetchData() 호출
     */
    fun removeToken() = viewModelScope.launch(ioDispatchers){
        LogUtil.v(Constants.TAG, "$THIS_NAME removeToken() called")

        appPreferenceManager.removeIdToken()
        appPreferenceManager.removeUserNameString()

        handlerFirebaseAuth.signout()

        fetchData()
    }

    /**
     * Firebase Auth, Firestore User Collection에서 현재 로그인한 User 정보의 삭제를 요청하는 메서드
     */
    fun disjoinMembership() = viewModelScope.launch(ioDispatchers){

        /**
         * 1. Firestore Users에서 현재 Firebase Auth 데이터베이스의 사용자 정보와 동일한 정보를 제거하는 메서드
         * 2. Firebase 인증 시스템 데이터베이스에서 현재 사용자 정보를 제거하는 메서드
         */
        val disjoinMembershipState = handlerFireStore.disjoinMembership()
        val deleteCurrentUserState = handlerFirebaseAuth.deleteCurrentUser()

        if (disjoinMembershipState is MyState.True && deleteCurrentUserState is MyState.True) removeToken()

        else if(disjoinMembershipState is MyState.False) _myStateLiveData.postValue(disjoinMembershipState)

        else if(deleteCurrentUserState is MyState.False) _myStateLiveData.postValue(deleteCurrentUserState)

        else if(disjoinMembershipState is MyState.Error) _myStateLiveData.postValue(disjoinMembershipState)

        else if(deleteCurrentUserState is MyState.Error) _myStateLiveData.postValue(deleteCurrentUserState)

        else LogUtil.w(Constants.TAG, "$THIS_NAME disjoinMembership() else : $disjoinMembershipState, $deleteCurrentUserState")

    }
}