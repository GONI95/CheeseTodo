package sang.gondroid.cheesetodo.presentation.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject
import sang.gondroid.cheesetodo.BuildConfig
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.preference.LiveSharedPreferences
import sang.gondroid.cheesetodo.databinding.FragmentHomeBinding
import sang.gondroid.cheesetodo.presentation.todocategory.InsertTodoActivity
import sang.gondroid.cheesetodo.presentation.todocategory.TodoCategoryFragment
import sang.gondroid.cheesetodo.util.*
import sang.gondroid.cheesetodo.widget.page.FragmentViewPagerAdapter


class HomeFragment : Fragment() {
    private val THIS_NAME = this::class.simpleName

    private lateinit var binding : FragmentHomeBinding

    private val liveSharedPreferences: LiveSharedPreferences by inject()

    // Fragment에 하위 뷰를 삽입하기위해 FragmentStateAdapter를 상속받는 Adapter 선언
    private lateinit var viewPagerAdapter: FragmentViewPagerAdapter

    private val getStartActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            activityResult.data?.extras?.getSerializable("jobState").let { jobState ->
                LogUtil.d(Constants.TAG, "$THIS_NAME jobState : $jobState")

                when (jobState) {
                    is JobState.True -> { Toast.makeText(requireContext(), getString(R.string.insert_job_complete), Toast.LENGTH_SHORT).show() }
                    is JobState.Error -> { Toast.makeText(requireContext(), getString(R.string.an_error_occurred), Toast.LENGTH_LONG).show() }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogUtil.v(Constants.TAG, "$THIS_NAME, onViewCreated() called : ${hashCode()}")

        initViewPager()
        /**
         * 무명 클래스로 클릭한 Chip(정렬) 이벤트 처리
         */
        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.chip_initialize ->
                    changeTodoListSortFilter(TodoListSortFilter.DEFAULT)

                R.id.chip_low_importance ->
                    changeTodoListSortFilter(TodoListSortFilter.HIGH_IMPORTANCE)

                R.id.chip_fast_date ->
                    changeTodoListSortFilter(TodoListSortFilter.FAST_DATE)

                else ->
                    LogUtil.w(Constants.TAG, "$THIS_NAME 해당하는 Chip 없음")
            }
        }

        binding.addTodoButton.setOnClickListener {
            Intent(requireContext(), InsertTodoActivity::class.java).apply {
                getStartActivityForResult.launch(this)
            }
        }

        liveSharedPreferences.getString(BuildConfig.KEY_DISPLAY_NAME, null).observe(viewLifecycleOwner, Observer { displayName ->
            LogUtil.i(Constants.TAG, "$THIS_NAME getString() called : $displayName")

            binding.displayName = displayName
        })
    }

    /**
     * Chip 클릭 시 Listener에 의해 호출되는 메서드, viewPagerAdapter로부터 FragmentList를 받아,
     * 각 Fragment의 ViewModel의 변수인 filter 값을 변경
     */
    private fun changeTodoListSortFilter(filter: TodoListSortFilter) {
        LogUtil.i(Constants.TAG, "$THIS_NAME changeTodoListSortFilter() called : $filter")

        viewPagerAdapter.fragmentList.forEach {
            LogUtil.i(Constants.TAG, "$THIS_NAME viewPagerAdapter : $it, ${it.hashCode()}")
            it.viewModel.setTodoListFilter(filter)
        }
    }

    /**
     * 1. todoCategories : enum class로 정의한 Category들의 값을 배열에 저장
     *
     * 2. if문 : viewPagerAdapter가 초기화되지 않았다면,
     * Category 배열만큼 TodoCategoryFragment의 인스턴스 생성한 후, List에 Fragment를 저장
     * FragmentViewPagerAdapter에 Fragment를 담은 List를 전달하여, 하위 뷰를 담을 컨테이너를 생성해 viewPager에 삽입
     *
     * 3. offscreenPageLimit : ViewPager2가 현재 페이지로부터 얼만큼 떨어진 페이지를 미리 생성할 것인지 설정
     *
     * 4. TabLayoutMediator : TabLayout을 ViewPager2와 연결하는 중재자 역할을 하며, Tab을 선택하면 ViewPager2의 위치와
     * 동기화하고, ViewPager2를 끌면 TabLayout의 스크롤 위치를 동기화
     *
     */
    private fun initViewPager() = with(binding) {
        LogUtil.v(Constants.TAG, "$THIS_NAME initViewPager called")
        val todoCategories = TodoCategory.values()

        if (::viewPagerAdapter.isInitialized.not()) {
            val fragmentList = todoCategories.map {
                TodoCategoryFragment.newInstance(it).also {
                    LogUtil.i(Constants.TAG, "$THIS_NAME Create TodoCategoryFragment : $it")
                }
            }

            viewPagerAdapter = FragmentViewPagerAdapter(this@HomeFragment, fragmentList)
            viewPager.adapter = viewPagerAdapter
        }

        viewPager.offscreenPageLimit = todoCategories.size  // 1로하면 오류남!!

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(todoCategories[position].categoryNameId)
        }.attach()
    }

    companion object {
        fun newInstance() = HomeFragment()

        const val TAG = "HomeFragment"
    }
}
