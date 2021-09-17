package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.util.JobState

class GetCommentsUseCase(private val reviewTodoRepository: ReviewTodoRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Firestore에 ReviewTodo의 Comment 컬렉션에 Get 작업 요청
     */
    suspend operator fun invoke(modelId: Long): JobState = withContext(ioDispatcher) {
        return@withContext reviewTodoRepository.getComments(modelId)
    }
}