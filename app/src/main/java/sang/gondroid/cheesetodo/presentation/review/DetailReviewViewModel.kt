package sang.gondroid.cheesetodo.presentation.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.FireStoreMembershipModel
import sang.gondroid.cheesetodo.domain.usecase.firestore.GetCommentsUseCase
import sang.gondroid.cheesetodo.domain.usecase.firestore.GetCurrentMembershipUseCase
import sang.gondroid.cheesetodo.domain.usecase.firestore.InsertCommentUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil

class DetailReviewViewModel(
    private val insertCommentUseCase: InsertCommentUseCase,
    private val getCurrentMembershipUseCase: GetCurrentMembershipUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    // JobState 상태가 초기화되지 않은 값으로 초기화
    private var _jobStateLiveData = MutableLiveData<JobState>()
    val jobStateLiveData : LiveData<JobState>
        get() = _jobStateLiveData

    private var _getCommentJobStateLiveData = MutableLiveData<JobState>()
    val getCommentJobStateLiveData : LiveData<JobState>
        get() = _getCommentJobStateLiveData

    override fun fetchData(): Job = viewModelScope.launch(ioDispatcher) {

    }

    /**
     * comment 객체 생성
     */
    private fun createCommentModel(commentValue: String, currentMembershipModel: FireStoreMembershipModel) : CommentModel {
        return CommentModel(
            id = null,
            userEmail = currentMembershipModel.userEmail,
            userName = currentMembershipModel.userName,
            userPhoto = currentMembershipModel.userPhoto.toString(),
            userRank = currentMembershipModel.userRank,
            userScore = currentMembershipModel.userScore.toLong(),
            date = System.currentTimeMillis(),
            comment = commentValue
        )
    }

    fun insertComment(commentValue: String, modelId: Long) = viewModelScope.launch(ioDispatcher) {
        getCurrentMembershipUseCase.invoke().run {
            when(this) {
                is JobState.Success.Registered<*> -> {
                    val commentModel = createCommentModel(commentValue, this.data as FireStoreMembershipModel)
                    val insertCommentResult = insertCommentUseCase.invoke(commentModel, modelId)
                    LogUtil.i(Constants.TAG, "$THIS_NAME insertComment() : $insertCommentResult")
                }
                is JobState.Success.NotRegistered -> _jobStateLiveData.postValue(this)
                is JobState.Error -> _jobStateLiveData.postValue(this)
                else -> LogUtil.w(Constants.TAG, "$THIS_NAME insertComment() else : $this")
            }
        }

    }

    fun getComments(modelId: Long) = viewModelScope.launch(ioDispatcher) {
        getCommentsUseCase.invoke(modelId).run {
            LogUtil.i(Constants.TAG, "$THIS_NAME getComments() JobState : $this")
            _getCommentJobStateLiveData.postValue(this)
        }
    }
}