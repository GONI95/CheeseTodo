package sang.gondroid.cheesetodo.data.repository

import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.data.firebase.HandleFireStore
import sang.gondroid.cheesetodo.domain.mapper.*
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.JobState
import sang.gondroid.cheesetodo.util.LogUtil

class ReviewTodoRepositoryImpl(
    private val handleFireStore: HandleFireStore,
    private val toReviewTodoMapper: MapperReviewTodoDTO,
    private val toReviewTodoModelMapper: MapperToReviewTodoModel,
    private val toCommentDTO: MapperToCommentDTO,
    private val toCommentModel: MapperToCommentModel,
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

    override suspend fun getComments(modelId: Long): JobState = withContext(ioDispatcher) {
        val result = handleFireStore.getComments(modelId)

        return@withContext when(result) {
            is JobState.True.Result<*> -> {
                JobState.True.Result((result.data as List<*>).map { toCommentModel.map(it as CommentDTO) })
            }
            else -> result
        }
    }

    override suspend fun insertComment(model: CommentModel, modelId: Long): JobState = withContext(ioDispatcher) {
        return@withContext handleFireStore.insertComment(toCommentDTO.map(model), modelId)
    }
}