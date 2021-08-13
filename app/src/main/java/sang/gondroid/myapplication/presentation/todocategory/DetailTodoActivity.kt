package sang.gondroid.myapplication.presentation.todocategory

import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
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
    AdapterView.OnItemSelectedListener,
    RadioGroup.OnCheckedChangeListener,
    Toolbar.OnMenuItemClickListener {
        private val THIS_NAME = this::class.simpleName
        private lateinit var model: TodoModel
        private lateinit var category : TodoCategory
        private var importance by Delegates.notNull<String>()
        private var importanceId by Delegates.notNull<Int>()

        override val viewModel: DetailTodoViewModel by viewModel()

        override fun getViewBinding(): ActivityDetailTodoBinding
                = ActivityDetailTodoBinding.inflate(layoutInflater)

        override fun initViews() {
            super.initViews()

            binding.detatilViewModel = viewModel

            val bundle = this.intent.getBundleExtra("bundle")
            bundle?.let { model = bundle.getSerializable("TodoItemData") as TodoModel }

            with(binding) {
                ArrayAdapter.createFromResource(this@DetailTodoActivity,
                    R.array.importance_array,
                    R.layout.support_simple_spinner_dropdown_item).also {
                    it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                    editModeImportanceSpinner.adapter = it
                }

                readModeToolbar.setOnMenuItemClickListener(this@DetailTodoActivity)
                editModeToolbar.setOnMenuItemClickListener(this@DetailTodoActivity)
                editModeCategoryRadioGroup.setOnCheckedChangeListener(this@DetailTodoActivity)
                editModeImportanceSpinner.onItemSelectedListener = this@DetailTodoActivity

                readModeTitleTextView.text = model.title
                readModeCategoryTextView.text = model.category.name
                readModeImportanceTextView.text = model.importanceId.toImportanceString()
                readModeTodoTextView.text =  model.todo
                readModeDifficultTextView.text =  model.difficult
            }
        }

        override fun observeData() {
            viewModel.jobState.observe(this) {  jobState ->

                when(jobState) {
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


        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            importance = parent.getItemAtPosition(position).toString()
            importanceId = position
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d(Constants.TAG, "$THIS_NAME onNothingSelected : $parent")
        }

        override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
            category = when (checkedId) {
                R.id.androidRadioButton -> {
                    TodoCategory.ANDROID
                }
                R.id.lauguageRadioButton -> {
                    TodoCategory.LANGUAGE
                }
                R.id.dbRadioButton -> {
                    TodoCategory.DB
                }
                R.id.otherRadioButton -> {
                    TodoCategory.OTHER
                }
                else -> TodoCategory.ANDROID
            }
        }

        override fun onMenuItemClick(item: MenuItem): Boolean
                = when(item.itemId) {
            R.id.edit_item -> {

                with(binding) {
                    editModeCategoryRadioGroup.check(editModeCategoryRadioGroup.getChildAt(model.category.ordinal - 1).id)
                    editModeImportanceSpinner.setSelection(model.importanceId)

                    editModeTitleEdit.setText(binding.readModeTitleTextView.text)
                    editModeTodoEdit.setText(binding.readModeTodoTextView.text)
                    editModeDifficultEdit.setText(binding.readModeDifficultTextView.text)

                    readModeConstraintLayout.visibility = View.GONE
                    editModeConstraintLayout.visibility = View.VISIBLE
                }

                true
            }

            R.id.upload_item -> {

                true
            }

            R.id.delete_item -> {
                CustomDialog(this,
                    getString(R.string.delete_todo_dialog_title),
                    getString(R.string.delete_todo_dialog_description),
                    object : CustomDialogClickListener {
                        override fun onPositiveClick() {
                            Log.d(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Positive")

                            viewModel.deleteData(model.id)
                        }
                        override fun onNegativeClick() {
                            Log.d(Constants.TAG, "$THIS_NAME onMenuItemClick CustomDialog Negative")
                        }
                    }).show()

                true
            }

            R.id.check_item -> {

                with(binding) {
                    val todoModel = TodoModel(
                        model.id,
                        model.date,
                        category,
                        importanceId,
                        editModeTitleEdit.text.toString(),
                        editModeTodoEdit.text.toString(),
                        editModeDifficultEdit.text.toString()
                    )

                    todoModel.let { viewModel.updateData(todoModel) }
                }

                true
            }

            else -> {
                false
            }
        }
}