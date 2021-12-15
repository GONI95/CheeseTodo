package sang.gondroid.cheesetodo.data.repository

import io.reactivex.rxjava3.core.Observable
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.JobState

interface ReviewTodoRepository {
    suspend fun insertReviewTodo(reviewTodoModel: ReviewTodoModel): JobState
    suspend fun validateReviewTodoExist(todoModel: TodoModel): JobState
    suspend fun getReviewTodo() : Observable<JobState.True.Result<List<ReviewTodoModel>>>
    suspend fun insertComment(commentModel: CommentModel, reviewTodoModel: ReviewTodoModel): JobState
    suspend fun getComments(reviewTodoModel: ReviewTodoModel): Observable<JobState.True.Result<List<CommentModel>>>
    suspend fun insertCheckedMember(reviewTodoModel: ReviewTodoModel) : JobState
    suspend fun getCheckedCurrentMember(reviewTodoModel: ReviewTodoModel) : JobState
    suspend fun deleteCheckedMember(reviewTodoModel: ReviewTodoModel) : JobState
    suspend fun getCheckedMemberCount(reviewTodoModel: ReviewTodoModel): Observable<JobState.True.Result<Int>>
}