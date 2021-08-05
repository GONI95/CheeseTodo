package sang.gondroid.myapplication.widget.base

import sang.gondroid.myapplication.databinding.LayoutEmptyItemBinding
import sang.gondroid.myapplication.domain.model.BaseModel

class EmptyViewHolder(
    private val binding: LayoutEmptyItemBinding
) : BaseViewHolder<BaseModel>(binding) {

    override fun bindData(model: BaseModel) = Unit
}