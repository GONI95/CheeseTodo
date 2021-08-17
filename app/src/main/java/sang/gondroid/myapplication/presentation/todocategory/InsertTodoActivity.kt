package sang.gondroid.myapplication.presentation.todocategory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.ActivityInsertTodoBinding
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.presentation.base.BaseActivity
import sang.gondroid.myapplication.presentation.home.HomeFragment
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.JobState
import sang.gondroid.myapplication.util.TodoCategory
import java.util.*
import kotlin.properties.Delegates


class InsertTodoActivity : BaseActivity<InsertTodoViewModel, ActivityInsertTodoBinding>() {

    private val THIS_NAME = this::class.simpleName
    private val difficultText : String = ""
    private var category : TodoCategory = TodoCategory.ANDROID
    private var importanceId by Delegates.notNull<Int>()

    override val viewModel: InsertTodoViewModel by viewModel<InsertTodoViewModel>()

    override fun getViewBinding(): ActivityInsertTodoBinding
        = ActivityInsertTodoBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.insertViewModel = viewModel
        binding.handler = this
    }

    override fun initViews() {
        super.initViews()

        with(binding) {
            /**
             * 1. createFromResource() : 정적 메소드를 호출하여 ArrayAdapter 객체를 생성(context, array, layout id)
             *
             * 2. setDropDownViewResource() : Adapter가 Spinner 선택 항목의 목록을 표시하는데 사용할 Layout 지정
             */
            ArrayAdapter.createFromResource(
                this@InsertTodoActivity,
                R.array.importance_array,
                R.layout.support_simple_spinner_dropdown_item
            ).also {
                it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                importanceSpinner.adapter = it
            }
        }
    }

    fun titleAfterTextChanged(editable : Editable?) : Boolean {
        return !editable.toString().isEmpty()
    }
    fun todoAfterTextChanged(editable : Editable?, view: TextInputLayout) {
        if (!editable.toString().isEmpty()) view.error = null
    }

    /**
     * initViews()에서 등록한 각각의 Listener Interface를 구현한 메서드
     */
    fun onImportanceItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        Log.d(Constants.TAG,
            "$THIS_NAME onItemSelected : ${parent.getItemAtPosition(position)} $view, $position, $id")

        importanceId = position
    }

    fun onCategoryCheckChanged(group: RadioGroup?, checkedId: Int) {
        Log.d(Constants.TAG, "$THIS_NAME onCustomCheckChanged")

        category = when (checkedId) {
            R.id.androidRadioButton -> TodoCategory.ANDROID

            R.id.lauguageRadioButton -> TodoCategory.LANGUAGE

            R.id.dbRadioButton -> TodoCategory.DB

            R.id.otherRadioButton -> TodoCategory.OTHER

            else -> TodoCategory.ALL
        }
    }

    fun onBtnClick(titleEditLayout : TextInputLayout, todoEditLayout : TextInputLayout) {
        val titleText = titleEditLayout.editText?.text.toString()
        val todoText = todoEditLayout.editText?.text.toString()

        if (titleEditLayout.editText?.text.isNullOrEmpty())
            titleEditLayout.error = getString(R.string.please_enter_the_text)

        else if(todoEditLayout.editText?.text.isNullOrEmpty())
            todoEditLayout.error = getString(R.string.please_enter_the_text)

        else {
            Log.d(Constants.TAG, "$THIS_NAME $category, $importanceId")

            val todoModel = TodoModel(null, System.currentTimeMillis(), category, importanceId, titleText, todoText, difficultText)

            todoModel.let {  viewModel.insertData(it) }
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    override fun observeData() {
        viewModel.jobState.observe(this) { jobState ->
            val resultIntent = Intent(this, HomeFragment::class.java)

            when(jobState) {
                JobState.ERROR -> {
                    resultIntent.putExtra("jobState", JobState.ERROR)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                JobState.SUCCESS -> {
                    resultIntent.putExtra("jobState", JobState.SUCCESS)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }
}