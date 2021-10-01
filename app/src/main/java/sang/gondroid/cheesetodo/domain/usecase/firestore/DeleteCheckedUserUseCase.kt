package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil

class DeleteCheckedUserUseCase(private val reviewTodoRepository: ReviewTodoRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Firestore에 ReviewTodo의 checked_users 컬렉션에 Delete 작업 요청
     */
    suspend operator fun invoke(model: ReviewTodoModel) : JobState = withContext(ioDispatcher) {
        LogUtil.d(Constants.TAG, "DeleteCheckedUserUseCase deleteCheckedUser()")
        return@withContext reviewTodoRepository.deleteCheckedUser(model)
    }
}