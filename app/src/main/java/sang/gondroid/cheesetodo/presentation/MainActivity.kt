package sang.gondroid.cheesetodo.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
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
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private val THIS_NAME = this::class.simpleName

    private lateinit var binding: ActivityMainBinding

    var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        showContent()
        initViews()
    }

    /**
     * Gon : android.R.id.content를 통해 Activity의 root view를 얻어와, ViewTree가 그려지는 것을 지연시키는 메서드
     *       viewTreeObserver : ViewTree의 변경 사항을 알릴 수 있는 Listener를 등록할 때 사용
     *       addOnPreDrawListener : ViewTree가 그려지려고 할 때 호출될 콜백을 등록
     *       onPreDraw : ViewTree가 그려지려고 할 때 호출되는 콜백 메소드
     *       [update - 21.11.30]
     */
    private fun showContent() {
        thread(start=true) {
            Thread.sleep(1000)
            isReady = true
        }

        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                return if (isReady) {
                    LogUtil.v(Constants.TAG, "$THIS_NAME, onPreDraw() true")
                    // 이벤트 발생 후 제거해주지 않으면 콜백 메서드가 여러번 호출되는 현상이 발생
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    true
                } else {
                    LogUtil.v(Constants.TAG, "$THIS_NAME, onPreDraw() false")
                    false
                }
            }
        })
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