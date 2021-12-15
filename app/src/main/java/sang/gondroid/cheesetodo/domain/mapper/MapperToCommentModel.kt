package sang.gondroid.cheesetodo.domain.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.domain.model.CommentModel

class MapperToCommentModel : Mapper<CommentDTO, CommentModel> {
    override fun map(input: CommentDTO): CommentModel {
        return CommentModel(
            id = null,
            memberEmail = input.memberEmail,
            memberName = input.memberName,
            memberPhoto = input.memberPhoto,
            memberRank = input.memberRank,
            memberScore = input.memberScore,
            date = input.date,
            comment = input.comment
        )
    }
}