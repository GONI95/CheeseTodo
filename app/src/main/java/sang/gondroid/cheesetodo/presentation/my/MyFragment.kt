package sang.gondroid.cheesetodo.presentation.my

import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.databinding.FragmentMyBinding
import sang.gondroid.cheesetodo.presentation.base.BaseFragment


class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    override val viewModel: MyViewModel by viewModel()

    override fun getDataBinding(): FragmentMyBinding
        = FragmentMyBinding.inflate(layoutInflater)

    override fun observeData() {}

    companion object {
        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }
}