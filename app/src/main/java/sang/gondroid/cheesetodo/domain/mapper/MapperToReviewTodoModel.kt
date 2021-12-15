package sang.gondroid.cheesetodo.domain.mapper

import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel

class MapperToReviewTodoModel : Mapper<ReviewTodoDTO, ReviewTodoModel> {

    override fun map(input: ReviewTodoDTO): ReviewTodoModel {
        return ReviewTodoModel(
            id = null,
            modelId = input.modelId,
            memberEmail = input.memberEmail,
            memberName = input.memberName,
            memberPhoto = input.memberPhoto,
            category = input.category,
            date = input.date,
            title = input.title,
            todo = input.todo,
            difficult = input.difficult
        )
    }
}