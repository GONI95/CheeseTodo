package sang.gondroid.cheesetodo.presentation.todocategory

import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import sang.gondroid.cheesetodo.widget.custom.CustomDialogClickListener
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.databinding.ActivityDetailTodoBinding
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.presentation.base.BaseActivity
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.TodoCategory
import sang.gondroid.cheesetodo.widget.custom.CustomDialog
import kotlin.properties.Delegates

class DetailTodoActivity : BaseActivity<DetailTodoViewModel, ActivityDetailTodoBinding>(),
    Toolbar.OnMenuItemClickListener {
    private val THIS_NAME = this::class.simpleName
    private lateinit var model: TodoModel
    private lateinit var category: TodoCategory
    private var importance by Delegates.notNull<String>()
    private var importanceId by Delegates.notNull<Int>()

    override val viewModel: DetailTodoViewModel by viewModel()

    override fun getDataBinding(): ActivityDetailTodoBinding =
        ActivityDetailTodoBinding.inflate(layoutInflater)

    override fun initViews() {
        super.initViews()

        /**
         * TodoCategoryFragment의 리사이클러뷰 클릭 이벤트를 통해 받은 Bundle 객체를 이용
         */
        val bundle = this.intent.getBundleExtra("bundle")
        bundle?.let {
            model = it.getSerializable("TodoItemData") as TodoModel
            category = model.category   //category 초기화
            binding.todoItem = model
        }

        binding.detatilViewModel = viewModel
        binding.handler = this

        with(binding) {
            ArrayAdapter.createFromResource(
                this@DetailTodoActivity,
                R.array.importance_array,
                R.layout.support_simple_spinner_dropdown_item
            ).also {
                it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                //editModeImportanceSpinner.adapter = it
            }

            /**
             * OptionMenu를 DataBinding 레이아웃 표현식으로 처리하고 싶었지만, 보류
             */
            readModeToolbar.setOnMenuItemClickListener(this@DetailTodoActivity)
            //editModeToolbar.setOnMenuItemClickListener(this@DetailTodoActivity)
        }
    }

    /**
     * 1, 2. addtextchangedlistener의 afterTextChanged() 이벤트 핸들러 메서드(리스너 바인딩 방식)
     *
     * 3. OnItemSelectedListener의 onItemSelected() 이벤트 핸들러 메서드(메서드 참조 방식, Spinner)
     *
     * 4. OnCheckedChangeListener의 onCheckedChanged() 이벤트 핸들러 메서드(메서드 참조 방식, RadioButton)
     *
     * 5. OnMenuItemClickListener의 onMenuItemClick() 이벤트 핸들러 메서드(메서드 참조 방식, Button)
     */
    fun titleAfterTextChanged(editable: Editable?) {
        //if(!editable.toString().isEmpty()) binding.editModeTitleEditLayout.error = null
    }

    fun todoAfterTextChanged(editable: Editable?) {
        //if(!editable.toString().isEmpty()) binding.editModeTodoEditLayout.error = null
    }

    fun onImportanceItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        importance = parent.getItemAtPosition(position).toString()
        importanceId = position
    }

    fun onCategoryCheckChanged(group: RadioGroup?, checkedId: Int) {
        category = when (checkedId) {
            R.id.androidRadioButton -> TodoCategory.ANDROID
            R.id.lauguageRadioButton -> TodoCategory.LANGUAGE
            R.id.dbRadioButton -> TodoCategory.DB
            R.id.otherRadioButton -> TodoCategory.OTHER

            else -> TodoCategory.ALL
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean = when (item.itemId) {
        R.id.edit_item -> {
            with(binding) {
                readModeConstraintLayout.visibility = View.GONE
                //editModeConstraintLayout.visibility = View.VISIBLE
            }
            true
        }

        R.id.upload_item -> {
            with(binding) {
                if (layoutReadModeInfo.readModeDifficultTextView.text.isNullOrEmpty())
                    Toast.makeText(this@DetailTodoActivity, R.string.there_are_blank_entries, Toast.LENGTH_LONG).show()
                else
                    viewModel.uploadReviewTodo(model)
            }
            true
        }

        R.id.delete_item -> {
            createDeleteDialog()
            true
        }

        R.id.check_item -> {
            /*
            with(binding) {
                val (titleText, todoText) = editModeTitleEditLayout.editText?.text to editModeTodoEditLayout.editText?.text

                if (titleText.isNullOrEmpty())  editModeTitleEditLayout.error = getString(R.string.please_enter_the_text)

                else if (todoText.isNullOrEmpty())  editModeTodoEditLayout.error = getString(R.string.please_enter_the_text)

                else {
                    TodoModel(model.id, model.date, category, importanceId, titleText.toString(), todoText.toString(),
                        editModeDifficultEdit.text.toString()).let { viewModel.updateData(it) }
                }
            }
             */
            true
        }

        else -> {
            false
        }
    }

    private fun createDeleteDialog() {
        CustomDialog(this, getString(R.string.delete_todo_dialog_title), getString(R.string.delete_todo_dialog_description),
            object : CustomDialogClickListener {
                override fun onPositiveClick() {
                    LogUtil.v(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Positive")
                    viewModel.deleteData(model.id)
                }
                override fun onNegativeClick() {
                    LogUtil.v(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Negative")
                }
            }).show()
    }

    /**
     * Gon : Todo_CardView, Difficult_CardView의 가시성 변경을 위한 메서드 입니다.
     *       OnClickListener의 onClick() 이벤트 핸들러 메서드(메서드 참조 방식)
     *       [21.11.21]
     */
    fun onTransformCardViewClick(view: View) {
        with(binding) {
            when(view) {
                layoutReadModeTransform.readModeTodoCardView -> {
                    layoutReadModeInfo.readModeTodoCardView.visibility = View.VISIBLE
                    layoutReadModeInfo.readModeDifficultCardView.visibility = View.GONE
                }
                layoutReadModeTransform.readModeDifficultCardView -> {
                    layoutReadModeInfo.readModeTodoCardView.visibility = View.GONE
                    layoutReadModeInfo.readModeDifficultCardView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun observeData() {
        viewModel.jobState.observe(this) { jobState ->

            when (jobState) {
                is JobState.Error -> {
                    LogUtil.e(Constants.TAG, "$THIS_NAME observeData() jobState : ${getString(jobState.messageId, jobState.e)}")
                    Toast.makeText(this, R.string.an_error_occurred, Toast.LENGTH_LONG).show()

                    finish()
                }
                is JobState.True -> {
                    LogUtil.v(Constants.TAG, "$THIS_NAME observeData() jobState : $jobState")

                    finish()
                }
                is JobState.Uninitialized -> {
                    LogUtil.v(Constants.TAG, "$THIS_NAME observeData() jobState : $jobState")
                    Toast.makeText(this, R.string.login_is_required, Toast.LENGTH_LONG).show()
                }
                else -> {
                    LogUtil.w(Constants.TAG, "$THIS_NAME observeData() jobState else $jobState")
                    Toast.makeText(this, R.string.request_false, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}