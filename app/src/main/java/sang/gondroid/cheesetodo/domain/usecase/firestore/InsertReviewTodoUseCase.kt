package sang.gondroid.cheesetodo.domain.usecase.firestore

import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.JobState

class InsertReviewTodoUseCase(
    private val reviewTodoRepository : ReviewTodoRepository
) {
    /**
     * Firestore에 ReviewTodo의 Insert 작업 요청
     */
    suspend operator fun invoke(reviewTodoModel: ReviewTodoModel): JobState {
        return reviewTodoRepository.insertReviewTodo(reviewTodoModel)
    }
}