package sang.gondroid.myapplication.widget.todo

import sang.gondroid.myapplication.databinding.LayoutTodoItemBinding
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.widget.base.AdapterListener
import sang.gondroid.myapplication.widget.base.BaseViewHolder

class TodoViewHolder(
    private val binding: LayoutTodoItemBinding
) : BaseViewHolder<TodoModel>(binding){

    override fun bindData(model: TodoModel) {
        binding.todoItem = model
    }

    override fun bindViews(model: TodoModel, adapterListener: AdapterListener) {
            if (adapterListener is TodoListener) {
                binding.root.setOnClickListener {
                    adapterListener.onClickItem(binding.planItemTile, model)
                }
            }
    }
}