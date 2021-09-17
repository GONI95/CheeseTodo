package sang.gondroid.cheesetodo.presentation.review

import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import org.koin.android.ext.android.inject
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.databinding.ActivityDetailReviewBinding
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.presentation.base.BaseActivity
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import sang.gondroid.cheesetodo.widget.review.CommentListener


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
        binding.handler = this

        val bundle = intent.getBundleExtra("bundle")
        bundle?.let {
            val model = it.getSerializable("ReviewTodoItemData") as ReviewTodoModel
            binding.reviewTodoModel = model
            viewModel.getComments(model.modelId)
            observeData()
        }
    }

    /**
     * comment 추가 작업
     */
    fun insertCommentOnClick(modelId : Long) {
        with(binding) {
            viewModel.insertComment(commentValueEditText.text.toString(), modelId)
        }
    }

    override fun observeData() {
        LogUtil.d(Constants.TAG, "$THIS_NAME getCommentJobStateLiveData ...")
        viewModel.getCommentJobStateLiveData.observe(this, Observer {
            when(it) {
                is JobState.True.Result<*> -> {
                    LogUtil.d(Constants.TAG, "$THIS_NAME getCommentJobStateLiveData True")
                    commentAdapter.submitList(it.data as List<CommentModel>)
                }
                is JobState.False -> {
                    Toast.makeText(this, R.string.get_comment_jobstate_false, Toast.LENGTH_SHORT).show()
                }
                is JobState.Error -> {
                    Toast.makeText(this, R.string.get_comment_jobstate_error, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    LogUtil.w(Constants.TAG, "$THIS_NAME getComments() else : $this")
                }
            }
        })
    }

}