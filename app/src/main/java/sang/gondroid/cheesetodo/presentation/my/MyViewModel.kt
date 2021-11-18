package sang.gondroid.cheesetodo.presentation.my

import android.app.job.JobInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.firebase.HandlerFireStore
import sang.gondroid.cheesetodo.data.firebase.HandlerFirebaseAuth
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.domain.usecase.firestore.GetCurrentMembershipUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.JobState

class MyViewModel(
    private val appPreferenceManager: AppPreferenceManager,
    private val handlerFirebaseAuth: HandlerFirebaseAuth,
    private val handlerFireStore: HandlerFireStore,
    private val getCurrentMembershipUseCase: GetCurrentMembershipUseCase,
    private val ioDispatchers: CoroutineDispatcher
) : BaseViewModel() {

    private val THIS_NAME = this::class.simpleName

    // MyState의 상태가 초기화되지 않은 값으로 초기화
    private var _jobStateLiveData = MutableLiveData<JobState>()
    val jobStateLiveData : LiveData<JobState>
        get() = _jobStateLiveData

    private var _dsjnMembJobStateLiveData = MutableLiveData<JobState>()
    val dsjnMembJobStateLiveData : LiveData<JobState>
        get() = _dsjnMembJobStateLiveData

    /**
     * Preference에 저장된 Token 유무에 따라 현재 로그인이 되었는지 체크하는 작업 : Loading, Login, NotRegistered 핸들링
     */
    override fun fetchData(): Job = viewModelScope.launch(ioDispatchers) {
        LogUtil.v(Constants.TAG, "$THIS_NAME fetchData() called")
        _jobStateLiveData.postValue(JobState.Loading)

        when (val myState = handlerFirebaseAuth.validateToken()) {
            is JobState.Login -> _jobStateLiveData.postValue(myState)
            is JobState.Error -> _jobStateLiveData.postValue(myState)
            else -> _jobStateLiveData.postValue(myState)
        }
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
        when (val membershipState = handlerFireStore.validateMembership()) {
            is JobState.True -> {

                when(val userState = getCurrentMembershipUseCase.invoke()) {
                    is JobState.Success.Registered<*> -> _jobStateLiveData.postValue(userState)
                    is JobState.Success.NotRegistered -> _jobStateLiveData.postValue(userState)
                    is JobState.Error -> _jobStateLiveData.postValue(userState)
                    else -> LogUtil.w(Constants.TAG, "$THIS_NAME getCurrentMembership() else : $userState")
                }
            }
            is JobState.False -> _jobStateLiveData.postValue(membershipState)
            is JobState.Error -> _jobStateLiveData.postValue(membershipState)
            else -> LogUtil.w(Constants.TAG, "$THIS_NAME validateMembership() else : $membershipState")
        }
    }

    /**
     * Preference에 Token 삭제 후 fetchData() 호출
     */
    fun removeToken() = viewModelScope.launch(ioDispatchers){
        LogUtil.v(Constants.TAG, "$THIS_NAME removeToken() called")

        appPreferenceManager.removeIdToken()
        appPreferenceManager.removeUserNameString()

        fetchData()
    }

    /**
     * Gon : Firestore Users, ReviewTodo collection에서 회원정보를 성공적으로 삭제하면, Firebase Auth Database에서 현재 사용자 정보를 삭제합니다.
     *       [update - 21.11.17]
     */
    fun disjoinMembership() = viewModelScope.launch(ioDispatchers) {

        val dltRvTdResult = handlerFireStore.deleteReviewTodoOwnedByMember().also { jobState ->
            when(jobState) {
                is JobState.True ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() 회원의 ReviewTodo 삭제 성공")
                is JobState.False ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() 회원의 ReviewTodo 삭제 실패")
                is JobState.Error ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() Error 발생 : ${jobState.e}")
                else ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteReviewTodoOwnedByMember() Firebase 인증 시스템 로그인되지 않음")
            }
        }

        val dltMembResult = handlerFireStore.deleteMemberInfo().also { jobState ->
            when(jobState) {
                is JobState.True ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteMemberInfo() 회원정보 삭제 성공")
                is JobState.False ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteMemberInfo() 회원정보 삭제 실패")
                is JobState.Error ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteMemberInfo() Error 발생 : ${jobState.e}")
                else ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME deleteMemberInfo() Firebase 인증 시스템 로그인되지 않음")
            }
        }

        if (dltRvTdResult == JobState.True && dltMembResult == JobState.True) {
            handlerFirebaseAuth.deleteCurrentUser( result = { jobState ->

                _dsjnMembJobStateLiveData.postValue(jobState)

                when(jobState) {
                    is JobState.True ->
                        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCurrentUser() Firebase 인증 시스템 회원정보 삭제 성공")
                    is JobState.False ->
                        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCurrentUser() Firebase 인증 시스템 회원정보 삭제 실패")
                    is JobState.Error ->
                        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCurrentUser() Error 발생 : ${jobState.e}")
                    else ->
                        LogUtil.d(Constants.TAG, "$THIS_NAME deleteCurrentUser() Firebase 인증 시스템 로그인되지 않음")
                }
            })
        }
    }
}