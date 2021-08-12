package sang.gondroid.myapplication.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sang.gondroid.myapplication.util.TodoListSortFilter

/**
 * ViewModel에서 공통적으로 사용될 사항들을 정의
 */
open class BaseViewModel : ViewModel() {

    /**
     * 1. 내부적으로 Lifecycler을 관리하도록 stateBundle 선언
     * 2. open 변경자를 통해 상속을 허용하고 Job을 반환하는 fetchData() 생성
     * 3. View에 대한 상태를 저장하기위해 storeState() 생성 [activity, fragment가 종료되기 전까진 해당 데이터가 유지됨]
     */
    //protected var stateBundle : Bundle? = null

    open fun fetchData(): Job = viewModelScope.launch {  }

    /*
    open fun storeState(stateBundle: Bundle) {
        this.stateBundle = stateBundle
    }
     */

}