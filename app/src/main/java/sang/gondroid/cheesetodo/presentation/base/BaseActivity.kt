package sang.gondroid.cheesetodo.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

/**
 * Gon : Activity에서 공통적으로 사용될 메서드와 프로퍼티를 정의했습니다.
 *       abstract 변경자 - 추상 클래스로, 공통 메서드, 프로퍼티 정의에 사용 (상속이 이루어져야함)
 *       open 변경자 - 무분별한 상속을 막기 위한 변경자로 기본적으로 final, 상속을 허용하려면 open 변경자가 필요
 *       generic - 클래스 및 메서드 정의 시 Type을 확실히 정하지 않고, 클래스 내부에서 사용할 데이터의 Type을 외부에서 지정
 */
abstract class BaseActivity<VM : BaseViewModel, VB : ViewDataBinding> : AppCompatActivity() {

    /**
     * Gon : viewModel과 binding을 외부에서 받는 Generic 타입으로 선언했습니다.
     */
    abstract val viewModel : VM

    protected lateinit var binding : VB

    /**
     * Gon : DataBinding 초기화를 위한 getDataBinding()
     */
    abstract fun getDataBinding() : VB

    /**
     * Gon : View 초기화를 위한 initViews()
     */
    open fun initViews() {
        observeData()
    }

    /**
     * Gon : LiveData를 관찰하는 observeData()
     */
    abstract fun observeData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getDataBinding()
        binding.lifecycleOwner = this
        setContentView(binding.root)

        initViews()
    }
}