package sang.gondroid.cheesetodo.presentation.review

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.BuildConfig
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.preference.LivePreference
import sang.gondroid.cheesetodo.data.preference.LiveSharedPreferences
import sang.gondroid.cheesetodo.databinding.FragmentReviewBinding
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.presentation.base.BaseFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.TodoCategory
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import sang.gondroid.cheesetodo.widget.review.ReviewTodoListener


@Suppress("UNCHECKED_CAST")
class ReviewFragment  : BaseFragment<ReviewViewModel, FragmentReviewBinding>(),
    androidx.appcompat.widget.SearchView.OnQueryTextListener {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: ReviewViewModel by viewModel()

    private val liveSharedPreferences: LiveSharedPreferences by inject()

    private lateinit var mSearchView: androidx.appcompat.widget.SearchView

    private val reviewAdapter by lazy {
        BaseAdapter<ReviewTodoModel>(
            modelList = listOf(),
            adapterListener = object : ReviewTodoListener {
                override fun onClickItem(view: View, position: Int, model: BaseModel) {
                    LogUtil.i(Constants.TAG, "$THIS_NAME onClickItem() : $position, $model")

                    val bundle = Bundle()
                    bundle.putSerializable("ReviewTodoItemData", model)
                }
            }
        )
    }

    override fun getDataBinding(): FragmentReviewBinding
            = FragmentReviewBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {

        binding.adapter = reviewAdapter
        binding.reviewViewModel = viewModel

        /**
         * Toolbar menu의 Item에 SearchView 위젯의 객체를 가져와 초기화
         */
        mSearchView = toolbar.menu.findItem(R.id.search_menu_item)?.actionView as androidx.appcompat.widget.SearchView
        mSearchView.setOnQueryTextListener(this@ReviewFragment)


        liveSharedPreferences.getString(BuildConfig.KEY_USER_NAME, null)
            .observe(viewLifecycleOwner, Observer { displayName ->
                LogUtil.i(Constants.TAG, "$THIS_NAME getString() called : $displayName")

                binding.displayName = displayName
            })
    }


    override fun observeData() {
        viewModel.jobStateLiveData.observe(viewLifecycleOwner, Observer { state ->

            when (state) {
                is JobState.True.Result<*> -> {
                    reviewAdapter.submitList(state.data as List<ReviewTodoModel>)
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
     * 키보드 검색버튼 클릭 시 SearchView 입력값을 가져오는 메서드
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(Constants.TAG, "onQueryTextSubmit() quary : $query")

        this.mSearchView.setQuery("", false)    // SearchView의 입력값을 빈값으로 초기화
        this.binding.toolbar.collapseActionView()   // 액션뷰가 닫힌다.

        return true
    }

    /**
     * Toolbar의 SearchView 입력값이 변경되면 호출되는 메서드 (검색어 문자열 길이 제한)
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        Log.d(Constants.TAG, "$THIS_NAME onQueryTextChange() newText : $newText")

        if (newText?.count() == 50) Toast.makeText(requireContext(), getString(R.string.String_length_limit), Toast.LENGTH_SHORT).show()

        return true
    }

    companion object {
        fun newInstance() = ReviewFragment()

        const val TAG = "ReviewFragment"
    }
}