package sang.gondroid.cheesetodo.widget.review

import android.view.View
import sang.gondroid.cheesetodo.databinding.LayoutCommentItemBinding
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.widget.base.BaseViewHolder

class CommentViewHolder(
    private val binding: LayoutCommentItemBinding,
    private val onItemClick : (view : View, position : Int) -> Unit
) : BaseViewHolder<CommentModel>(binding) {

    private val THIS_NAME = this::class.simpleName

    override fun bindData(model: CommentModel) {
        binding.commentModel = model
    }

    init {
        LogUtil.d(Constants.TAG, "$THIS_NAME, init() called ")

        binding.root.setOnClickListener {
            LogUtil.d(Constants.TAG, "$THIS_NAME, setOnClickListener()dd called : 사용자 이벤트가 발생했습니다.")

            onItemClick(it, adapterPosition)
        }
    }
}