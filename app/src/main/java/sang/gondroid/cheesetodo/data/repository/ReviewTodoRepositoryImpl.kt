package sang.gondroid.cheesetodo.data.repository

import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
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
    private val toCommentModel: MapperToCommentModel,
    private val ioDispatcher: CoroutineDispatcher
    ) : ReviewTodoRepository {

    override suspend fun insertReviewTodo(model: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        with(handlerFireStore) {
            when(val result = insertReviewTodo( toReviewTodoMapper.map(model))) {
                is JobState.True -> {
                    return@withContext updateMemberReviewTodoCount(model)
                }
                else -> {
                    return@withContext result
                }
            }
        }
    }

    override suspend fun validateReviewTodoExist(todoModel: TodoModel): JobState = withContext(ioDispatcher) {
        handlerFireStore.validateReviewTodoExist(todoModel)
    }

    override suspend fun getReviewTodo() : Observable<JobState.True.Result<List<ReviewTodoModel>>> = withContext(ioDispatcher) {
        return@withContext handlerFireStore.getReviewTodo().map { jobState ->
            JobState.True.Result(jobState.data?.map { reviewTodoDTO ->
                toReviewTodoModelMapper.map(reviewTodoDTO)
            })
        }
    }

    override suspend fun getComments(reviewTodoModel: ReviewTodoModel) : Observable<JobState.True.Result<List<CommentModel>>> = withContext(ioDispatcher) {
        return@withContext handlerFireStore.getComments(reviewTodoModel).map { jobState ->
            JobState.True.Result(jobState.data?.map { commentTodo ->
                toCommentModel.map(commentTodo)
            })
        }
    }

    override suspend fun insertCheckedMember(reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        with(handlerFireStore) {
            when(val result = insertCheckedMember(reviewTodoModel)) {
                is JobState.True -> {
                    return@withContext if (updateMemberScore(reviewTodoModel) == JobState.True)
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

    override suspend fun getCheckedCurrentMember(reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handlerFireStore.getCheckedCurrentMember(reviewTodoModel)
    }

    override suspend fun deleteCheckedMember(reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handlerFireStore.deleteCheckedMember(reviewTodoModel)
    }

    override suspend fun getCheckedMemberCount(reviewTodoModel: ReviewTodoModel): Observable<JobState.True.Result<Int>> = withContext(ioDispatcher) {
        return@withContext handlerFireStore.getCheckedMemberCount(reviewTodoModel)
    }

    override suspend fun insertComment(commentModel: CommentModel, reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        return@withContext handlerFireStore.insertComment(toCommentDTO.map(commentModel), reviewTodoModel)
    }
}