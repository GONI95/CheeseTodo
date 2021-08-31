package sang.gondroid.cheesetodo.widget.todo

import android.util.Log
import android.view.View
import sang.gondroid.cheesetodo.databinding.LayoutTodoItemBinding
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.widget.base.BaseViewHolder

class TodoViewHolder(
    private val binding: LayoutTodoItemBinding,
    private val onItemClick: (view : View, position: Int) -> Unit
) : BaseViewHolder<TodoModel>(binding){

    private val THIS_NAME = this::class.simpleName

    override fun bindData(model: TodoModel) {
        binding.todoItem = model
    }

    init {
        Log.d(Constants.TAG, "$THIS_NAME, init() called ")

        binding.root.setOnClickListener {
            Log.d(Constants.TAG, "$THIS_NAME, setOnClickListener() called : 사용자 이벤트가 발생했습니다.")

            onItemClick(binding.todoItemTitle, adapterPosition)
        }
    }
}