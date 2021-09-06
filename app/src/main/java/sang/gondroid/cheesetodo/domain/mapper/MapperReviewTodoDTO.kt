package sang.gondroid.cheesetodo.domain.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel

class MapperReviewTodoDTO(
    private val ioDispatcher: CoroutineDispatcher
) : Mapper<ReviewTodoModel, ReviewTodoDTO> {

    override suspend fun map(input: ReviewTodoModel): ReviewTodoDTO = withContext(ioDispatcher) {
        return@withContext ReviewTodoDTO(
            modelId = input.modelId,
            userEmail = input.userEmail,
            userName = input.userName,
            userPhoto = input.userPhoto,
            category = input.category,
            passOrNot = input.passOrNot,
            participating = input.participating,
            passCount = input.passCount,
            date = input.date,
            title = input.title,
            todo = input.todo,
            difficult = input.difficult,
            comments = null
        )
    }
}