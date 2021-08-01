package sang.gondroid.myapplication.presentation.todocategory

import android.util.Log
import com.gondroid.cheeseplan.presentation.base.BaseViewModel
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.TodoCategory

class TodoCategoryViewModel(
    private val todoCategory: TodoCategory,
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    init {
        Log.d(Constants.TAG, "$THIS_NAME todoCategory : $todoCategory, ${hashCode()}")
    }
}