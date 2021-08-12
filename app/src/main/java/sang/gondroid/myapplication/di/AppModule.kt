package sang.gondroid.myapplication.di

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sang.gondroid.myapplication.data.db.TodoDao
import sang.gondroid.myapplication.data.db.TodoDatabase
import sang.gondroid.myapplication.data.repository.TodoRepository
import sang.gondroid.myapplication.data.repository.TodoRepositoryImpl
import sang.gondroid.myapplication.domain.usecase.GetTodoListUseCase
import sang.gondroid.myapplication.domain.usecase.InsertTodoUseCase
import sang.gondroid.myapplication.presentation.home.HomeViewModel
import sang.gondroid.myapplication.presentation.my.MyViewModel
import sang.gondroid.myapplication.presentation.review.ReviewViewModel
import sang.gondroid.myapplication.presentation.todocategory.InsertTodoViewModel
import sang.gondroid.myapplication.presentation.todocategory.TodoCategoryViewModel
import sang.gondroid.myapplication.util.TodoCategory

val appModule = module {

    /**
     * CoroutineDispatcher
     */
    single<MainCoroutineDispatcher>(named("main")) { Dispatchers.Main }
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }

    /**
     * ViewModel
     */
    viewModel { HomeViewModel() }
    viewModel { MyViewModel() }
    viewModel { ReviewViewModel() }
    viewModel { InsertTodoViewModel(get<InsertTodoUseCase>(), get(named("io"))) }

    viewModel { (todoCategory : TodoCategory) -> TodoCategoryViewModel(todoCategory, get()) }

    /**
     * Repository : Domain과 Data Layer 사이를 중재해주는 객체입니다.
     */
    single<TodoRepository> { TodoRepositoryImpl(get<TodoDao>(), get(named("io"))) }

    /**
     * Database
     */
    single<TodoDatabase> { provideDB(androidApplication()) }
    single<TodoDao> { provideTodoDao(get()) }

    /**
     * UseCase : Repository를 받아 비즈니스 로직을 처리하는 부분, Interface 구현체로 보면 됩니다.
     */
    factory { InsertTodoUseCase(get()) }
    factory { GetTodoListUseCase(get()) }
}

private fun provideDB(context : Context) : TodoDatabase =
    Room.databaseBuilder(context, TodoDatabase::class.java, TodoDatabase.DB_NAME).build()

private fun provideTodoDao(database: TodoDatabase) = database.todoDao()