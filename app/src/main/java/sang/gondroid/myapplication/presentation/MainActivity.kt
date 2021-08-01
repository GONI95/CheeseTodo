package sang.gondroid.myapplication.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import sang.gondroid.myapplication.R
import sang.gondroid.myapplication.databinding.ActivityMainBinding
import sang.gondroid.myapplication.presentation.home.HomeFragment
import sang.gondroid.myapplication.presentation.my.MyFragment
import sang.gondroid.myapplication.presentation.review.ReviewFragment
import sang.gondroid.myapplication.util.Constants


class MainActivity : AppCompatActivity() {

    private val THIS_NAME = this::class.simpleName

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


    }
}