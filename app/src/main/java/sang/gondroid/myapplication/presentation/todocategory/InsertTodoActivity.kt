package sang.gondroid.myapplication.presentation.todocategory

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.ActivityInsertTodoBinding
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.presentation.base.BaseActivity
import sang.gondroid.myapplication.util.Constants
import sang.gondroid.myapplication.util.TodoCategory
import kotlin.properties.Delegates

class InsertTodoActivity : BaseActivity<InsertTodoViewModel, ActivityInsertTodoBinding>(),
    AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private val THIS_NAME = this::class.simpleName
    private lateinit var category : TodoCategory
    private var importanceId by Delegates.notNull<Int>()

    override val viewModel: InsertTodoViewModel by viewModel()

    override fun getViewBinding(): ActivityInsertTodoBinding
        = ActivityInsertTodoBinding.inflate(layoutInflater)

    override fun initViews() {
        with(binding) {
            ArrayAdapter.createFromResource(this@InsertTodoActivity,
                R.array.importance_array,
                R.layout.support_simple_spinner_dropdown_item).also {
                it.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                importanceSpinner.adapter = it
            }

            importanceSpinner.onItemSelectedListener = this@InsertTodoActivity
            categoryRadioGroup.setOnCheckedChangeListener(this@InsertTodoActivity)
            insertButton.setOnClickListener(this@InsertTodoActivity)

            titleEditLayout.editText?.addTextChangedListener {
                if (!it.toString().isEmpty()) titleEditLayout.error = null
            }
            todoEditLayout.editText?.addTextChangedListener {
                if (!it.toString().isEmpty()) todoEditLayout.error = null
            }
        }
    }

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

            else -> TodoCategory.ERROR
        }
    }

    override fun onClick(v: View?) {
        with(binding) {
            when {
                titleEdit.text.isNullOrEmpty() ->
                    titleEditLayout.error = getString(R.string.please_enter_the_text)

                todoEdit.text.isNullOrEmpty() ->
                    todoEditLayout.error = getString(R.string.please_enter_the_text)

                else -> {
                    val todoModel = when(v?.id) {
                        R.id.insertButton -> TodoModel(
                            null, System.currentTimeMillis(), category, importanceId, titleEdit.text.toString(), todoEdit.text.toString(), null
                        )
                        else -> null
                    }

                    todoModel?.let {  viewModel.insertData(it) }
                }
            }
        }
    }

    override fun observeData() {

    }
}