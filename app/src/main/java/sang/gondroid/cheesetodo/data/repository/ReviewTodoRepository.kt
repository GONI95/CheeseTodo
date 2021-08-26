package sang.gondroid.cheesetodo.data.repository

import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.JobState

interface ReviewTodoRepository {
    suspend fun insertReviewTodo(model: ReviewTodoModel): JobState
    suspend fun validateReviewTodoExist(model: TodoModel): JobState
}