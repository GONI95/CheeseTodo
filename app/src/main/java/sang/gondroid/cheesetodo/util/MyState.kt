package sang.gondroid.cheesetodo.util

import androidx.annotation.StringRes

/**
 * 상위 클래스를 상속한 하위 클래스 정의를 제한할 수 있다.
 */
sealed class MyState {
    // MyState를 상속받는 singleton 객체를 선언
    object Uninitialized : MyState()    // 초기화되지 않은 상태
    object Loading : MyState()  // 로딩 중인 상태

    // 로그인 상태 (idToken을 가짐)
    data class Login(
        val idData : String,
        val nameData : String
    ) : MyState()

    // 반환이 성공적으로 이루어진 상태
    sealed class Success : MyState() {
        // 로그인이 된 상태
        data class Registered<out T, R>(
            val userName: T,
            val userImageUri: R?
        ) : Success()

        // 로그인이 되지않은 상태
        object NotRegistered : Success()
    }

    // 반환이 성공적으로 이루어지지 않은 상태
    data class Error (
        @StringRes val messageId : Int,
        val e : Throwable
    ): MyState()

}