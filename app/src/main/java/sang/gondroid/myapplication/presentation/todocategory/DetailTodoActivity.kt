package sang.gondroid.myapplication.presentation.todocategory

import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import sang.gondroid.myapplication.widget.custom.CustomDialogClickListener
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.ActivityDetailTodoBinding
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.presentation.base.BaseActivity
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.JobState
import sang.gondroid.myapplication.util.TodoCategory
import sang.gondroid.myapplication.util.toImportanceString
import sang.gondroid.myapplication.widget.custom.CustomDialog
import kotlin.properties.Delegates

class DetailTodoActivity : BaseActivity<DetailTodoViewModel, ActivityDetailTodoBinding>(),
    Toolbar.OnMenuItemClickListener {
    private val THIS_NAME = this::class.simpleName
    private lateinit var model: TodoModel
    private lateinit var category: TodoCategory
    private var importance by Delegates.notNull<String>()
    private var importanceId by Delegates.notNull<Int>()

    override val viewModel: DetailTodoViewModel by viewModel()

    override fun getViewBinding(): ActivityDetailTodoBinding =
        ActivityDetailTodoBinding.inflate(layoutInflater)

    override fun initViews() {
        super.initViews()

        /**
         * TodoCategoryFragment의 리사이클러뷰 클릭 이벤트를 통해 받은 Bundle 객체를 이용
         */
        val bundle = this.intent.getBundleExtra("bundle")
        bundle?.let {
            model = bundle.getSerializable("TodoItemData") as TodoModel
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
                editModeImportanceSpinner.adapter = it
            }

            /**
             * OptionMenu를 DataBinding 레이아웃 표현식으로 처리하고 싶었지만, 보류
             */
            readModeToolbar.setOnMenuItemClickListener(this@DetailTodoActivity)
            editModeToolbar.setOnMenuItemClickListener(this@DetailTodoActivity)
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
        if(!editable.toString().isEmpty()) binding.editModeTitleEditLayout.error = null
    }

    fun todoAfterTextChanged(editable: Editable?) {
        if(!editable.toString().isEmpty()) binding.editModeTodoEditLayout.error = null
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
                editModeConstraintLayout.visibility = View.VISIBLE
            }
            true
        }

        R.id.upload_item -> {
            true
        }

        R.id.delete_item -> {
            createDeleteDialog()
            true
        }

        R.id.check_item -> {

            with(binding) {
                val (titleText, todoText) = editModeTitleEditLayout.editText?.text to editModeTodoEditLayout.editText?.text

                if (titleText.isNullOrEmpty())  editModeTitleEditLayout.error = getString(R.string.please_enter_the_text)

                else if (todoText.isNullOrEmpty())  editModeTodoEditLayout.error = getString(R.string.please_enter_the_text)

                else {
                    TodoModel(
                        model.id,
                        model.date,
                        category,
                        importanceId,
                        titleText.toString(),
                        todoText.toString(),
                        editModeDifficultEdit.text.toString()
                    ).let { viewModel.updateData(it) }
                }
            }
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
                    Log.d(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Positive")
                    viewModel.deleteData(model.id)
                }
                override fun onNegativeClick() {
                    Log.d(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Negative")
                }
            }).show()
    }

    override fun observeData() {
        viewModel.jobState.observe(this) { jobState ->

            when (jobState) {
                JobState.ERROR -> {
                    Toast.makeText(this, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()

                    finish()
                }
                JobState.SUCCESS -> {
                    Toast.makeText(this, "작업 성공.", Toast.LENGTH_SHORT).show()

                    finish()
                }
                else -> {
                    Toast.makeText(this, "작업 진행이 되지않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}