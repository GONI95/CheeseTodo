package sang.gondroid.cheesetodo.data.repository

import io.reactivex.rxjava3.core.Observable
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.JobState

interface ReviewTodoRepository {
    suspend fun insertReviewTodo(model: ReviewTodoModel): JobState
    suspend fun validateReviewTodoExist(model: TodoModel): JobState
    suspend fun getReviewTodo() : JobState
    suspend fun insertComment(commentModel: CommentModel, reviewTodoModel: ReviewTodoModel): JobState
    suspend fun getComments(model: ReviewTodoModel): Observable<List<CommentDTO>>
    suspend fun insertCheckedUser(reviewTodoModel: ReviewTodoModel) : JobState
    suspend fun getCheckedCurrentUser(reviewTodoModel: ReviewTodoModel) : JobState
    suspend fun deleteCheckedUser(reviewTodoModel: ReviewTodoModel) : JobState
    suspend fun getCheckedUserCount(model: ReviewTodoModel): Observable<Int>
}