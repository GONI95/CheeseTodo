package sang.gondroid.myapplication.widget.todo

import android.view.View
import sang.gondroid.myapplication.domain.model.BaseModel

interface TodoListener {
    fun onClickItem(v: View, model: BaseModel)
}