package sang.gondroid.cheesetodo.presentation.review

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.gson.*
import org.koin.android.ext.android.inject
import sang.gondroid.cheesetodo.databinding.ActivityDetailReviewBinding
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.presentation.base.BaseActivity
import sang.gondroid.cheesetodo.presentation.todocategory.DetailTodoActivity
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import sang.gondroid.cheesetodo.widget.review.CommentListener
import sang.gondroid.cheesetodo.widget.todo.TodoListener
import java.lang.reflect.Type


class DetailReviewActivity : BaseActivity<DetailReviewViewModel, ActivityDetailReviewBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: DetailReviewViewModel by inject()

    override fun getDataBinding(): ActivityDetailReviewBinding
        = ActivityDetailReviewBinding.inflate(layoutInflater)

    private val commentAdapter by lazy {
        BaseAdapter<CommentModel>( modelList = listOf(), adapterListener = object : CommentListener {
            override fun onClickItem(view: View, position: Int, model: BaseModel) {
                LogUtil.i(Constants.TAG, "$THIS_NAME onClickItem() : $position, $model")

            }
        })
    }

    override fun initViews() {
        binding.commentRecycvlerViewAdapter = commentAdapter

        val bundle = intent.getBundleExtra("bundle")
        bundle?.let {
            val model = it.getSerializable("ReviewTodoItemData") as ReviewTodoModel
            binding.reviewTodoModel = model
            commentAdapter.submitList(model.comments)
        }
    }

    override fun observeData() {

    }

}