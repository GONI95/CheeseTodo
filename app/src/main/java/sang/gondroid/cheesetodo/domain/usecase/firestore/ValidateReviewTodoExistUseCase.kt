package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.JobState

class ValidateReviewTodoExistUseCase(
    private val reviewTodoRepository : ReviewTodoRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Firestore에 동일한 ReviewTodo가 있는지 확인 작업 요청
     */
    suspend operator fun invoke(model: TodoModel): JobState = withContext(ioDispatcher) {
        return@withContext reviewTodoRepository.validateReviewTodoExist(model)
    }
}