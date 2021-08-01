package sang.gondroid.myapplication.di

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sang.gondroid.myapplication.data.db.TodoDao
import sang.gondroid.myapplication.data.db.TodoDatabase
import sang.gondroid.myapplication.data.repository.TodoRepository
import sang.gondroid.myapplication.data.repository.TodoRepositoryImpl
import sang.gondroid.myapplication.presentation.home.HomeViewModel
import sang.gondroid.myapplication.presentation.my.MyViewModel
import sang.gondroid.myapplication.presentation.review.ReviewViewModel
import sang.gondroid.myapplication.presentation.todocategory.TodoCategoryViewModel
import sang.gondroid.myapplication.util.TodoCategory

val appModule = module {

    /**
     * CoroutineDispatcher
     */
    single<CoroutineDispatcher>(named("main")) { Dispatchers.Main }
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }

    /**
     * ViewModel
     */
    viewModel { HomeViewModel() }
    viewModel { MyViewModel() }
    viewModel { ReviewViewModel() }

    viewModel { (todoCategory : TodoCategory) -> TodoCategoryViewModel(todoCategory) }

    /**
     * Repository
     */
    single<TodoRepository> { TodoRepositoryImpl() }

    /**
     * Database
     */
    single<TodoDatabase> { provideDB(androidApplication()) }
    single<TodoDao> { provideTodoDao(get()) }
}

private fun provideDB(context : Context) : TodoDatabase =
    Room.databaseBuilder(context, TodoDatabase::class.java, TodoDatabase.DB_NAME).build()

private fun provideTodoDao(database: TodoDatabase) = database.todoDao()