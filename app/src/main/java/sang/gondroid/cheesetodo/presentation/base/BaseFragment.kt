package sang.gondroid.cheesetodo.presentation.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Job
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil

abstract class BaseFragment<VM : BaseViewModel, VB : ViewDataBinding> : Fragment() {
    private val THIS_NAME = this::class.simpleName

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
    open fun initViews() = Unit

    /**
     * Gon : LiveData를 관찰하는 observeData()
     */
    abstract fun observeData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getDataBinding()
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogUtil.v(Constants.TAG, "$THIS_NAME, onViewCreated() called : ${hashCode()}")
        initViews()
        observeData()
    }

    override fun onResume() {
        LogUtil.v(Constants.TAG, "$THIS_NAME, onResume() called : ${hashCode()}")
        super.onResume()

        viewModel.fetchData()
    }

    /**
     * Gon : Fragment 제거 시 viewModel의 fetchData()에서 사용하는 coroutine이 살아있는 경우 중지시킵니다.
     */
    override fun onDestroy() {
        LogUtil.v(Constants.TAG, "$THIS_NAME, onDestroy() called : ${hashCode()}")
        if (viewModel.fetchData().isActive)   viewModel.fetchData().cancel()
        super.onDestroy()
    }
}