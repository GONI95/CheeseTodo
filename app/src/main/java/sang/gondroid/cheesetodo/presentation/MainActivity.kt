package sang.gondroid.cheesetodo.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.databinding.ActivityMainBinding
import sang.gondroid.cheesetodo.presentation.home.HomeFragment
import sang.gondroid.cheesetodo.presentation.my.MyFragment
import sang.gondroid.cheesetodo.presentation.review.ReviewFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil


class MainActivity : AppCompatActivity() {

    private val THIS_NAME = this::class.simpleName

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
    }

    private fun initViews() = with(binding) {
        LogUtil.v(Constants.TAG, "$THIS_NAME, initViews() called")

        /**
         * Gon : BottomNavigationView에서 선택한 bottom_navigation_menu의 item 값에 따라 해당하는 Framgent를 띄웁니다.
         */
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {

                    showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
                    true
                }
                R.id.nav_review -> {

                    showFragment(ReviewFragment.newInstance(), ReviewFragment.TAG)
                    true
                }
                R.id.nav_my -> {

                    showFragment(MyFragment.newInstance(), MyFragment.TAG)
                    true
                }
                else -> {

                    false
                }
            }
        }

        /**
         * Gon : 처음 애플리케이션이 켜졌을 때 기본적으로 HomeFramgent를 출력합니다.
         */
        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        LogUtil.v(Constants.TAG, "$THIS_NAME, showFragment() called")

        /**
         * Gon : FragmentManager에 추가된 Fragment들 중 인자를 가지는 Fragment를 찾아 선언과 함께 초기화 합니다.
         */
        val findFragment = supportFragmentManager.findFragmentByTag(tag)

        /**
         * Gon : FragmentManager에 추가된 모든 Fragment 리스트를 가져와
         *       Fragment Transaction 작업을 통해 화면에서 숨깁니다.
         */
        supportFragmentManager.fragments.forEach { fm ->
            supportFragmentManager.beginTransaction().hide(fm).commitAllowingStateLoss()
        }

        /**
         * Gon : findFragment의 값이 존재하는 한다면 숨겨두었던 Fragment를 표시합니다.
         *       findFragment의 값이 존재하지 않는다면 해당 프래그먼트를 생성하여 표시합니다. (기존의 Fragment는 그대로 둠)
         */
        findFragment?.let {
            LogUtil.i(Constants.TAG, "$THIS_NAME, showFragment() : Show ${tag}")
            supportFragmentManager.beginTransaction().show(it).commitAllowingStateLoss()
        } ?: kotlin.run {
            LogUtil.i(Constants.TAG, "$THIS_NAME, showFragment() : Add ${tag}")
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment, tag).commitAllowingStateLoss()
        }
    }
}