package sang.gondroid.cheesetodo.widget.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import sang.gondroid.cheesetodo.databinding.*
import sang.gondroid.cheesetodo.domain.model.*
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.checkType
import sang.gondroid.cheesetodo.widget.history.SearchHistoryListener
import sang.gondroid.cheesetodo.widget.history.SearchHistoryViewHolder
import sang.gondroid.cheesetodo.widget.review.CommentListener
import sang.gondroid.cheesetodo.widget.review.CommentViewHolder
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<M> {
        val inflater = LayoutInflater.from(parent.context)

        return if (modelList.checkType<TodoModel>()){
            LogUtil.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : TodoViewHolder가 생성됩니다.")

            TodoViewHolder(
                binding = LayoutTodoItemBinding.inflate(inflater, parent, false),
                onItemClick = { view, adapterPosition ->
                LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : TodoViewHolder로부터 응답을 받았습니다.")

                if (adapterListener is TodoListener) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : 구현체에 값을 전달합니다.")

                    adapterListener.onClickItem(view, adapterPosition, modelList[adapterPosition])
                }

            }) as BaseViewHolder<M>
        }
        else if(modelList.checkType<ReviewTodoModel>()) {
            LogUtil.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : ReviewTodoViewHolder가 생성됩니다.")

            ReviewTodoViewHolder(
                binding = LayoutReviewTodoItemBinding.inflate(inflater, parent, false),
                onItemClick = { view, adapterPosition ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : ReviewTodoViewHolder로부터 응답을 받았습니다.")

                    if (adapterListener is ReviewTodoListener) {
                        LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : 구현체에 값을 전달합니다.")

                        adapterListener.onClickItem(view, adapterPosition, modelList[adapterPosition])
                    }

                }) as BaseViewHolder<M>
        }
        else if(modelList.checkType<SearchHistoryModel>()) {
            LogUtil.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : SearchHistoryViewHolder가 생성됩니다.")

            SearchHistoryViewHolder(
                binding = LayoutSearchHistoryItemBinding.inflate(inflater, parent, false),
                onItemClick = { view, adapterPosition ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : SearchHistoryViewHolder로부터 응답을 받았습니다.")

                    if (adapterListener is SearchHistoryListener && view is TextView) {
                        LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : 구현체에 값을 전달합니다.")

                        adapterListener.onClickItem(view, (modelList[adapterPosition] as SearchHistoryModel).value)

                    } else if(adapterListener is SearchHistoryListener && view is ImageView) {
                        LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : 구현체에 값을 전달합니다.")

                        adapterListener.onClickItem(view, modelList[adapterPosition])
                    }

                }) as BaseViewHolder<M>
        }
        else if(modelList.checkType<CommentModel>()) {
            LogUtil.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : CommentViewHolder가 생성됩니다.")

            CommentViewHolder(
                binding = LayoutCommentItemBinding.inflate(inflater, parent, false),
                onItemClick = { view, adapterPosition ->
                    LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : CommentViewHolderr로부터 응답을 받았습니다.")

                    if (adapterListener is CommentListener) {
                        LogUtil.d(Constants.TAG, "$THIS_NAME, onItemClick() called : 구현체에 값을 전달합니다.")

                        adapterListener.onClickItem(view, adapterPosition, modelList[adapterPosition])
                    }
                }) as BaseViewHolder<M>
        }
        else {
            LogUtil.d(Constants.TAG, "$THIS_NAME, onCreateViewHolder() called : EmptyViewHolder가 생성됩니다.")

            EmptyViewHolder(LayoutEmptyItemBinding.inflate(inflater, parent, false)) as BaseViewHolder<M>
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<M>, position: Int) {
        LogUtil.d(Constants.TAG, "$THIS_NAME, onBindViewHolder() called : ViewHolder에 bind 합니다.")

        holder.bindData(modelList[position] as M)
    }

    override fun getItemCount(): Int = modelList.size

    override fun submitList(list: List<BaseModel>?) {
        list?.let {
            modelList = it

            super.submitList(it.toMutableList())
        }
    }
}
