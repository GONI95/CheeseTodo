package sang.gondroid.myapplication.presentation.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gondroid.cheeseplan.presentation.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.FragmentHomeBinding
import sang.gondroid.myapplication.databinding.FragmentMyBinding
import sang.gondroid.myapplication.presentation.my.MyViewModel
import sang.gondroid.myapplication.util.Constants


class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: HomeViewModel by viewModel()

    override fun getDataBinding(): FragmentHomeBinding
            = FragmentHomeBinding.inflate(layoutInflater)

    override fun observeData() {}

    companion object {
        fun newInstance() = HomeFragment()

        const val TAG = "HomeFragment"
    }
}