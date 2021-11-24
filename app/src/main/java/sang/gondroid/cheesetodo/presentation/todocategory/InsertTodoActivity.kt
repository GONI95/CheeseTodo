package sang.gondroid.cheesetodo.presentation.todocategory

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.databinding.ActivityInsertTodoBinding
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.presentation.base.BaseActivity
import sang.gondroid.cheesetodo.presentation.home.HomeFragment
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil
import sang.gondroid.cheesetodo.util.TodoCategory
import java.util.*
import kotlin.properties.Delegates

class InsertTodoActivity : BaseActivity<InsertTodoViewModel, ActivityInsertTodoBinding>() {

    private val THIS_NAME = this::class.simpleName
    private val difficultText : String = ""
    private var category : TodoCategory = TodoCategory.ANDROID
    private var importanceId by Delegates.notNull<Int>()

    override val viewModel: InsertTodoViewModel by viewModel<InsertTodoViewModel>()

    override fun getDataBinding(): ActivityInsertTodoBinding
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

    /**
     * 1, 2. addtextchangedlistener의 afterTextChanged() 이벤트 핸들러 메서드(리스너 바인딩 방식)
     *
     * 3. OnItemSelectedListener의 onItemSelected() 이벤트 핸들러 메서드(메서드 참조 방식, Spinner)
     *
     * 4. OnCheckedChangeListener의 onCheckedChanged() 이벤트 핸들러 메서드(메서드 참조 방식, RadioButton)
     *
     * 5. OnClickListener onClick() 이벤트 핸들러 메서드(메서드 참조 방식, Button)
     */
    fun titleAfterTextChanged(editable : Editable?) {
        if (!editable.toString().isEmpty()) binding.titleTextInputLayout.error = null
    }

    fun todoAfterTextChanged(editable : Editable?) {
        if (!editable.toString().isEmpty()) binding.todoTextInputLayout.error = null
    }

    fun onImportanceItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        LogUtil.v(Constants.TAG, "$THIS_NAME onImportanceItemSelected() called : ${parent.getItemAtPosition(position)} $view, $position, $id")

        importanceId = position
    }

    fun onCategoryCheckChanged(group: RadioGroup?, checkedId: Int) {
        LogUtil.v(Constants.TAG, "$THIS_NAME onCustomCheckChanged called :  $checkedId")

        category = when (checkedId) {
            R.id.androidRadioButton -> TodoCategory.ANDROID
            R.id.lauguageRadioButton -> TodoCategory.LANGUAGE
            R.id.dbRadioButton -> TodoCategory.DB
            R.id.otherRadioButton -> TodoCategory.OTHER

            else -> TodoCategory.ALL
        }
    }

    fun onBtnClick(view: View?) {
        with(binding) {
            val titleText = titleTextInputLayout.editText?.text
            val todoText = todoTextInputLayout.editText?.text

            if (titleText.isNullOrEmpty())
                titleTextInputLayout.error = getString(R.string.please_enter_the_text)

            else if(todoText.isNullOrEmpty())
                todoTextInputLayout.error = getString(R.string.please_enter_the_text)

            else {
                val todoModel = TodoModel(null, System.currentTimeMillis(), category, importanceId, titleText.toString(), todoText.toString(), difficultText)

                todoModel.let {  viewModel.insertData(it) }
            }
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    override fun observeData() {
        LogUtil.v(Constants.TAG, "$THIS_NAME observeData() called")

        viewModel.jobState.observe(this) { jobState ->
            LogUtil.i(Constants.TAG, "$THIS_NAME observeData() JobState : $jobState")
            val resultIntent = Intent(this, HomeFragment::class.java)

            when(jobState) {
                is JobState.Error -> {
                    resultIntent.putExtra("jobState", jobState)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                is JobState.True -> {
                    resultIntent.putExtra("jobState", jobState)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }
}