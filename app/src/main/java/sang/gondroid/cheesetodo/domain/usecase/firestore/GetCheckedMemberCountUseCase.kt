package sang.gondroid.cheesetodo.domain.usecase.firestore

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil

class GetCheckedMemberCountUseCase(private val reviewTodoRepository: ReviewTodoRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Firestore에 ReviewTodo의 checked_users 컬렉션에 document 가져오기 작업 요청
     */
    suspend operator fun invoke(model: ReviewTodoModel) : Observable<JobState.True.Result<Int>> = withContext(ioDispatcher) {
        LogUtil.d(Constants.TAG, "GetCheckedUserCountUseCase getCheckedUserCount()")
        return@withContext reviewTodoRepository.getCheckedMemberCount(model)
    }
}