package sang.gondroid.cheesetodo.domain.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.domain.model.CommentModel

class MapperToCommentDTO (
    private val ioDispatcher: CoroutineDispatcher
) : Mapper<CommentModel, CommentDTO> {
    override suspend fun map(input: CommentModel): CommentDTO = withContext(ioDispatcher) {
        return@withContext CommentDTO(
            userEmail = input.userEmail,
            userName = input.userName,
            userPhoto = input.userPhoto,
            userRank = input.userRank,
            userScore = input.userScore,
            date = input.date,
            comment = input.comment
        )
    }
}