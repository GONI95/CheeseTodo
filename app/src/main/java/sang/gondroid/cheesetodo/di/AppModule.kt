package sang.gondroid.cheesetodo.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sang.gondroid.cheesetodo.BuildConfig
import sang.gondroid.cheesetodo.data.db.TodoDao
import sang.gondroid.cheesetodo.data.db.TodoDatabase
import sang.gondroid.cheesetodo.data.firebase.HandlerFireStore
import sang.gondroid.cheesetodo.data.firebase.HandlerFirebaseAuth
import sang.gondroid.cheesetodo.data.preference.AppPreferenceManager
import sang.gondroid.cheesetodo.data.preference.LiveSharedPreferences
import sang.gondroid.cheesetodo.data.repository.*
import sang.gondroid.cheesetodo.domain.mapper.*
import sang.gondroid.cheesetodo.domain.usecase.*
import sang.gondroid.cheesetodo.domain.usecase.firestore.*
import sang.gondroid.cheesetodo.presentation.my.MyViewModel
import sang.gondroid.cheesetodo.presentation.review.DetailReviewViewModel
import sang.gondroid.cheesetodo.presentation.review.ReviewViewModel
import sang.gondroid.cheesetodo.presentation.todocategory.DetailTodoViewModel
import sang.gondroid.cheesetodo.presentation.todocategory.InsertTodoViewModel
import sang.gondroid.cheesetodo.presentation.todocategory.TodoCategoryViewModel
import sang.gondroid.cheesetodo.util.NetworkConnection
import sang.gondroid.cheesetodo.util.TodoCategory

val appModule = module {

    /**
     * CoroutineDispatcher
     */
    single<MainCoroutineDispatcher>(named("main")) { Dispatchers.Main }
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }

    /**
     * ViewModel
     */
    viewModel { MyViewModel(get<AppPreferenceManager>(), get(), get(), get(), get(named("io"))) }
    viewModel { ReviewViewModel(get(), get(), get(named("io"))) }
    viewModel { DetailReviewViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(named("io"))) }
    viewModel { InsertTodoViewModel(get<InsertTodoUseCase>(), get(named("io"))) }
    viewModel { DetailTodoViewModel(it[0], get(), get(), get(), get(), get(), get(), get(named("io"))) }

    viewModel { (todoCategory : TodoCategory) -> TodoCategoryViewModel(todoCategory, get()) }

    /**
     * Repository : Domain??? Data Layer ????????? ??????????????? ???????????????.
     */
    single<TodoRepository> { TodoRepositoryImpl(get<TodoDao>(), get(named("io"))) }
    single<ReviewTodoRepository> { ReviewTodoRepositoryImpl(get(), get(), get(), get(), get(), get(named("io"))) }
    single<MemberRepository> { MemberRepositoryImpl(get(), get(), get(named("io"))) }

    /**
     * Mapper : Model <-> DTO
     */
    single { MapperReviewTodoDTO() }
    single { MapperToReviewTodoModel() }
    single { MapperToCommentModel() }
    single { MapperToCommentDTO() }
    single { MapperToFireStoreMemberModel() }

    /**
     * Database
     */
    single<TodoDatabase> { provideDB(androidApplication()) }
    single<TodoDao> { provideTodoDao(get()) }

    /**
     * UseCase : Repository??? ?????? ???????????? ????????? ???????????? ??????, Interface ???????????? ?????? ?????????.
     */
    factory { InsertTodoUseCase(get()) }
    factory { GetTodoListUseCase(get()) }
    factory { GetTodoUseCase(get()) }
    factory { UpdateTodoUseCase(get()) }
    factory { DeleteTodoUseCase(get()) }

    factory { InsertReviewTodoUseCase(get(), get(named("io"))) }
    factory { ValidateReviewTodoExistUseCase(get(), get(named("io"))) }
    factory { GetReviewTodoUseCase(get(), get(named("io"))) }

    factory { InsertCommentUseCase(get(), get(named("io"))) }
    factory { MemberVerificationUseCase(get(), get(named("io"))) }
    factory { DeleteAccountUseCase(get(), get(named("io"))) }
    factory { GetCommentsUseCase(get(), get(named("io"))) }
    factory { InsertCheckedMemberUseCase(get(), get(named("io"))) }
    factory { GetCheckedCurrentMemberUseCase(get(), get(named("io"))) }
    factory { DeleteCheckedMemberUseCase(get(), get(named("io"))) }
    factory { GetCheckedMemberCountUseCase(get(), get(named("io"))) }

    /**
     * FirebaseAuth
     */
    single<FirebaseAuth> { provideFirebaseAuth() }

    /**
     * FireStore
     */
    single<FirebaseFirestore> { Firebase.firestore }

    /**
     * AppPreferenceManager
     */
    single<AppPreferenceManager> { AppPreferenceManager(getPreferences(androidApplication())) }

    /**
     * CheckFirebaseAuth : Preference, Firebase Current User??? ????????? Token ?????? ??????, User ????????? ???????????? Class
     */
    single { HandlerFirebaseAuth(get(), get(), get(named("io"))) }

    /**
     * HandleFireStore
     */
    single { HandlerFireStore(get(), get(), get(named("io")), androidApplication()) }

    /**
     * LiveSharedPreferences
     */
    single { LiveSharedPreferences(getPreferences(androidApplication())) }

    /**
     * Network State
     */
    single { NetworkConnection(androidApplication()) }
}

private fun provideDB(context : Context) : TodoDatabase =
    Room.databaseBuilder(context, TodoDatabase::class.java, TodoDatabase.DB_NAME).build()

private fun provideTodoDao(database: TodoDatabase) = database.todoDao()

private fun provideFirebaseAuth() : FirebaseAuth = FirebaseAuth.getInstance()

private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(BuildConfig.PREFERENCES_NAME, Context.MODE_PRIVATE)
}