package sang.gondroid.cheesetodo.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Gon : ViewModel에서 공통적으로 사용될 메서드를 정의했습니다.
 */
open class BaseViewModel : ViewModel() {

    /**
     * Gon : open 변경자를 통해 상속을 허용해 재정의가 가능하고 Coroutine Job을 반환하는 fetchData() 생성했습니다.
     */
    open fun fetchData(): Job = viewModelScope.launch {  }
}