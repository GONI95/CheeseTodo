package sang.gondroid.cheesetodo.widget.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import sang.gondroid.cheesetodo.domain.model.BaseModel

abstract class BaseViewHolder<M : BaseModel> (
    binding : ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {

    /**
     * View에 bind 작업을 하는 중, 이전의 data가 남아있을 수 있어 초기화시키는 작업
     */
    open fun reset() {}

    /**
     * model을 전달받아 View에 bind하는 작업
     */
    abstract fun bindData(model : M)
}