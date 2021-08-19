package sang.gondroid.cheesetodo.presentation.my

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.presentation.base.BaseViewModel
import sang.gondroid.cheesetodo.util.Constants

class MyViewModel(
    private val appPreferenceManager: AppPreferenceManager,
    private val ioDispatchers : CoroutineDispatcher
) : BaseViewModel() {

    private val THIS_NAME = this::class.simpleName

    fun saveToken(idToken: String, disPlayName: String) = viewModelScope.launch(ioDispatchers){
        withContext(Dispatchers.IO) {
            Log.d(Constants.TAG, "$THIS_NAME saveToken() called")
            appPreferenceManager.putIdToken(idToken)
            appPreferenceManager.setUserNameString(disPlayName)

            fetchData()
        }
    }
}