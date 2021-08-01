package sang.gondroid.myapplication.widget.page

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import sang.gondroid.myapplication.presentation.todocategory.TodoCategoryFragment

/**
 *
 */
class FragmentViewPagerAdapter(
    fragment : Fragment,
    val fragmentList : List<TodoCategoryFragment>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment
        = fragmentList[position]

}