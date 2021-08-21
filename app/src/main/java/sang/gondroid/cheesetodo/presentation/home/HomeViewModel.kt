package sang.gondroid.cheesetodo.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sang.gondroid.cheesetodo.data.firebase.CheckFirebaseAuth
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.MyState

class HomeViewModel(
    private val checkFirebaseAuth: CheckFirebaseAuth
) : BaseViewModel() {
    private val THIS_NAME = this::class.simpleName

    override fun fetchData() = viewModelScope.launch {

    }
}