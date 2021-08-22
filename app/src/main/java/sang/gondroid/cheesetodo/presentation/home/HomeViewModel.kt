package sang.gondroid.cheesetodo.presentation.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.data.firebase.HandlerFirebaseAuth
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel

class HomeViewModel(
    private val handlerFirebaseAuth: HandlerFirebaseAuth
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    override fun fetchData() = viewModelScope.launch {

    }
}