package sang.gondroid.myapplication.widget.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import sang.gondroid.myapplication.domain.model.BaseModel

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
    open fun bindData(model : M) {}
    
 
    /**
     * bind된 View에 이벤트가 발생하면, AdapterListener를 구현한 곳에서 처리
     */
    abstract fun bindViews(model: M, adapterListener: AdapterListener)

}