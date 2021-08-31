package sang.gondroid.cheesetodo.domain.usecase.firestore

import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.JobState

class GetReviewTodoUseCase(
    private val reviewTodoRepository : ReviewTodoRepository
) {
    /**
     * Firestore에 ReviewTodo의 Get 작업 요청
     */
    suspend operator fun invoke(): JobState {
        return reviewTodoRepository.getReviewTodo()
    }
}