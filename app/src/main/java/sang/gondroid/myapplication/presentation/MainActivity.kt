package sang.gondroid.myapplication.presentation

import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.ActivityMainBinding
import sang.gondroid.myapplication.presentation.home.HomeFragment
import sang.gondroid.myapplication.presentation.my.MyFragment
import sang.gondroid.myapplication.presentation.review.ReviewFragment
import sang.gondroid.myapplication.util.Constants


class MainActivity : AppCompatActivity() {

    private val THIS_NAME = this::class.simpleName

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
    }

    private fun initViews() = with(binding) {
        Log.d(Constants.TAG, "$THIS_NAME, initViews() called")

        binding.bottomNav.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener when (item.itemId) {
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

        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
    }

    /**
     * 1. supportFragmentManager : FragmentManager는 동적인 UI를 제공하기 위한 Class인 Fragment를 관리하는
     * 컨트롤러 역할을 합니다. 현재 UI에 Fragment를 추가, 교체, 제거가 가능합니다.
     * Activity에 하위 Fragment를 이용하겠다면, supportFragmentManager
     * Fragment에 하위 Fragment를 이용하겠다면, parentFragmentManager 또는 childFragmentManager
     *
     * 2. beginTransaction : FragmentTransaction을 가져와 각종 Transaction 작업을 할 수 있도록 함
     *
     * 3. commitAllowingStateLoss : activity 상태가 저장된 후 commit을 실행할 수 있는데, 이 경우 나중에 activity를
     *                           해당 상태에서 복원해야할 때 상태에 대한 손실이 발생할 수 있고, 이 경우 에러가 발생함.
     *                           이러한 UI 상태가 예기치 않게 변경되어도 괜찮은 경우에 사용해야한다.
     */
    private fun showFragment(fragment: Fragment, tag: String) {
        Log.d(Constants.TAG, "$THIS_NAME, showFragment() called")
        /**
         * 1. 주어진 Tag를 가진 Fragment가 가져옴
         * 2. FragmentTransaction을 가져와 각종 Transaction 작업을 할 수 있도록 함
         */
        val findFragment = supportFragmentManager.findFragmentByTag(tag)

        /**
         * Fragment가 교체되기전 모든 Fragment들을 화면에서 숨김
         */
        supportFragmentManager.fragments.forEach { fm ->
            supportFragmentManager.beginTransaction().hide(fm).commitAllowingStateLoss()
        }

        /**
         * 주어진 Tag를 가진 Fragment가 있으면 show(), 없다면 add
         */
        findFragment?.let {
            supportFragmentManager.beginTransaction().show(it).commitAllowingStateLoss()
        } ?: kotlin.run {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment, tag)
                .commitAllowingStateLoss()
        }
    }
}