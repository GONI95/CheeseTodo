package sang.gondroid.myapplication.widget.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import sang.gondroid.myapplication.databinding.LayoutEmptyItemBinding
import sang.gondroid.myapplication.databinding.LayoutTodoItemBinding
import sang.gondroid.myapplication.domain.model.BaseModel
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.util.checkType
import sang.gondroid.myapplication.widget.todo.TodoViewHolder

@Suppress("UNCHECKED_CAST")
class BaseAdapter<M : BaseModel>(
    private var modelList : List<BaseModel>,
    private val adapterListener : AdapterListener
) : ListAdapter<BaseModel, BaseViewHolder<M>>(BaseModel.DIFF_CALLBACK){

    override fun getItemCount(): Int = modelList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<M> {
        val inflater = LayoutInflater.from(parent.context)

        return if (modelList.checkType<TodoModel>()){
            TodoViewHolder(LayoutTodoItemBinding.inflate(inflater, parent, false)) as BaseViewHolder<M>
        }
            else {
            EmptyViewHolder(LayoutEmptyItemBinding.inflate(inflater, parent, false)) as BaseViewHolder<M>
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<M>, position: Int) {
        holder.bindData(modelList[position] as M)
        holder.bindViews(modelList[position] as M, adapterListener)
    }

    override fun submitList(list: List<BaseModel>?) {
        list?.let {
            modelList = it

            super.submitList(list)
        }
    }
}