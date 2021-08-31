package sang.gondroid.cheesetodo.widget.review

import android.view.View
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.widget.base.AdapterListener

interface ReviewTodoListener : AdapterListener {
    fun onClickItem(view: View, position: Int, model: BaseModel)
}