package sang.gondroid.cheesetodo.domain.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.domain.model.CommentModel

class MapperToCommentModel(
    private val ioDispatcher: CoroutineDispatcher
) : Mapper<CommentDTO, CommentModel> {
    override suspend fun map(input: CommentDTO): CommentModel = withContext(ioDispatcher) {
        return@withContext CommentModel(
            id = null,
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