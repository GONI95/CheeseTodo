package sang.gondroid.cheesetodo.presentation.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.domain.mapper.MapperToCommentModel
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.FireStoreMemberModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.usecase.firestore.*
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil

class DetailReviewViewModel(
    private val insertCommentUseCase: InsertCommentUseCase,
    private val memberVerificationUseCase: MemberVerificationUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val insertCheckedUserUseCase: InsertCheckedUserUseCase,
    private val getCheckedCurrentUserUseCase: GetCheckedCurrentUserUseCase,
    private val getCheckedUserCountUseCase: GetCheckedUserCountUseCase,
    private val deleteCheckedUserUseCase: DeleteCheckedUserUseCase,
    private val toCommentModel: MapperToCommentModel,
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

    private var _insertCheckedUserJobStateLiveData = MutableLiveData<JobState>()
    val insertCheckedUserJobStateLiveData : LiveData<JobState>
        get() = _insertCheckedUserJobStateLiveData

    private var _getCheckedCurrentUserBooleanLiveData = MutableLiveData<Boolean>()
    val getCheckedCurrentUserBooleanLiveData : LiveData<Boolean>
        get() = _getCheckedCurrentUserBooleanLiveData

    private var _deleteCheckedUserLiveData = MutableLiveData<JobState>()
    val deleteCheckedUserLiveData : LiveData<JobState>
        get() = _deleteCheckedUserLiveData

    private var _getCheckedUserCountLiveData = MutableLiveData<Int>()
    val getCheckedUserCountLiveData : LiveData<Int>
        get() = _getCheckedUserCountLiveData

    override fun fetchData(): Job = viewModelScope.launch(ioDispatcher) {

    }

    /**
     * comment 객체 생성
     */
    private fun createCommentModel(commentValue: String, currentMemberModel: FireStoreMemberModel) : CommentModel {
        return CommentModel(
            id = null,
            userEmail = currentMemberModel.userEmail,
            userName = currentMemberModel.userName,
            userPhoto = currentMemberModel.userPhoto,
            userRank = currentMemberModel.userRank,
            userScore = currentMemberModel.userScore.toLong(),
            date = System.currentTimeMillis(),
            comment = commentValue
        )
    }

    fun insertComment(commentValue: String, reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        memberVerificationUseCase.invoke().run {
            when(this) {
                is JobState.Success.Registered<*> -> {
                    val commentModel = createCommentModel(commentValue, this.data as FireStoreMemberModel)
                    val insertCommentResult = insertCommentUseCase.invoke(commentModel, reviewTodoModel)
                    LogUtil.i(Constants.TAG, "$THIS_NAME insertComment() : $insertCommentResult")
                }
                is JobState.Success.NotRegistered -> _jobStateLiveData.postValue(this)
                is JobState.Error -> _jobStateLiveData.postValue(this)
                else -> LogUtil.w(Constants.TAG, "$THIS_NAME insertComment() else : $this")
            }
        }

    }

    fun getComments(model: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {

        getCommentsUseCase.invoke(model).run {
            this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableObserver<List<CommentDTO>>() {
                    override fun onComplete() {
                        LogUtil.d(Constants.TAG, "$THIS_NAME getComments() : onComplete")
                    }

                    override fun onNext(value: List<CommentDTO>) {
                        LogUtil.d(Constants.TAG, "$THIS_NAME getComments() : onNext : $value")

                        viewModelScope.launch(ioDispatcher) {
                            _getCommentJobStateLiveData.postValue(JobState.True.Result(value.map { toCommentModel.map(it) }))
                        }
                    }

                    override fun onError(e: Throwable) {
                        LogUtil.e(Constants.TAG, "$THIS_NAME getComments() : onError")
                        _getCommentJobStateLiveData.postValue(JobState.Error(R.string.request_error, e))
                    }
                })
        }
    }

    fun insertCheckedUser(reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        insertCheckedUserUseCase.invoke(reviewTodoModel).run {
            _insertCheckedUserJobStateLiveData.postValue(this)
        }
    }

    fun getCheckedCurrentUser(reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        getCheckedCurrentUserUseCase.invoke(reviewTodoModel).run {
            when(this) {
                is JobState.True -> {
                    _getCheckedCurrentUserBooleanLiveData.postValue(true)
                    LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedCurrentUser() ${getCheckedCurrentUserBooleanLiveData.value}")
                }
                is JobState.False -> {
                    LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedCurrentUser() True")
                    _getCheckedCurrentUserBooleanLiveData.postValue(false)
                }
                is JobState.Error -> {
                    LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedCurrentUser() Error")
                    _getCheckedCurrentUserBooleanLiveData.postValue(false)
                }
            }
        }
    }

    fun getCheckedUserCount(reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {

        getCheckedUserCountUseCase.invoke(reviewTodoModel).run {
            this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableObserver<Int>() {
                    override fun onComplete() {
                        LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedUserCount() : onComplete")
                    }

                    override fun onNext(value: Int) {
                        LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedUserCount() : onNext : $value")

                        viewModelScope.launch(ioDispatcher) {
                            _getCheckedUserCountLiveData.postValue(value)
                        }
                    }

                    override fun onError(e: Throwable) {
                        LogUtil.e(Constants.TAG, "$THIS_NAME getCheckedUserCount() : onError")
                        _getCheckedUserCountLiveData.postValue(0)
                    }
                })
        }
    }

    fun deleteUnCheckedUser(reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        deleteCheckedUserUseCase.invoke(reviewTodoModel).run {
            _deleteCheckedUserLiveData.postValue(this)
        }
    }
}