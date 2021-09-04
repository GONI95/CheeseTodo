package sang.gondroid.cheesetodo.presentation.review

import android.net.Uri
import android.os.Bundle
import com.google.gson.*
import org.koin.android.ext.android.inject
import sang.gondroid.cheesetodo.databinding.ActivityDetailReviewBinding
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.presentation.base.BaseActivity
import java.lang.reflect.Type


class DetailReviewActivity : BaseActivity<DetailReviewViewModel, ActivityDetailReviewBinding>() {
    override val viewModel: DetailReviewViewModel by inject()

    override fun getDataBinding(): ActivityDetailReviewBinding
        = ActivityDetailReviewBinding.inflate(layoutInflater)

    override fun initViews() {
        val bundle = intent.getBundleExtra("bundle")
        bundle?.let {
            val model = it.getSerializable("ReviewTodoItemData") as ReviewTodoModel
            binding.reviewTodoModel = model
        }
    }

    override fun observeData() {

    }

}