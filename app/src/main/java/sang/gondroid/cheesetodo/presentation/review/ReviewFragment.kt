package sang.gondroid.cheesetodo.presentation.review

import android.os.Bundle
import android.view.View
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.databinding.FragmentReviewBinding
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.presentation.base.BaseFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.TodoCategory
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import sang.gondroid.cheesetodo.widget.review.ReviewTodoListener


class ReviewFragment  : BaseFragment<ReviewViewModel, FragmentReviewBinding>() {
    private val THIS_NAME = this::class.simpleName

    override val viewModel: ReviewViewModel by viewModel()

    private val adapter by lazy {
        BaseAdapter<ReviewTodoModel>(
            modelList = listOf(),
            adapterListener = object : ReviewTodoListener {
                override fun onClickItem(view: View, position: Int, model: BaseModel) {
                    LogUtil.i(Constants.TAG, "$THIS_NAME onClickItem() : $position, $model")

                    val bundle = Bundle()
                    bundle.putSerializable("ReviewTodoItemData", model)
                }
            }
        )
    }

    override fun getDataBinding(): FragmentReviewBinding
            = FragmentReviewBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {

        reviewTodoRecyclerView.adapter = adapter
    }

    override fun observeData() {
        adapter.submitList(
            listOf(
                ReviewTodoModel(1, "asd","wrtj","erm",TodoCategory.ALL,true, 1233123, "gfh,gj,gh,m,ty,ty,tyenetdfgherh", "", "",
                arrayListOf(HashMap<String, CommentModel>())),
                ReviewTodoModel(2, "qwe","qwe","rym",TodoCategory.ALL,true, 12356123, "sjdgnmkjfgnerhdfgerh", "", "",
                    arrayListOf(HashMap<String, CommentModel>())),
                ReviewTodoModel(3, "dfb","m,rtym","rmrym",TodoCategory.ALL,true, 1231623, "erdfgherh", "", "",
                    arrayListOf(HashMap<String, CommentModel>()))
            )
        )

    }

    companion object {
        fun newInstance() = ReviewFragment()

        const val TAG = "ReviewFragment"
    }
}