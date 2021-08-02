package sang.gondroid.myapplication.widget.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import sang.gondroid.myapplication.domain.model.BaseModel

@Suppress("UNCHECKED_CAST")
class BaseAdapter<M : BaseModel>(
    private val modelList : List<BaseModel>,
    private val adapterListener : AdapterListener
) : ListAdapter<BaseModel, BaseViewHolder<M>>(BaseModel.DIFF_CALLBACK){

    private val THIS_NAME = this::class.simpleName


    override fun getItemCount(): Int = modelList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<M> {
        val inflate = LayoutInflater.from(parent.context)

        //TODO: Create ViewHodelr Instance
    }

    override fun onBindViewHolder(holder: BaseViewHolder<M>, position: Int) {
        holder.bindData(modelList[position] as M)
        holder.bindViews(modelList[position] as M, adapterListener)
    }

    override fun submitList(list: MutableList<BaseModel>?) {
        super.submitList(list)
    }

}