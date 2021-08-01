package sang.gondroid.myapplication.presentation.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gondroid.cheeseplan.presentation.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.FragmentHomeBinding
import sang.gondroid.myapplication.databinding.FragmentMyBinding
import sang.gondroid.myapplication.presentation.my.MyViewModel
import sang.gondroid.myapplication.presentation.todocategory.TodoCategoryFragment
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.TodoCategory
import sang.gondroid.myapplication.widget.page.FragmentViewPagerAdapter


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: HomeViewModel by viewModel()

    private lateinit var viewPagerAdapter: FragmentViewPagerAdapter

    override fun getDataBinding(): FragmentHomeBinding
            = FragmentHomeBinding.inflate(layoutInflater)

    override fun initState() {
        super.initState()

        initViewPager()
    }

    private fun initViewPager() = with(binding) {
        // 생성한 카테고리 받기
        val todoCategories = TodoCategory.values()

        // viewPagerAdapter가 초기화되지 않았으면 초기화
        if (::viewPagerAdapter.isInitialized.not()) {
            val fragmentList = todoCategories.map {
                TodoCategoryFragment.newInstance(it)
            }

            viewPagerAdapter = FragmentViewPagerAdapter(
                this@HomeFragment, fragmentList
            )

            viewPager.adapter = viewPagerAdapter
        }

        viewPager.offscreenPageLimit = todoCategories.size
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            Log.d(Constants.TAG, "$THIS_NAME TodoCategory : $position, ${todoCategories[position].name}")
            tab.text = todoCategories[position].name
        }.attach()
    }

    override fun observeData() {}

    companion object {
        fun newInstance() = HomeFragment()

        const val TAG = "HomeFragment"
    }
}