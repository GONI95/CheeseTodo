package sang.gondroid.cheesetodo.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

/**
 * Activity에서 공통적으로 사용될 사항들을 정의
 * open : 하위 Class에서 재정의할 수 있다. 코틀린은 final이 원칙이라, 상속을 허용할거면 open을 붙여야한다.
 * abstract : 하위 Class에서 반드시 재정의해야한다. abstract 선언된 Class는 인스턴스화 불가능, 기본적으로 open이다.
 * Generic : Class 내부에서 사용할 데이터의 타입을 외부에서 지정
 * Protected : private + 상속받은 Class에서 접근이 가능 [protected 멤버를 오버라이딩한 멤버에 따로 접근 제한자를 선언하지 않으면 protected를 따름]
 *
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewDataBinding> : AppCompatActivity() {

    /**
     * 1. Generic 타입으로 받기때문에 viewModel과 viewBining은 Generic 타입으로 선언이 가능
     * 2. onCreate() 후에 처리하기 위해 lateinit var 프로퍼티로 선언
     * 3. viewModel에서 선언한 fetchData()에서 사용하는 coroutine을 Lifecycler에 맞게 제거하기위해 선언
     */
    abstract val viewModel : VM
    protected lateinit var binding : VB

    abstract fun getDataBinding() : VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getDataBinding()
        binding.lifecycleOwner = this
        setContentView(binding.root)
        initViews()
    }

    /**
     * 1. View 초기화를 위한 initViews()
     * 2. LiveData를 처리하는 observeData()
     */
    open fun initViews() {
        observeData()
    }

    abstract fun observeData()
}