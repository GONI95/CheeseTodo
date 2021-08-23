package sang.gondroid.cheesetodo.presentation.todocategory

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.databinding.FragmentTodoCategoryBinding
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.presentation.base.BaseFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.TodoCategory
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import sang.gondroid.cheesetodo.widget.todo.TodoListener

class TodoCategoryFragment : BaseFragment<TodoCategoryViewModel, FragmentTodoCategoryBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: TodoCategoryViewModel by viewModel { parametersOf(todoCategory) }

    // HomeFragment에서 newInstance()를 통해 전달한 category값을 Bundle로부터 가져옴
    private val todoCategory by lazy { arguments?.getSerializable(TODO_CATEGORY_KEY) as TodoCategory }

    private val adapter by lazy {
        BaseAdapter<TodoModel>( modelList = listOf(), adapterListener = object : TodoListener {
            override fun onClickItem(view: View, position: Int, model: BaseModel) {
                LogUtil.i(Constants.TAG, "$THIS_NAME onClickItem() : $position, $model")

                val bundle = Bundle()
                bundle.putSerializable("TodoItemData", model)

                Intent(requireContext(), DetailTodoActivity::class.java).apply {
                    putExtra("bundle", bundle)
                    startActivity(this, getBudle(view))
                }
            }
        })
    }

    /**
     * API 21 이상부터 가능하지만, 최소 버전이 23이라 분기처리 없이 처리 / 클릭 이벤트 애니메이션 처리
     */
    private fun getBudle(v : View) : Bundle
        = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(), Pair.create(v, resources.getString(R.string.title_transition_name))
        ).toBundle()


    override fun getDataBinding(): FragmentTodoCategoryBinding
            = FragmentTodoCategoryBinding.inflate(layoutInflater)

    override fun onResume() {
        viewModel.fetchData()
        super.onResume()
    }

    override fun initViews() = with(binding) {
        todoCategoryRecyclerView.adapter = adapter
    }

    override fun observeData() = viewModel.todoListLiveData.observe(viewLifecycleOwner, Observer {
        adapter.submitList(it)
    })

    companion object {
        const val TODO_CATEGORY_KEY = "todoCategory"

        // TodoCategoryFragment 인스턴스를 생성하면서 arguments에 데이터를 넘겨주는 코드
        fun newInstance(todoCategory: TodoCategory) = TodoCategoryFragment().apply {
            arguments = bundleOf(
                TODO_CATEGORY_KEY to todoCategory
            )

            LogUtil.i(Constants.TAG, "$THIS_NAME newInstance() : $arguments, $TODO_CATEGORY_KEY")
        }
    }
}