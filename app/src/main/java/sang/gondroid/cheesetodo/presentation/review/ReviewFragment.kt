package sang.gondroid.cheesetodo.presentation.review

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.gson.*
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
import sang.gondroid.cheesetodo.util.NetworkConnection
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import sang.gondroid.cheesetodo.widget.custom.CustomDialog
import sang.gondroid.cheesetodo.widget.custom.CustomDialogClickListener
import sang.gondroid.cheesetodo.widget.history.SearchHistoryListener
import sang.gondroid.cheesetodo.widget.review.ReviewTodoListener
import java.lang.reflect.Type
import java.util.*
import kotlin.properties.Delegates


@Suppress("UNCHECKED_CAST")
class ReviewFragment  : BaseFragment<ReviewViewModel, FragmentReviewBinding>(),
    androidx.appcompat.widget.SearchView.OnQueryTextListener,
    View.OnFocusChangeListener {
    private val THIS_NAME = this::class.simpleName

    /**
     * Gon : 네트워크 연결 상태를 확인하기 위한 NetworkConnection class
     *       [21.11.28]
     */
    private val connection : NetworkConnection by inject()

    override val viewModel: ReviewViewModel by viewModel()

    private val liveSharedPreferences: LiveSharedPreferences by inject()

    private var searchHistoryList = ArrayList<SearchHistoryModel>()
    private var reviewTodoList = ArrayList<ReviewTodoModel>()
    private var saveModeState by Delegates.notNull<Boolean>()

    private val mSearchView: androidx.appcompat.widget.SearchView by lazy {
        binding.toolbar.menu.findItem(R.id.search_menu_item)?.actionView as androidx.appcompat.widget.SearchView
    }

    private var loginState by Delegates.notNull<Boolean>()
    private val reviewTodoAdapter by lazy {
        BaseAdapter<ReviewTodoModel>(modelList = listOf(), adapterListener = object : ReviewTodoListener {
            override fun onClickItem(view: View, position: Int, model: BaseModel) {
                LogUtil.i(Constants.TAG, "$THIS_NAME onClickItem() : $position, $model")

                val bundle = Bundle()
                bundle.putSerializable("ReviewTodoItemData", model)

                if (loginState) {
                    Intent(requireContext(), DetailReviewActivity::class.java).apply {
                        putExtra("bundle", bundle)
                        startActivity(this, getBudle(view))
                    }
                } else {
                    Snackbar.make(view, getString(R.string.sign_in_is_required), Snackbar.LENGTH_SHORT).run {
                        this.setAction(getString(R.string.yes)) {
                            this.dismiss()
                        }
                    }.show()
                }
            }
        }
        )
    }

    /**
     * API 21 이상부터 가능하지만, 최소 버전이 24라 분기처리 없이 처리 / 클릭 이벤트 애니메이션 처리
     */
    private fun getBudle(v : View) : Bundle
            = ActivityOptions.makeSceneTransitionAnimation(
        requireActivity(), Pair.create(v, resources.getString(R.string.title_transition_name))
    ).toBundle()

    private val searchHistoryAdapter by lazy {
        BaseAdapter<SearchHistoryModel>(modelList = listOf(), adapterListener = object : SearchHistoryListener {
            override fun onClickItem(v: View, model: BaseModel) {
                LogUtil.v(Constants.TAG, "$THIS_NAME onClickItem() -> removeSearchHistory()")
                viewModel.removeSearchHistory(searchHistoryList, model)
            }

            override fun onClickItem(v: View, query: String) {
                LogUtil.v(Constants.TAG, "$THIS_NAME onClickItem() -> filterSearchHistory()")
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
         * AppPreferenceManager의 토큰 값 유무 확인
         */
        liveSharedPreferences.getString(BuildConfig.KEY_ID_TOKEN, null).observe(viewLifecycleOwner, Observer { token ->
            loginState = !token.isNullOrBlank()
        })

        /**
         * AppPreferenceManager의 회원명 값을 관찰
         */
        liveSharedPreferences.getString(BuildConfig.KEY_DISPLAY_NAME, null).observe(viewLifecycleOwner, Observer { displayName ->
            LogUtil.v(Constants.TAG, "$THIS_NAME getString() called : $displayName")

            binding.displayName = displayName
        })

        /**
         * AppPreferenceManager의 검색 히스토리 값을 관찰
         */
        liveSharedPreferences.getString(BuildConfig.KEY_SEARCH_HISTORY, null).observe(viewLifecycleOwner, Observer { list ->
            LogUtil.v(Constants.TAG, "$THIS_NAME getSearchHistoryList() called : $list")

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
            LogUtil.v(Constants.TAG, "$THIS_NAME getBoolean() called")
            saveModeState = state
            binding.switchState = saveModeState
        })
    }

    /**
     * Gon : jobStateLiveData의 값에 따라 reviewTodo를 표시하는 RecyclerView 목록을 업데이트
     *       jobStateLiveData - HandlerFireStore.kt getReviewTodo() 메서드로 부터 반환받은 JobState
     *       [update - 21.11.18]
     */
    override fun observeData() = with(binding) {

        /**
         * Gon : connection(네트워크 변경 상태에 따라 true, false) 값에 따라 서비스 이용이 유무에 대응하는 layout을 표시
         *       [update - 21.11.28]
         */
        connection.observe(this@ReviewFragment, Observer { isConnected ->
            if (isConnected) {
                LogUtil.d(Constants.TAG, "$THIS_NAME, 네트워크 연결 상태 : On ")

                reviewLayoutDisconnected.visibility = View.GONE
                layoutConnectedGroup.visibility = View.VISIBLE

            } else {
                LogUtil.d(Constants.TAG, "$THIS_NAME, 네트워크 연결 상태 : Off ")

                reviewLayoutDisconnected.visibility = View.VISIBLE
                layoutConnectedGroup.visibility = View.GONE
            }
        })

        viewModel.getReviewTodoLiveData.observe(viewLifecycleOwner, Observer {
            LogUtil.d(Constants.TAG, "$THIS_NAME observeData getReviewTodoLiveData : ${it}")

            when (it) {
                is JobState.True.Result<*> -> {
                    reviewTodoList = it.data as ArrayList<ReviewTodoModel>
                    reviewTodoAdapter.submitList(reviewTodoList)
                }
                is JobState.Error -> {
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

        if (newText?.count() == 50) Toast.makeText(requireContext(), getString(R.string.string_length_limit), Toast.LENGTH_SHORT).show()

        return true
    }

    companion object {
        fun newInstance() = ReviewFragment()

        const val TAG = "ReviewFragment"
    }
}