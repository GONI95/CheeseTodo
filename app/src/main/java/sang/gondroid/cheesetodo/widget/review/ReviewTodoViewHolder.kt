package sang.gondroid.cheesetodo.widget.review

import android.view.View
import sang.gondroid.cheesetodo.databinding.LayoutReviewTodoItemBinding
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.widget.base.BaseViewHolder

class ReviewTodoViewHolder(
    private val binding : LayoutReviewTodoItemBinding,
    private val onItemClick : (view : View, position: Int) -> Unit
) : BaseViewHolder<ReviewTodoModel>(binding) {

    private val THIS_NAME = this::class.simpleName

    override fun bindData(model: ReviewTodoModel) {
        binding.reviewTodoModel = model
    }

    init {
        LogUtil.d(Constants.TAG, "$THIS_NAME, init() called ")

        binding.root.setOnClickListener {
            LogUtil.d(Constants.TAG, "$THIS_NAME, setOnClickListener() called : 사용자 이벤트가 발생했습니다.")

            onItemClick(binding.reviewTodoTitleTextView, adapterPosition)
        }
    }
}