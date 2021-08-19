package sang.gondroid.cheesetodo.widget.base

import sang.gondroid.cheesetodo.databinding.LayoutEmptyItemBinding
import sang.gondroid.cheesetodo.domain.model.BaseModel

class EmptyViewHolder(
    private val binding: LayoutEmptyItemBinding
) : BaseViewHolder<BaseModel>(binding) {

    override fun bindData(model: BaseModel) = Unit
}