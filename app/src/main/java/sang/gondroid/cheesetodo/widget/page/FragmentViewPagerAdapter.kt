package sang.gondroid.cheesetodo.widget.page

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import sang.gondroid.cheesetodo.presentation.todocategory.TodoCategoryFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil

/**
 * 각 페이지를 나타내는 하위 뷰를 삽입하기위해 Layout을 FragmentStateAdapter에 연결
 * https://developer.android.com/guide/navigation/navigation-swipe-view-2?hl=ko
 */
class FragmentViewPagerAdapter(
    fragment: Fragment,
    val fragmentList: List<TodoCategoryFragment>
) : FragmentStateAdapter(fragment) {
    private val THIS_NAME = this::class.simpleName

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position].also {
        LogUtil.d(Constants.TAG, "$THIS_NAME createFragment() : ${fragmentList[position].hashCode()}")
    }
}