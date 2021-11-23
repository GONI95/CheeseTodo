package sang.gondroid.cheesetodo.presentation.todocategory

import android.text.Editable
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import org.koin.android.ext.android.bind
import sang.gondroid.cheesetodo.widget.custom.CustomDialogClickListener
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.CheeseTodoApplication
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
    private lateinit var categoryValue: TodoCategory
    private var importanceValue by Delegates.notNull<Int>()

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
            categoryValue = model.category   //category 초기화
            importanceValue = model.importanceId    //중요도 초기화

            binding.todoItem = model
        }

        binding.detatilViewModel = viewModel
        binding.handler = this

        with(binding) {

            /**
             * Gon : values/spinner_array.xml와 를 DataBinding 레이아웃 표현식으로 처리하고 싶었지만, 보류
             *       [21.11.21]
             */
            ArrayAdapter.createFromResource(
                this@DetailTodoActivity, R.array.importance_array, R.layout.support_simple_spinner_dropdown_item
            ).also {
                it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                layoutWriteModeTitle.writeModeImportanceSpinner.adapter = it
            }

            /**
             * Gon : OptionMenu를 DataBinding 레이아웃 표현식으로 처리하고 싶었지만, 보류
             *
             *       [21.11.21]
             */
            readModeToolbar.setOnMenuItemClickListener(this@DetailTodoActivity)
            writeModeToolbar.setOnMenuItemClickListener(this@DetailTodoActivity)
            layoutWriteModeTitle.writeModeExpandTextView.setOnClickListener(cardViewExtension)

        }
    }

    /**
     *
     */
    private val cardViewExtension = View.OnClickListener {
        with(binding.layoutWriteModeTitle) {
            if (writeModeHiddenLayout.visibility == View.VISIBLE) {
                TransitionManager.endTransitions(writeModeTitleNeumorphCardView)

                val drawable =
                CheeseTodoApplication.appContext?.let { instance -> ContextCompat.getDrawable(instance, R.drawable.ic_expand_more) }

                writeModeExpandTextView.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, drawable, null
                )

                writeModeHiddenLayout.visibility = View.GONE
            } else {
                TransitionManager.beginDelayedTransition(writeModeTitleNeumorphCardView, AutoTransition())

                val drawable =
                    CheeseTodoApplication.appContext?.let { instance -> ContextCompat.getDrawable(instance, R.drawable.ic_expand_less) }

                writeModeExpandTextView.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, drawable, null
                )

                writeModeHiddenLayout.visibility = View.VISIBLE
            }
        }
    }



    /**
     * Gon : TextView의 값이 빈값인 경우 Error가 발생했을 때, 값이 입력되면 Error 문구를 지우기위한 메서드 입니다.
     *       addtextchangedlistener의 afterTextChanged() 이벤트 핸들러 메서드(리스너 바인딩 방식)
     *       [21.11.21]
     */
    fun titleAfterTextChanged(editable: Editable?) {
        if(!editable.toString().isEmpty()) binding.layoutWriteModeTitle.writeModeTitleTextInputLayout.error = null
    }

    fun todoAfterTextChanged(editable: Editable?) {
        if(!editable.toString().isEmpty()) binding.layoutWriteModeInfo.writeModeTodoTextInputLayout.error = null
    }

    /**
     * Gon : 수정 작업 중 Importance 변경 값을 수신받아 멤버변수 importanceId에 저장하는 메서드 입니다.
     *       OnItemSelectedListener의 onItemSelected() 이벤트 핸들러 메서드(메서드 참조 방식, Spinner)
     *       [21.11.21]
     */
    fun onImportanceItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        importanceValue = position
    }

    /**
     * Gon : 수정 작업 중 Category 변경 값을 수신받아 멤버변수 category에 저장하는 메서드 입니다.
     *       OnCheckedChangeListener의 onCheckedChanged() 이벤트 핸들러 메서드(메서드 참조 방식, RadioButton)
     *       [21.11.21]
     */
    fun onCategoryCheckChanged(group: RadioGroup?, checkedId: Int) {
        categoryValue = when (checkedId) {
            R.id.android_radio_button -> TodoCategory.ANDROID
            R.id.lauguage_radio_button -> TodoCategory.LANGUAGE
            R.id.db_radio_button -> TodoCategory.DB
            R.id.other_radio_button -> TodoCategory.OTHER

            else -> TodoCategory.ALL
        }
    }

    /**
     * Gon : Menu 별 수정, 업로드, 삭제 이벤트를 발생시키기 위한 메서드 입니다.
     *       OnMenuItemClickListener의 onMenuItemClick() 이벤트 핸들러 메서드(메서드 참조 방식, MenuButton)
     *       [21.11.21]
     */
    override fun onMenuItemClick(item: MenuItem): Boolean = when (item.itemId) {
        R.id.edit_item -> {
            with(binding) {
                readModeTitleText = layoutReadModeTitle.readModeTitleTextView.text.toString()
                readModeTodoText = layoutReadModeInfo.readModeTodoTextView.text.toString()
                readModeDifficultText = layoutReadModeInfo.readModeDifficultTextView.text.toString()

                readModeConstraintLayout.visibility = View.GONE
                writeModeConstraintLayout.visibility = View.VISIBLE
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
            with(binding) {
                val (titleText, todoText) =
                    layoutWriteModeTitle.writeModeTitleEditText.text to layoutWriteModeInfo.writeModeTodoEditText.text

                if (titleText.isNullOrEmpty()) layoutWriteModeTitle.writeModeTitleTextInputLayout.error = getString(R.string.please_enter_the_text)

                else if (todoText.isNullOrEmpty()) layoutWriteModeInfo.writeModeTodoTextInputLayout.error = getString(R.string.please_enter_the_text)

                else {
                    TodoModel(model.id, model.date, categoryValue, importanceValue, titleText.toString(), todoText.toString(),
                        layoutWriteModeInfo.writeModeDifficultEditText.text.toString()).let { viewModel.updateData(it) }
                }
            }

            true
        }

        else -> {
            false
        }
    }

    /**
     * Gon : Todo_CardView, Difficult_CardView의 가시성 변경을 위한 메서드 입니다.
     *       OnClickListener의 onClick() 이벤트 핸들러 메서드(메서드 참조 방식)
     *       [21.11.21]
     */
    fun onReadModeTransformCardViewClick(view: View) {
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
    fun onWriteModeTransformCardViewClick(view: View) {
        with(binding) {
            when(view) {
                layoutWriteModeTransform.writeModeTodoCardView -> {
                    layoutWriteModeInfo.writeModeTodoCardView.visibility = View.VISIBLE
                    layoutWriteModeInfo.writeModeDifficultCardView.visibility = View.GONE
                }
                layoutWriteModeTransform.writeModeDifficultCardView -> {
                    layoutWriteModeInfo.writeModeTodoCardView.visibility = View.GONE
                    layoutWriteModeInfo.writeModeDifficultCardView.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Gon : TodoItem 삭제 여부를 묻는 Dialog 생성 메서드 입니다.
     *       [21.11.21]
     */
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