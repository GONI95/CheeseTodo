package sang.gondroid.myapplication.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
     * commitAllowingStateLoss : activity 상태가 저장된 후 commit을 실행할 수 있는데, 이 경우 나중에 activity를
     *                           해당 상태에서 복원해야할 때 상태에 대한 손실이 발생할 수 있고, 이 경우 에러가 발생함.
     *                           이러한 UI 상태가 예기치 않게 변경되어도 괜찮은 경우에 사용해야한다.
     */
    private fun showFragment(fragment: Fragment, tag: String) {

        /**
         * 주어진 Tag를 가진 Fragment가 있는지 확인하는 용도
         */
        val findFragment = supportFragmentManager.findFragmentByTag(tag)

        /**
         * Fragment가 교체되기전 모든 Fragment들을 화면에서 숨김
         */
        supportFragmentManager.fragments.forEach { fm ->

            supportFragmentManager.beginTransaction().hide(fm).commit()
        }

        /**
         * 주어진 Tag를 가진 Fragment가 있으면 show, 없다면 add
         */
        findFragment?.let { fm ->

            supportFragmentManager.beginTransaction().show(fm).commit()
        } ?: kotlin.run {

            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment, tag)
                .commitAllowingStateLoss()
        }
    }
}