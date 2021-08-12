package sang.gondroid.myapplication.widget.page

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import sang.gondroid.myapplication.presentation.todocategory.TodoCategoryFragment
import sang.gondroid.myapplication.util.Constants

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
        Log.d(Constants.TAG, "$THIS_NAME createFragment() : ${fragmentList[position].hashCode()}")
    }
}