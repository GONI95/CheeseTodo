package sang.gondroid.myapplication.presentation.todocategory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.android.viewmodel.ext.android.viewModel
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.ActivityInsertTodoBinding
import sang.gondroid.myapplication.presentation.base.BaseActivity

class InsertTodoActivity : BaseActivity<InsertTodoViewModel, ActivityInsertTodoBinding>() {

    override val viewModel: InsertTodoViewModel by viewModel()

    override fun getViewBinding(): ActivityInsertTodoBinding
        = ActivityInsertTodoBinding.inflate(layoutInflater)

    override fun observeData() {

    }
}