package sang.gondroid.myapplication.presentation.todocategory

import android.graphics.Color
import android.util.Log
import androidx.core.os.bundleOf
import com.gondroid.cheeseplan.presentation.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import sang.gondroid.myapplication.databinding.FragmentTodoCategoryBinding
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.TodoCategory

class TodoCategoryFragment : BaseFragment<TodoCategoryViewModel, FragmentTodoCategoryBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: TodoCategoryViewModel by viewModel { parametersOf(todoCategory) }

    // HomeFragment에서 newInstance()를 통해 전달한 category값을 Bundle로부터 가져옴
    private val todoCategory by lazy { arguments?.getSerializable(TODO_CATEGORY_KEY) as TodoCategory }

    override fun getDataBinding(): FragmentTodoCategoryBinding
        = FragmentTodoCategoryBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {
        if (todoCategory == TodoCategory.ANDROID) {
            Log.d(Constants.TAG, "$THIS_NAME initViews() : ${hashCode()}")
            binding.TodoCategoryRecyclerView.setBackgroundColor(Color.BLUE)
        }
    }

    override fun onResume() {
        Log.d(Constants.TAG, "$THIS_NAME onResume() : ${hashCode()}")
        super.onResume()
    }

    override fun observeData()  {

    }

    companion object {
        const val TODO_CATEGORY_KEY = "todoCategory"

        // TodoCategoryFragment 인스턴스를 생성하면서 arguments에 데이터를 넘겨주는 코드
        fun newInstance(todoCategory: TodoCategory) = TodoCategoryFragment().apply {
            arguments = bundleOf(
                TODO_CATEGORY_KEY to todoCategory
            )

            Log.d(Constants.TAG, "$THIS_NAME newInstance() : $arguments, $TODO_CATEGORY_KEY")
        }
    }
}