package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.JobState

class GetReviewTodoUseCase(
    private val reviewTodoRepository : ReviewTodoRepository, private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Firestore에 ReviewTodo의 Get 작업 요청
     */
    suspend operator fun invoke(): JobState = withContext(ioDispatcher) {
        return@withContext reviewTodoRepository.getReviewTodo()
    }
}