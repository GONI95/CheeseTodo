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
import sang.gondroid.cheesetodo.data.firebase.HandlerFirebaseAuth
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
    private val insertCheckedMemberUseCase: InsertCheckedMemberUseCase,
    private val getCheckedCurrentMemberUseCase: GetCheckedCurrentMemberUseCase,
    private val getCheckedMemberCountUseCase: GetCheckedMemberCountUseCase,
    private val deleteCheckedMemberUseCase: DeleteCheckedMemberUseCase,
    private val toCommentModel: MapperToCommentModel,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    // JobState 상태가 초기화되지 않은 값으로 초기화
    private var _memberVerificationLiveData = MutableLiveData<JobState>()
    val memberVerificationLiveData : LiveData<JobState>
        get() = _memberVerificationLiveData

    private var _getCommentLiveData = MutableLiveData<JobState>()
    val getCommentLiveData : LiveData<JobState>
        get() = _getCommentLiveData

    private var _getCheckedMemberCountLiveData = MutableLiveData<Int>()
    val getCheckedMemberCountLiveData : LiveData<Int>
        get() = _getCheckedMemberCountLiveData

    val getCheckedCurrentMemberBooleanLiveData = MutableLiveData<Boolean>()

    private var _jobStateLiveData = MutableLiveData<JobState>()
    val jobStateLiveData : LiveData<JobState>
        get() = _jobStateLiveData

    private lateinit var memberModel: FireStoreMemberModel

    override fun fetchData(): Job = viewModelScope.launch(ioDispatcher) {
        LogUtil.v(Constants.TAG, "$THIS_NAME fetchData() called")

        memberVerificationUseCase.invoke().let { result ->
            if (result is JobState.Success.Registered<*>) memberModel = result.data as FireStoreMemberModel
            _memberVerificationLiveData.postValue(result)
        }
    }

    /**
     * comment 객체 생성
     */
    private fun createCommentModel(commentValue: String, currentMemberModel: FireStoreMemberModel) : CommentModel {
        LogUtil.v(Constants.TAG, "$THIS_NAME createCommentModel() called")
        return CommentModel(
            id = null,
            memberEmail = currentMemberModel.memberEmail,
            memberName = currentMemberModel.memberName,
            memberPhoto = currentMemberModel.memberPhoto,
            memberRank = currentMemberModel.memberRank,
            memberScore = currentMemberModel.memberScore.toLong(),
            date = System.currentTimeMillis(),
            comment = commentValue
        )
    }


    fun insertComment(commentValue: String, reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.v(Constants.TAG, "$THIS_NAME insertComment() called")

        val commentModel = createCommentModel(commentValue, memberModel)
        insertCommentUseCase.invoke(commentModel, reviewTodoModel).let { result ->
            _jobStateLiveData.postValue(result)
        }
    }

    fun getComments(model: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.v(Constants.TAG, "$THIS_NAME getComments() called")

        getCommentsUseCase.invoke(model).run {
            this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableObserver<JobState.True.Result<List<CommentModel>>>() {
                    override fun onComplete() {
                        LogUtil.d(Constants.TAG, "$THIS_NAME getComments() : onComplete")
                    }

                    override fun onNext(value: JobState.True.Result<List<CommentModel>>) {
                        LogUtil.i(Constants.TAG, "$THIS_NAME getComments() : onNext : $value")

                        _getCommentLiveData.postValue(value)
                    }

                    override fun onError(e: Throwable) {
                        _getCommentLiveData.postValue(JobState.Error(R.string.request_error, e))
                    }
                })
        }
    }

    fun insertCheckedMember(reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.d(Constants.TAG, "$THIS_NAME insertCheckedMember() called")

        insertCheckedMemberUseCase.invoke(reviewTodoModel).let { result ->
            _jobStateLiveData.postValue(result)
        }
    }

    fun getCheckedCurrentMember(reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedCurrentMember() called")

        getCheckedCurrentMemberUseCase.invoke(reviewTodoModel).let { result ->
            _jobStateLiveData.postValue(result)

            if (result is JobState.True.Result<*>)
                getCheckedCurrentMemberBooleanLiveData.postValue(result.data as Boolean)
            else
                getCheckedCurrentMemberBooleanLiveData.postValue(false)
        }
    }

    fun getCheckedMemberCount(reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedMemberCount() called")

        getCheckedMemberCountUseCase.invoke(reviewTodoModel).run {
            this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableObserver<JobState.True.Result<Int>>() {
                    override fun onComplete() {
                        LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedMemberCount() : onComplete")
                    }

                    override fun onNext(value: JobState.True.Result<Int>) {
                        LogUtil.d(Constants.TAG, "$THIS_NAME getCheckedMemberCount() : onNext : $value")

                        _getCheckedMemberCountLiveData.postValue(value.data!!)
                    }

                    override fun onError(e: Throwable) {
                        _getCheckedMemberCountLiveData.postValue(0)
                    }
                })
        }
    }

    fun deleteUnCheckedMember(reviewTodoModel: ReviewTodoModel) = viewModelScope.launch(ioDispatcher) {
        LogUtil.d(Constants.TAG, "$THIS_NAME deleteUnCheckedMember() called")

        deleteCheckedMemberUseCase.invoke(reviewTodoModel).let { result ->
            _jobStateLiveData.postValue(result)
        }
    }
}