package sang.gondroid.cheesetodo.widget.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import sang.gondroid.cheesetodo.databinding.LayoutEmptyItemBinding
import sang.gondroid.cheesetodo.databinding.LayoutReviewTodoItemBinding
import sang.gondroid.cheesetodo.databinding.LayoutSearchHistoryItemBinding
import sang.gondroid.cheesetodo.databinding.LayoutTodoItemBinding
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.SearchHistoryModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.checkType
import sang.gondroid.cheesetodo.widget.history.SearchHistoryListener
import sang.gondroid.cheesetodo.widget.history.SearchHistoryViewHolder
import sang.gondroid.cheesetodo.widget.review.ReviewTodoListener
import sang.gondroid.cheesetodo.widget.review.ReviewTodoViewHolder
import sang.gondroid.cheesetodo.widget.todo.TodoListener
import sang.gondroid.cheesetodo.widget.todo.TodoViewHolder

@Suppress("UNCHECKED_CAST")
class BaseAdapter<M : BaseModel>(
    private var modelList : List<BaseModel>,
    private val adapterListener : AdapterListener
) : ListAdapter<BaseModel, BaseViewHolder<M>>(BaseModel.DIFF_CALLBACK){

    private val THIS_NAME = this::class.simpleName

    override fun getItemCount(): Int = modelList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<M> {
        val inflater = LayoutInflater.from(parent.context)

        return if (modelList.checkType<TodoModel>()){
            Log.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : ViewHolder가 생성됩니다.")

            TodoViewHolder(
                binding = LayoutTodoItemBinding.inflate(inflater, parent, false),
                onItemClick = { view, adapterPosition ->
                Log.d(Constants.TAG, "$THIS_NAME, onItemClick() called : ViHolder로부터 응답을 받았습니다.")

                if (adapterListener is TodoListener) {
                    Log.d(Constants.TAG, "$THIS_NAME, onItemClick() called : 구현체에 값을 전달합니다.")

                    adapterListener.onClickItem(view, adapterPosition, modelList[adapterPosition])
                }

            }) as BaseViewHolder<M>
        }
        else if(modelList.checkType<ReviewTodoModel>()) {
            Log.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : ViewHolder가 생성됩니다.")

            ReviewTodoViewHolder(
                binding = LayoutReviewTodoItemBinding.inflate(inflater, parent, false),
                onItemClick = { view, adapterPosition ->
                    Log.d(Constants.TAG, "$THIS_NAME, onItemClick() called : ViHolder로부터 응답을 받았습니다.")

                    if (adapterListener is ReviewTodoListener) {
                        Log.d(Constants.TAG, "$THIS_NAME, onItemClick() called : 구현체에 값을 전달합니다.")

                        adapterListener.onClickItem(view, adapterPosition, modelList[adapterPosition])
                    }

                }) as BaseViewHolder<M>
        }
        else if(modelList.checkType<SearchHistoryModel>()) {
            Log.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : ViewHolder가 생성됩니다.")

            SearchHistoryViewHolder(
                binding = LayoutSearchHistoryItemBinding.inflate(inflater, parent, false),
                onItemClick = { view, adapterPosition ->
                    Log.d(Constants.TAG, "$THIS_NAME, onItemClick() called : ViHolder로부터 응답을 받았습니다.")

                    if (adapterListener is SearchHistoryListener) {
                        Log.d(Constants.TAG, "$THIS_NAME, onItemClick() called : 구현체에 값을 전달합니다.")

                        adapterListener.onClickItem(view, modelList[adapterPosition])
                        adapterListener.onClickItem(view, (modelList[adapterPosition] as SearchHistoryModel).value)
                    }

                }) as BaseViewHolder<M>
        }
        else {
            Log.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : ViewHolder가 생성됩니다.")

            EmptyViewHolder(LayoutEmptyItemBinding.inflate(inflater, parent, false)) as BaseViewHolder<M>
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<M>, position: Int) {
        Log.d(Constants.TAG, "$THIS_NAME, onBindViewHolder() called : ViewHolder에 bind 합니다.")

        holder.bindData(modelList[position] as M)
    }

    override fun submitList(list: List<BaseModel>?) {
        list?.let {
            modelList = it

            super.submitList(list.toMutableList())
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<M>) {
        Log.d(Constants.TAG, "$THIS_NAME, onViewRecycled() called : ViewHolder가 재사용 됩니다.")

        super.onViewRecycled(holder)
    }
}
