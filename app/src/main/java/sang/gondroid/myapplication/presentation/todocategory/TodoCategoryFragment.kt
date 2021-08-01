package sang.gondroid.myapplication.presentation.todocategory

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import com.gondroid.cheeseplan.presentation.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.FragmentTodoCategoryBinding
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.TodoCategory

class TodoCategoryFragment : BaseFragment<TodoCategoryViewModel, FragmentTodoCategoryBinding>() {
    private val THIS_NAME = this::class.simpleName

    init {
        // TodoCategoryFragment들이 잘생성되었는지 확인하기 위한 용도
        Log.d(Constants.TAG, "$THIS_NAME hashCode : ${hashCode()}")
    }

    // HomeFragment에서 newInstance()를 통해 전달한 category값을 Bundle로부터 가져옴
    private val todoCategory by lazy { arguments?.getSerializable(TODO_CATEGORY_KEY) as TodoCategory }
        .also {
            Log.d(Constants.TAG, "$THIS_NAME todoCategory : $arguments, $TODO_CATEGORY_KEY")
        }

    override val viewModel: TodoCategoryViewModel by viewModel { parametersOf(todoCategory) }

    override fun getDataBinding(): FragmentTodoCategoryBinding
        = FragmentTodoCategoryBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {

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