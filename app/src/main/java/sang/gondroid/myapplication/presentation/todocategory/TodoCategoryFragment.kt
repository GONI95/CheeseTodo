package sang.gondroid.myapplication.presentation.todocategory

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import androidx.core.os.bundleOf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.FragmentTodoCategoryBinding
import sang.gondroid.myapplication.domain.model.BaseModel
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.presentation.base.BaseFragment
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.TodoCategory
import sang.gondroid.myapplication.widget.base.AdapterListener
import sang.gondroid.myapplication.widget.base.BaseAdapter
import sang.gondroid.myapplication.widget.todo.TodoListener
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

class TodoCategoryFragment : BaseFragment<TodoCategoryViewModel, FragmentTodoCategoryBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: TodoCategoryViewModel by viewModel { parametersOf(todoCategory) }

    // HomeFragment에서 newInstance()를 통해 전달한 category값을 Bundle로부터 가져옴
    private val todoCategory by lazy { arguments?.getSerializable(TODO_CATEGORY_KEY) as TodoCategory }

    private val adapter by lazy {
        BaseAdapter<TodoModel>(
            modelList = listOf(),
            adapterListener = object : TodoListener {
                override fun onClickItem(position: Int, model: BaseModel) {

                    Log.d(Constants.TAG, "$THIS_NAME onClickItem() : $position, $model")

                    val bundle = Bundle()
                    bundle.putSerializable("TodoItemData", model)

                    /*
                    Intent(requireContext(), ***********::class.java).apply {
                        putExtra("bundle", bundle)

                        startActivity(this, getBudle(v))
                    }
                     */
                }
            })
    }

    private fun getBudle(v : View) : Bundle {
        // API 21 이상부터 가능하지만, 최소 버전이 23이라 분기처리 없이 처리
        return ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            Pair.create(v, resources.getString(R.string.title_transition_name))
        ).toBundle()
    }

    override fun getDataBinding(): FragmentTodoCategoryBinding
        = FragmentTodoCategoryBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {
        todoCategoryRecyclerView.adapter = adapter

        Thread(Runnable {
            kotlin.run {
                repeat(2) {
                    Thread.sleep(3000)
                    adapter.submitList(listOf(
                        TodoModel(
                            0, 0, "2020년", TodoCategory.LANGUAGE, (0..15).random(), "ddddd", "daaaaa", ""),
                        TodoModel(
                            1, 0, "2020년", TodoCategory.LANGUAGE, 2, "ddddd", "daaaaa", ""),
                        TodoModel(
                            2, 0, "2020년", TodoCategory.LANGUAGE, 3, "ddddd", "daaaaa", ""),
                        TodoModel(
                            4, 0, "2020년", TodoCategory.LANGUAGE, (0..15).random(), "ddddd", "daaaaa", ""),
                        TodoModel(
                            5, 0, "2020년", TodoCategory.LANGUAGE, (0..15).random(), "ddddd", "daaaaa", ""),

                    ))
                }
            }
        }).start()
    }

    override fun onResume() {
        Log.d(Constants.TAG, "$THIS_NAME onResume() : ${hashCode()}")
        super.onResume()
    }

    override fun observeData()  {
        adapter.submitList(listOf(
            TodoModel(
                0, 0, "2020년", TodoCategory.LANGUAGE, 1, "ddddd", "daaaaa", ""),
            TodoModel(
                1, 0, "2020년", TodoCategory.LANGUAGE, 2, "ddddd", "daaaaa", ""),
            TodoModel(
                2, 0, "2020년", TodoCategory.LANGUAGE, 3, "ddddd", "daaaaa", ""),
            TodoModel(
                4, 0, "2020년", TodoCategory.LANGUAGE, 4, "ddddd", "daaaaa", ""),
        ))
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