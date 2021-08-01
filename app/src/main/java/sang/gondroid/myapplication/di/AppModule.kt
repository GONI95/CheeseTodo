package sang.gondroid.myapplication.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sang.gondroid.myapplication.presentation.home.HomeViewModel
import sang.gondroid.myapplication.presentation.my.MyViewModel
import sang.gondroid.myapplication.presentation.review.ReviewViewModel

val appModule = module {
    single<CoroutineDispatcher>(named("main")) { Dispatchers.Main }
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }

    viewModel { HomeViewModel() }
    viewModel { MyViewModel() }
    viewModel { ReviewViewModel() }
}