package sang.gondroid.myapplication.presentation.review

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
import sang.gondroid.myapplication.databinding.FragmentReviewBinding
import sang.gondroid.myapplication.presentation.home.HomeFragment
import sang.gondroid.myapplication.presentation.home.HomeViewModel
import sang.gondroid.myapplication.util.Constants


class ReviewFragment  : BaseFragment<ReviewViewModel, FragmentReviewBinding>() {
    override val viewModel: ReviewViewModel by viewModel()

    override fun getDataBinding(): FragmentReviewBinding
            = FragmentReviewBinding.inflate(layoutInflater)

    override fun observeData() { }

    companion object {
        fun newInstance() = ReviewFragment()

        const val TAG = "ReviewFragment"
    }
}