package sang.gondroid.cheesetodo.domain.mapper

import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel

class MapperToReviewTodoModel(
    private val ioDispatcher: CoroutineDispatcher
) : Mapper<ReviewTodoDTO, ReviewTodoModel> {

    override suspend fun map(input: ReviewTodoDTO): ReviewTodoModel = withContext(ioDispatcher) {
        return@withContext ReviewTodoModel(
            id = null,
            modelId = input.modelId,
            userEmail = input.userEmail,
            userName = input.userName,
            userPhoto = input.userPhoto,
            category = input.category,
            passOrNot = input.passOrNot,
            date = input.date,
            title = input.title,
            todo = input.todo,
            difficult = input.difficult,
            comments = input.comments
        )
    }
}