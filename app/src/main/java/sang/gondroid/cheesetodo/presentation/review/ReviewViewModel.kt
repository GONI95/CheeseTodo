package sang.gondroid.cheesetodo.presentation.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.SearchHistoryModel
import sang.gondroid.cheesetodo.domain.usecase.firestore.GetReviewTodoUseCase
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.toDateFormat
import java.util.*

class ReviewViewModel(
    private val getReviewTodoUseCase: GetReviewTodoUseCase,
    private val appPreferenceManager: AppPreferenceManager,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    // JobState 상태가 초기화되지 않은 값으로 초기화
    private var _jobStateLiveData = MutableLiveData<JobState>()
    val jobStateLiveData : LiveData<JobState>
        get() = _jobStateLiveData

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean>
        get() = _isLoading

    /**
     * Gon : 최종적으로 HandlerFireStore.kt getReviewTodo() 메서드를 호출하여, Firestore ReviewTodo Collection에서 모든 정보를 수신받습니다.
     *       [update - 21.11.18]
     */
    override fun fetchData(): Job = viewModelScope.launch(ioDispatcher) {
        getReviewTodoUseCase.invoke().also { jobState ->
            _jobStateLiveData.postValue(jobState)

            when(jobState) {
                is JobState.True.Result<*> ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME HandlerFireStore getReviewTodo() ReviewTodo 가져오기 성공")
                is JobState.Error ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME HandlerFireStore getReviewTodo() Error 발생 : ${jobState.e}")
                else ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME HandlerFireStore getReviewTodo() 알 수 없는 반환값")
            }
        }
    }

    /**
     * SwipeRefreshLayout의 refreshing 작업
     */
    fun refresh() {
        LogUtil.i(Constants.TAG, "$THIS_NAME onRefresh() called")
        fetchData()
        _isLoading.value = false
    }

    /**
     * Switch 상태값 변경 시 호출되는 메서드 (검색어 저장모드)
     */
    fun onHistoryCheckedChanged(isChecked: Boolean) {
        when (isChecked) {
            true -> {
                LogUtil.v(Constants.TAG, "$THIS_NAME, onHistoryCheckedChanged() : 검색어 저장기능 활성화")
                appPreferenceManager.putSaveMode(isActivated = true)
            } false -> {
            LogUtil.v(Constants.TAG, "$THIS_NAME, onHistoryCheckedChanged() : 검색어 저장기능 비활성화")
            appPreferenceManager.putSaveMode(isActivated = false)
        }
        }
    }

    /**
     * Button 클릭 시 호출되는 메서드 (검색어 기록 List 삭제)
     */
    fun onHistoryDeleteBtnClick() {
        LogUtil.v(Constants.TAG, "$THIS_NAME, onHistoryDeleteBtnClick() called")
        appPreferenceManager.clearSearchHistoryList()
    }

    /**
     * 검색어 기록 Item 추가
     */
    fun insertSearchTermHistory(query: String, searchHistoryList: ArrayList<SearchHistoryModel>, saveModeState: Boolean) = viewModelScope.launch(ioDispatcher) {
        LogUtil.d(Constants.TAG, "$THIS_NAME, insertSearchTermHistory() called")

        if (saveModeState) {

            /**
             * 중복 Item 삭제
             */
            val iter = searchHistoryList.iterator()
            while (iter.hasNext()) {
                if (query.equals(iter.next().value)) {
                    iter.remove()
                    break
                }
            }

            /**
             * searchHistoryList 값의 유무에 따라 id를
             */
            var id : Long = 0
            if (searchHistoryList.isNotEmpty()) {
                id = searchHistoryList.last().id!! + 1L
            }

            searchHistoryList.add(SearchHistoryModel(id = id, timeSet = System.currentTimeMillis().toDateFormat(), value = query))
            LogUtil.d(Constants.TAG, "$THIS_NAME, insertSearchTermHistory() SearchHistoryModel : $searchHistoryList.")

            // 기존 데이터에 덮어쓰기
            appPreferenceManager.putSearchHistory(searchHistoryList)
        }
    }

    /**
     * 검색어 기록 Item 삭제
     */
    fun removeSearchHistory(searchHistoryList: ArrayList<SearchHistoryModel>, model: BaseModel) {
        searchHistoryList.remove(model)
        appPreferenceManager.putSearchHistory(searchHistoryList)
    }

    /**
     * ReviewTodoList 결과를 filter하는 메서드
     */
    fun filterSearchHistory(reviewTodoList: List<ReviewTodoModel>, query: String) {
        val regex = "([^#$]*)$query([^#\$]*)".toRegex()

        val filterList = reviewTodoList.filter {
            it.title.matches(regex)
        }

        filterList.sortedByDescending { it.date }

        LogUtil.i(Constants.TAG, "$THIS_NAME, filterSearchHistory() ReviewTodoList : $filterList")
        _jobStateLiveData.value = JobState.True.Result(filterList)

    }
}