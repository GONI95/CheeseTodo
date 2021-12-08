package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil

class DeleteCheckedMemberUseCase(private val reviewTodoRepository: ReviewTodoRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Firestore에 ReviewTodo의 checked_members 컬렉션에 Delete 작업 요청
     */
    suspend operator fun invoke(model: ReviewTodoModel) : JobState = withContext(ioDispatcher) {
        LogUtil.d(Constants.TAG, "DeleteCheckedMemberUseCase deleteCheckedMember()")
        return@withContext reviewTodoRepository.deleteCheckedMember(model)
    }
}