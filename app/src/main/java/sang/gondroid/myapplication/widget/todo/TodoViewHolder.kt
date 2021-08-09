package sang.gondroid.myapplication.widget.todo

import android.util.Log
import android.view.View
import sang.gondroid.myapplication.databinding.LayoutTodoItemBinding
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.widget.base.AdapterListener
import sang.gondroid.myapplication.widget.base.BaseViewHolder

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

            onItemClick(binding.todoItemTile, adapterPosition)
        }
    }
}