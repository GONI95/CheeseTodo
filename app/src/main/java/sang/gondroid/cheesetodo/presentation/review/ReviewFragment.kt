package sang.gondroid.cheesetodo.presentation.review

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.BuildConfig
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.preference.LiveSharedPreferences
import sang.gondroid.cheesetodo.databinding.FragmentReviewBinding
import sang.gondroid.cheesetodo.domain.model.*
import sang.gondroid.cheesetodo.presentation.base.BaseFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import sang.gondroid.cheesetodo.widget.history.SearchHistoryListener
import sang.gondroid.cheesetodo.widget.review.ReviewTodoListener
import java.util.*
import kotlin.properties.Delegates


@Suppress("UNCHECKED_CAST")
class ReviewFragment  : BaseFragment<ReviewViewModel, FragmentReviewBinding>(),
    androidx.appcompat.widget.SearchView.OnQueryTextListener,
    View.OnFocusChangeListener {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: ReviewViewModel by viewModel()

    private val liveSharedPreferences: LiveSharedPreferences by inject()

    private var searchHistoryList = ArrayList<SearchHistoryModel>()
    private var reviewTodoList = ArrayList<ReviewTodoModel>()
    private var saveModeState by Delegates.notNull<Boolean>()

    private val mSearchView: androidx.appcompat.widget.SearchView by lazy {
        binding.toolbar.menu.findItem(R.id.search_menu_item)?.actionView as androidx.appcompat.widget.SearchView
    }

    private val reviewTodoAdapter by lazy {
        BaseAdapter<ReviewTodoModel>(modelList = listOf(), adapterListener = object : ReviewTodoListener {
                override fun onClickItem(view: View, position: Int, model: BaseModel) {
                    LogUtil.i(Constants.TAG, "$THIS_NAME onClickItem() : $position, $model")

                    val bundle = Bundle()
                    bundle.putSerializable("ReviewTodoItemData", model)
                }
            }
        )
    }

    private val searchHistoryAdapter by lazy {
        BaseAdapter<SearchHistoryModel>(modelList = listOf(), adapterListener = object : SearchHistoryListener {
            override fun onClickItem(v: View, model: BaseModel) {
                LogUtil.i(Constants.TAG, "$THIS_NAME onClickItem()")
                viewModel.removeSearchHistory(searchHistoryList, model)
            }

            override fun onClickItem(v: View, query: String) {
                LogUtil.i(Constants.TAG, "$THIS_NAME onClickItem()")
                viewModel.filterSearchHistory(reviewTodoList, query)
            }
        })
    }

    override fun getDataBinding(): FragmentReviewBinding
            = FragmentReviewBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {

        binding.reviewAdapter = reviewTodoAdapter
        binding.historyAdapter = searchHistoryAdapter
        binding.reviewViewModel = viewModel

        /**
         * Toolbar menu의 Item에 SearchView 위젯의 객체를 가져와 초기화
         */
        mSearchView.setOnQueryTextListener(this@ReviewFragment)
        mSearchView.setOnQueryTextFocusChangeListener(this@ReviewFragment)

        /**
         * AppPreferenceManager의 회원명 값을 관찰
         */
        liveSharedPreferences.getString(BuildConfig.KEY_USER_NAME, null).observe(viewLifecycleOwner, Observer { displayName ->
            LogUtil.i(Constants.TAG, "$THIS_NAME getString() called : $displayName")

            binding.displayName = displayName
        })

        /**
         * AppPreferenceManager의 검색 히스토리 값을 관찰
         */
        liveSharedPreferences.getSearchHistoryList(BuildConfig.KEY_SEARCH_HISTORY, null).observe(viewLifecycleOwner, Observer { list ->
            LogUtil.i(Constants.TAG, "$THIS_NAME getSearchHistoryList() called : $list")

            if(!list.isNullOrBlank()) {
                /**
                 * String -> ArrayList 변환 : fromJson() [json -> java]
                 */
                searchHistoryList = Gson().fromJson(list, Array<SearchHistoryModel>::class.java).toMutableList() as ArrayList<SearchHistoryModel>
            } else {
                searchHistoryList.clear()
            }

            searchHistoryAdapter.submitList(searchHistoryList)
        })

        /**
         * AppPreferenceManager의 저장모드 값을 관찰
         */
        liveSharedPreferences.getBoolean(BuildConfig.KEY_SAVE_MODE, false).observe(viewLifecycleOwner, Observer { state ->
            LogUtil.i(Constants.TAG, "$THIS_NAME getBoolean() called")
            saveModeState = state
            binding.switchState = saveModeState
        })
    }


    override fun observeData() {
        viewModel.jobStateLiveData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is JobState.True.Result<*> -> {
                    reviewTodoList = state.data as ArrayList<ReviewTodoModel>
                    reviewTodoAdapter.submitList(reviewTodoList)
                }
                is JobState.False -> {
                    LogUtil.w(Constants.TAG, "$THIS_NAME handleFalseState() called")
                    Toast.makeText(requireContext(), R.string.request_false, Toast.LENGTH_LONG).show()
                }
                is JobState.Error -> {
                    LogUtil.e(Constants.TAG, "$THIS_NAME handleErrorState() : ${getString(state.messageId, state.e)}")
                    Toast.makeText(requireContext(), R.string.an_error_occurred, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    /**
     * SearchView의 Focus 상태(활성화, 비활성화)가 변경되면 호출되는 메서드
     */
    override fun onFocusChange(v: View?, hasFocus: Boolean) = when (hasFocus) {
        true -> { LogUtil.v(Constants.TAG, "$THIS_NAME - onFocusChange() SearchView On ")
            binding.searchHistoryLayout.isVisible = true
        }
        false -> { LogUtil.v(Constants.TAG, "$THIS_NAME - onFocusChange() SearchView Off")
            binding.searchHistoryLayout.isInvisible = true
        }
    }

    /**
     * 키보드 검색버튼 클릭 시 SearchView 입력값을 가져오는 메서드
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        LogUtil.i(Constants.TAG, "onQueryTextSubmit() quary : $query")

        if (!query.isNullOrEmpty()) {
            viewModel.insertSearchTermHistory(query, searchHistoryList, saveModeState)
            viewModel.filterSearchHistory(reviewTodoList, query)
        }

        mSearchView.setQuery("", false)    // SearchView의 입력값을 빈값으로 초기화
        binding.toolbar.collapseActionView()   // 액션뷰가 닫힌다.

        return true
    }

    /**
     * Toolbar의 SearchView 입력값이 변경되면 호출되는 메서드 (검색어 문자열 길이 제한)
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        LogUtil.i(Constants.TAG, "$THIS_NAME onQueryTextChange() newText : $newText")

        if (newText?.count() == 50) Toast.makeText(requireContext(), getString(R.string.String_length_limit), Toast.LENGTH_SHORT).show()

        return true
    }

    companion object {
        fun newInstance() = ReviewFragment()

        const val TAG = "ReviewFragment"
    }
}