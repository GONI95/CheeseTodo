package sang.gondroid.cheesetodo.domain.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel

class MapperReviewTodoDTO : Mapper<ReviewTodoModel, ReviewTodoDTO> {

    override fun map(input: ReviewTodoModel): ReviewTodoDTO {
        return ReviewTodoDTO(
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