package sang.gondroid.cheesetodo.presentation.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import sang.gondroid.cheesetodo.data.firebase.HandlerFireStore
import sang.gondroid.cheesetodo.data.firebase.HandlerFirebaseAuth
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.domain.usecase.firestore.DeleteAccountUseCase
import sang.gondroid.cheesetodo.domain.usecase.firestore.MemberVerificationUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.JobState

class MyViewModel(
    private val appPreferenceManager: AppPreferenceManager,
    private val handlerFirebaseAuth: HandlerFirebaseAuth,
    private val memberVerificationUseCase: MemberVerificationUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val ioDispatchers: CoroutineDispatcher
) : BaseViewModel() {

    private val THIS_NAME = this::class.simpleName

    private var _signInStateLiveData = MutableLiveData<JobState>()
    val signInStateLiveData : LiveData<JobState>
        get() = _signInStateLiveData

    private var _deleteAccountLiveData = MutableLiveData<JobState>()
    val deleteAccountLiveData : LiveData<JobState>
        get() = _deleteAccountLiveData

    /**
     * Gon : 사용자의 Google 계정 정보가 담긴 객체인 GoogleSignInAccount로부터 idToken, disPlayName을 SharedPreference에 저장
     *       saveToken()가 호출되려면 Google 로그인 이벤트로 인해 onResume()이 호출되어야하고 BaseFragment()에서 onResume() 호출 시 fetchData()를 호출합니다.
     *       [update - 21.12.3]
     */
    fun saveToken(idToken: String, disPlayName: String) = viewModelScope.launch(ioDispatchers){
        withContext(Dispatchers.IO) {
            LogUtil.v(Constants.TAG, "$THIS_NAME saveToken() called : $idToken, $disPlayName")

            appPreferenceManager.putIdTokenString(idToken)
            appPreferenceManager.putDisPlayNameString(disPlayName)
        }
    }

    /**
     * Gon : SharedPreference로부터 사용자의 Google 계정 정보인 idToken, disPlayName을 null로 초기화
     *       사용자 정보가 초기화 되었다는 것은 로그아웃, 회원탈퇴를 했다는 것이기 때문에 fetchData()가 호출되어야 합니다.
     *       [update - 21.12.3]
     */
    fun removeToken() = viewModelScope.launch(ioDispatchers){
        LogUtil.v(Constants.TAG, "$THIS_NAME removeToken() called")

        appPreferenceManager.clearIdTokenString()
        appPreferenceManager.clearDisPlayNameString()

        fetchData()
    }

    /**
     * Gon : SharedPreference로부터 Firebase ID Toeken(자격증명) 값의 유무에 따라 Firebase Authentication에 지정된 자격증명을 통해 로그인을 시도하는 signInWithCredential() 호출
     *       [update - 21.12.3]
     */
    override fun fetchData(): Job = viewModelScope.launch(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME fetchData() called")
        _signInStateLiveData.postValue(JobState.Loading)

        handlerFirebaseAuth.signInWithCredential { result ->
            _signInStateLiveData.postValue(result)
        }
    }

    /**
     * Gon : Firestore User 콜렉션에서 사용자 정보를 가져오거나, Firestore User 콜렉션에 사용자 정보를 추가하는 memberVerification() 호출
     *       [update - 21.12.3]
     */
    fun memberVerification() = viewModelScope.launch(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME memberVerification() called")
        _signInStateLiveData.postValue(JobState.Loading)
        
        memberVerificationUseCase.invoke().let { result ->
            _signInStateLiveData.postValue(result)
        }
    }
    
    /**
     * Gon : Firestore Users, ReviewTodo collection에서 회원정보를 성공적으로 삭제하면, Firebase Auth Database에서 현재 사용자 정보를 삭제하는 deleteAccount() 호출
     *       [update - 21.12.3]
     */
    fun deleteAccount() = viewModelScope.launch(ioDispatchers) {
        _deleteAccountLiveData.postValue(JobState.Loading)

        deleteAccountUseCase.invoke().let {
            if (it is JobState.True) {

                handlerFirebaseAuth.deleteCurrentUser { result ->
                    _deleteAccountLiveData.postValue(result)
                }

            }
        }
    }
}