package sang.gondroid.cheesetodo.data.repository

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.data.firebase.HandlerFireStore
import sang.gondroid.cheesetodo.domain.mapper.*
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.JobState

class ReviewTodoRepositoryImpl(
    private val handlerFireStore: HandlerFireStore,
    private val toReviewTodoMapper: MapperReviewTodoDTO,
    private val toReviewTodoModelMapper: MapperToReviewTodoModel,
    private val toCommentDTO: MapperToCommentDTO,
    private val ioDispatcher: CoroutineDispatcher
    ) : ReviewTodoRepository {

    override suspend fun insertReviewTodo(model: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        with(handlerFireStore) {
            when(val result = insertReviewTodo( toReviewTodoMapper.map(model))) {
                is JobState.True -> {
                    return@withContext if (updateMemberTodoCount(model) == JobState.True)
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

    override suspend fun validateReviewTodoExist(model: TodoModel): JobState = withContext(ioDispatcher) {
        handlerFireStore.validateReviewTodoExist(model)
    }

    override suspend fun getReviewTodo() : JobState = withContext(ioDispatcher) {
        val result = handlerFireStore.getReviewTodo()

        return@withContext when(result) {
            is JobState.True.Result<*> -> {
                JobState.True.Result((result.data as List<*>).map { toReviewTodoModelMapper.map(it as ReviewTodoDTO) })
            }
            else -> result
        }
    }

    override suspend fun getComments(model: ReviewTodoModel) : Observable<List<CommentDTO>> = withContext(ioDispatcher) {
        return@withContext handlerFireStore.getComments(model)
    }

    override suspend fun insertCheckedMember(model: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        with(handlerFireStore) {
            when(val result = insertCheckedMember(model)) {
                is JobState.True -> {
                    return@withContext if (updateMemberScore(model) == JobState.True)
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

    override suspend fun getCheckedCurrentMember(model: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handlerFireStore.getCheckedCurrentMember(model)
    }

    override suspend fun deleteCheckedMember(model: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handlerFireStore.deleteCheckedMember(model)
    }

    override suspend fun getCheckedMemberCount(model: ReviewTodoModel): Observable<Int> = withContext(ioDispatcher) {
        return@withContext handlerFireStore.getCheckedMemberCount(model)
    }

    override suspend fun insertComment(commentModel: CommentModel, reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handlerFireStore.insertComment(toCommentDTO.map(commentModel), reviewTodoModel)
    }
}