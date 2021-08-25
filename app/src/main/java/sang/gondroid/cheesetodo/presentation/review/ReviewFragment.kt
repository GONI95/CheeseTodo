package sang.gondroid.cheesetodo.presentation.review

import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.databinding.FragmentReviewBinding
import sang.gondroid.cheesetodo.presentation.base.BaseFragment


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