package sang.gondroid.cheesetodo.data.repository

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.data.firebase.HandleFireStore
import sang.gondroid.cheesetodo.domain.mapper.*
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.JobState

class ReviewTodoRepositoryImpl(
    private val handleFireStore: HandleFireStore,
    private val toReviewTodoMapper: MapperReviewTodoDTO,
    private val toReviewTodoModelMapper: MapperToReviewTodoModel,
    private val toCommentDTO: MapperToCommentDTO,
    private val ioDispatcher: CoroutineDispatcher
    ) : ReviewTodoRepository {

    override suspend fun insertReviewTodo(model: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        handleFireStore.insertReviewTodo( toReviewTodoMapper.map(model) )
    }

    override suspend fun validateReviewTodoExist(model: TodoModel): JobState = withContext(ioDispatcher) {
        handleFireStore.validateReviewTodoExist(model)
    }

    override suspend fun getReviewTodo() : JobState = withContext(ioDispatcher) {
        val result = handleFireStore.getReviewTodo()

        return@withContext when(result) {
            is JobState.True.Result<*> -> {
                JobState.True.Result((result.data as List<*>).map { toReviewTodoModelMapper.map(it as ReviewTodoDTO) })
            }
            else -> result
        }
    }

    override suspend fun getComments(model: ReviewTodoModel) : Observable<List<CommentDTO>> = withContext(ioDispatcher) {
        return@withContext handleFireStore.getComments(model)
    }

    override suspend fun insertCheckedUser(model: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        with(handleFireStore) {
            when(val result = insertCheckedUser(model)) {
                is JobState.True -> {
                    return@withContext if (updateMembership(model) == JobState.True)
                        JobState.True
                    else
                        JobState.False
                }
                else -> {
                    return@withContext result
                }
            }
        }
    }

    override suspend fun getCheckedCurrentUser(reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handleFireStore.getCheckedCurrentUser(reviewTodoModel)
    }

    override suspend fun deleteCheckedUser(reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handleFireStore.deleteCheckedUser(reviewTodoModel)
    }

    override suspend fun insertComment(commentModel: CommentModel, reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handleFireStore.insertComment(toCommentDTO.map(commentModel), reviewTodoModel)
    }
}