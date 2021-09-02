package sang.gondroid.cheesetodo.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.data.firebase.HandleFireStore
import sang.gondroid.cheesetodo.domain.mapper.*
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.JobState

class ReviewTodoRepositoryImpl(
    private val handleFireStore: HandleFireStore,
    private val reviewTodoMapper: MapperReviewTodoDTO,
    private val toReviewTodoModelMapper: MapperToReviewTodoModel,
    private val ioDispatcher: CoroutineDispatcher
    ) : ReviewTodoRepository {
    override suspend fun insertReviewTodo(model: ReviewTodoModel): JobState = withContext(ioDispatcher) {
        handleFireStore.insertReviewTodo( reviewTodoMapper.map(model) )
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
}