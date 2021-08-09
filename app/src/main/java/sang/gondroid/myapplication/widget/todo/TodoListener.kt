package sang.gondroid.myapplication.widget.todo

import android.view.View
import sang.gondroid.myapplication.domain.model.BaseModel
import sang.gondroid.myapplication.widget.base.AdapterListener

interface TodoListener : AdapterListener {
    fun onClickItem(view: View, position: Int, model: BaseModel)
}