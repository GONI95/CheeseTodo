package sang.gondroid.cheesetodo.widget.history

import android.view.View
import org.koin.core.parameter.parametersOf
import sang.gondroid.cheesetodo.databinding.LayoutSearchHistoryItemBinding
import sang.gondroid.cheesetodo.domain.model.SearchHistoryModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.widget.base.BaseViewHolder

class SearchHistoryViewHolder(
    private val binding: LayoutSearchHistoryItemBinding,
    private val onItemClick : (view : View, position: Int) -> Unit
) : BaseViewHolder<SearchHistoryModel>(binding) {

    private val THIS_NAME = this::class.simpleName

    override fun bindData(model: SearchHistoryModel) {
        binding.searchHistoryModel = model
    }

    init {
        LogUtil.d(Constants.TAG, "$THIS_NAME, init() called ")

        binding.historyDeleteImageView.setOnClickListener {
            LogUtil.d(Constants.TAG, "$THIS_NAME, setOnClickListener() called : 사용자 이벤트가 발생했습니다. (ImageView)")

            onItemClick(it, adapterPosition)
        }

        binding.historyValueTextView.setOnClickListener {
            LogUtil.d(Constants.TAG, "$THIS_NAME, setOnClickListener()dd called : 사용자 이벤트가 발생했습니다. (TextView)")

            onItemClick(it, adapterPosition)
        }
    }
}