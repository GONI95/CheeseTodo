package sang.gondroid.myapplication.presentation.todocategory

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import androidx.core.widget.addTextChangedListener
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

class InsertTodoActivity : BaseActivity<InsertTodoViewModel, ActivityInsertTodoBinding>(),
    AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private val THIS_NAME = this::class.simpleName
    private lateinit var category : TodoCategory
    private var importanceId by Delegates.notNull<Int>()

    override val viewModel: InsertTodoViewModel by viewModel<InsertTodoViewModel>()

    override fun getViewBinding(): ActivityInsertTodoBinding
        = ActivityInsertTodoBinding.inflate(layoutInflater)

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
                R.layout.support_simple_spinner_dropdown_item).also {
                it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                importanceSpinner.adapter = it
            }

            /**
             * Spinner, RadioGroup, Button에 대한 사용자 이벤트 처리하기 위해서 View 객체에 리스너 등록
             */
            importanceSpinner.onItemSelectedListener = this@InsertTodoActivity
            categoryRadioGroup.setOnCheckedChangeListener(this@InsertTodoActivity)
            insertButton.setOnClickListener(this@InsertTodoActivity)

            binding.androidRadioButton.isChecked = true

            /**
             * EditText에 입력되는 이벤트를 처리하기 위해서 무명 클래스를 생성해 Listener로 사용
             */
            titleEditLayout.editText?.addTextChangedListener {
                if (!it.toString().isEmpty()) titleEditLayout.error = null
            }
            todoEditLayout.editText?.addTextChangedListener {
                if (!it.toString().isEmpty()) todoEditLayout.error = null
            }
        }
    }

    /**
     * initViews()에서 등록한 각각의 Listener Interface를 구현한 메서드
     */
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        Log.d(Constants.TAG,
            "$THIS_NAME onItemSelected : ${parent.getItemAtPosition(position)} $view, $position, $id")

        importanceId = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d(Constants.TAG, "$THIS_NAME onNothingSelected : $parent")
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        category = when (checkedId) {
            R.id.androidRadioButton -> TodoCategory.ANDROID

            R.id.lauguageRadioButton -> TodoCategory.LANGUAGE

            R.id.dbRadioButton -> TodoCategory.DB

            R.id.otherRadioButton -> TodoCategory.OTHER

            else -> TodoCategory.ALL
        }
    }

    override fun onClick(v: View?) {
        with(binding) {
            if (titleEdit.text.isNullOrEmpty())
                titleEditLayout.error = getString(R.string.please_enter_the_text)

            else if(todoEdit.text.isNullOrEmpty())
                todoEditLayout.error = getString(R.string.please_enter_the_text)

            else {
                val todoModel = when(v?.id) {
                    R.id.insertButton -> {
                        TodoModel(
                            null,
                            System.currentTimeMillis(),
                            category,
                            importanceId,
                            titleEdit.text.toString(),
                            todoEdit.text.toString(),
                            ""
                        )
                    }

                    else -> null
                }

                todoModel?.let {  viewModel.insertData(it) }
            }
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    override fun observeData() {
        viewModel.jobState.observe(this) {  jobState ->
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