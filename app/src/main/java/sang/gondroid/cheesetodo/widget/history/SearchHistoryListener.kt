package sang.gondroid.cheesetodo.widget.history

import android.view.View
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.SearchHistoryModel
import sang.gondroid.cheesetodo.widget.base.AdapterListener

interface SearchHistoryListener: AdapterListener {
    fun onClickItem(v: View, model: BaseModel)
    fun onClickItem(v: View, query : String)
}