package sang.gondroid.myapplication.presentation.my

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gondroid.cheeseplan.presentation.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.FragmentMyBinding
import sang.gondroid.myapplication.presentation.home.HomeFragment


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