package sang.gondroid.cheesetodo.presentation.review

import android.view.View
import android.widget.CheckBox
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
import sang.gondroid.cheesetodo.util.NetworkConnection
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import sang.gondroid.cheesetodo.widget.review.CommentListener


class DetailReviewActivity : BaseActivity<DetailReviewViewModel, ActivityDetailReviewBinding>() {
    private val THIS_NAME = this::class.simpleName

    /**
     * Gon : 네트워크 연결 상태를 확인하기 위한 NetworkConnection class
     *       [21.11.28]
     */
    private val connection : NetworkConnection by inject()

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
        binding.reviewDetailViewModel = viewModel

        val bundle = intent.getBundleExtra("bundle")
        bundle?.let {
            val model = it.getSerializable("ReviewTodoItemData") as ReviewTodoModel
            binding.reviewTodoModel = model
            viewModel.getComments(model)
            viewModel.getCheckedCurrentUser(model)
            viewModel.getCheckedUserCount(model)
            observeData()
        }
    }

    /**
     * comment 추가 작업
     */
    fun insertCommentOnClick(reviewTodoModel : ReviewTodoModel) {
        with(binding) {
            viewModel.insertComment(commentValueEditText.text.toString(), reviewTodoModel)
        }
    }

    /**
     * Gon : like 추가 작업 [ onCheckedChanged 리스너 사용 시 Review 열람할 때 checked 값으로 인해서 passCount가 증가 ]
     */
    fun onClickPassButton(view: View, reviewTodoModel : ReviewTodoModel) {
        LogUtil.d(Constants.TAG, "$THIS_NAME insertPassButton ... ")

        if (view is CheckBox && view.isChecked) {
            LogUtil.d(Constants.TAG, "$THIS_NAME insertPassButton checked")
            viewModel.insertCheckedUser(reviewTodoModel)
        }
        else {
            LogUtil.d(Constants.TAG, "$THIS_NAME insertPassButton not checked")
            viewModel.deleteUnCheckedUser(reviewTodoModel)
        }
    }

    override fun observeData() = with(binding) {
        /**
         * Gon : connection(네트워크 변경 상태에 따라 true, false) 값에 따라 서비스 이용이 유무에 대응하는 layout을 표시
         *       [update - 21.11.28]
         */
        connection.observe(this@DetailReviewActivity, Observer { isConnected ->
            if (isConnected) {
                LogUtil.d(Constants.TAG, "$THIS_NAME, 네트워크 연결 상태 : On ")

                detailReviewLayoutDisconnected.visibility = View.GONE
                detailReviewLayoutConnected.visibility = View.VISIBLE

            } else {
                LogUtil.d(Constants.TAG, "$THIS_NAME, 네트워크 연결 상태 : Off ")

                detailReviewLayoutDisconnected.visibility = View.VISIBLE
                detailReviewLayoutConnected.visibility = View.GONE
            }
        })

        viewModel.getCommentJobStateLiveData.observe(this@DetailReviewActivity, Observer {
            when(it) {
                is JobState.True.Result<*> -> {
                    LogUtil.d(Constants.TAG, "$THIS_NAME getCommentJobStateLiveData True")
                    commentAdapter.submitList(it.data as List<CommentModel>)
                }
                is JobState.Error -> {
                    Toast.makeText(this@DetailReviewActivity, R.string.an_error_occurred, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.insertCheckedUserJobStateLiveData.observe(this@DetailReviewActivity, Observer {
            when(it) {
                is JobState.False -> {
                    Toast.makeText(this@DetailReviewActivity, R.string.insert_pass_information_job_error, Toast.LENGTH_SHORT).show()
                }
                is JobState.Error -> {
                    Toast.makeText(this@DetailReviewActivity, R.string.an_error_occurred, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getCheckedCurrentUserBooleanLiveData.observe(this@DetailReviewActivity, Observer {
            when(it) {
                false -> {
                    Toast.makeText(this@DetailReviewActivity, R.string.get_pass_information_job_error, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.deleteCheckedUserLiveData.observe(this@DetailReviewActivity, Observer {
            when(it) {
                is JobState.False -> {
                    Toast.makeText(this@DetailReviewActivity, R.string.delete_pass_information_job_error, Toast.LENGTH_SHORT).show()
                }
                is JobState.Error -> {
                    Toast.makeText(this@DetailReviewActivity, R.string.an_error_occurred, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}