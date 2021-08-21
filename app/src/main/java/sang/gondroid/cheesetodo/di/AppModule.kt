package sang.gondroid.cheesetodo.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sang.gondroid.cheesetodo.data.db.TodoDao
import sang.gondroid.cheesetodo.data.db.TodoDatabase
import sang.gondroid.cheesetodo.data.firebase.CheckFirebaseAuth
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.data.repository.TodoRepository
import sang.gondroid.cheesetodo.data.repository.TodoRepositoryImpl
import sang.gondroid.cheesetodo.domain.usecase.DeleteTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.GetTodoListUseCase
import sang.gondroid.cheesetodo.domain.usecase.InsertTodoUseCase
import sang.gondroid.cheesetodo.domain.usecase.UpdateTodoUseCase
import sang.gondroid.cheesetodo.presentation.home.HomeViewModel
import sang.gondroid.cheesetodo.presentation.my.MyViewModel
import sang.gondroid.cheesetodo.presentation.review.ReviewViewModel
import sang.gondroid.cheesetodo.presentation.todocategory.DetailTodoViewModel
import sang.gondroid.cheesetodo.presentation.todocategory.InsertTodoViewModel
import sang.gondroid.cheesetodo.presentation.todocategory.TodoCategoryViewModel
import sang.gondroid.cheesetodo.util.TodoCategory
import sang.gondroid.cheesetodo.widget.custom.CustomDialog

val appModule = module {

    /**
     * CoroutineDispatcher
     */
    single<MainCoroutineDispatcher>(named("main")) { Dispatchers.Main }
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }

    /**
     * ViewModel
     */
    viewModel { HomeViewModel(get()) }
    viewModel { MyViewModel(get<AppPreferenceManager>(), get(named("io")), get()) }
    viewModel { ReviewViewModel() }
    viewModel { InsertTodoViewModel(get<InsertTodoUseCase>(), get(named("io"))) }
    viewModel { DetailTodoViewModel(get(), get()) }

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
    factory { UpdateTodoUseCase(get()) }
    factory { DeleteTodoUseCase(get()) }

    /**
     * FirebaseAuth
     */
    single<FirebaseAuth> { provideFirebaseAuth() }

    /**
     * AppPreferenceManager
     */
    single<AppPreferenceManager> { AppPreferenceManager(androidApplication()) }

    /**
     * CheckFirebaseAuth : Preference, Firebase Current User를 이용해 Token 저장 유무, User 정보를 확인하는 Class
     */
    single { CheckFirebaseAuth(get(), get()) }
}

private fun provideDB(context : Context) : TodoDatabase =
    Room.databaseBuilder(context, TodoDatabase::class.java, TodoDatabase.DB_NAME).build()

private fun provideTodoDao(database: TodoDatabase) = database.todoDao()

private fun provideFirebaseAuth() : FirebaseAuth = FirebaseAuth.getInstance()