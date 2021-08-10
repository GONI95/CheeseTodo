package sang.gondroid.myapplication.presentation.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.FragmentHomeBinding
import sang.gondroid.myapplication.databinding.FragmentMyBinding
import sang.gondroid.myapplication.presentation.base.BaseFragment
import sang.gondroid.myapplication.presentation.my.MyViewModel
import sang.gondroid.myapplication.presentation.todocategory.InsertTodoActivity
import sang.gondroid.myapplication.presentation.todocategory.TodoCategoryFragment
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.JobState
import sang.gondroid.myapplication.util.TodoCategory
import sang.gondroid.myapplication.widget.page.FragmentViewPagerAdapter


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: HomeViewModel by viewModel()

    // Fragment에 하위 뷰를 삽입하기위해 FragmentStateAdapter를 상속받는 Adapter 선언
    private lateinit var viewPagerAdapter: FragmentViewPagerAdapter

    private val getStartActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            activityResult.data?.extras?.getSerializable("jobState").let { jobState ->
                Log.d(Constants.TAG, "$THIS_NAME jobState : $jobState")

                when (jobState) {
                    JobState.SUCCESS -> { Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show() }
                    JobState.ERROR -> { Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show() }
                }
            }
        }
    }

    override fun getDataBinding(): FragmentHomeBinding
            = FragmentHomeBinding.inflate(layoutInflater)

    override fun initViews() {
        initViewPager()

        binding.addTodoButton.setOnClickListener {
            Intent(requireContext(), InsertTodoActivity::class.java).apply {
                getStartActivityForResult.launch(this)
            }
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
        val todoCategories = TodoCategory.values()

        if (::viewPagerAdapter.isInitialized.not()) {
            val fragmentList = todoCategories.map {
                Log.d(Constants.TAG, "$THIS_NAME Create TodoCategoryFragment : $it")

                TodoCategoryFragment.newInstance(it)
            }

            viewPagerAdapter = FragmentViewPagerAdapter(
                this@HomeFragment, fragmentList
            )

            viewPager.adapter = viewPagerAdapter
        }

        viewPager.offscreenPageLimit = 1

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            Log.d(Constants.TAG, "$THIS_NAME TodoCategory : $position, ${todoCategories[position].name}")
            tab.text = getString(todoCategories[position].categoryNameId)
        }.attach()
    }

    override fun observeData() {}

    companion object {
        fun newInstance() = HomeFragment()

        const val TAG = "HomeFragment"
    }
}