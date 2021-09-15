package sang.gondroid.cheesetodo.domain.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.data.db.FireStoreMembershipDTO
import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.domain.model.FireStoreMembershipModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel

class MapperToFireStoreMembershipModel (
    private val ioDispatcher: CoroutineDispatcher
) : Mapper<FireStoreMembershipDTO, FireStoreMembershipModel> {

    override suspend fun map(input: FireStoreMembershipDTO): FireStoreMembershipModel = withContext(ioDispatcher) {
        return@withContext FireStoreMembershipModel(
            userName = input.userName,
            userEmail = input.userEmail,
            userPhoto = input.userPhoto,
            userTodoCount = input.userTodoCount,
            userScore = input.userScore,
            userRank = input.userRank
        )
    }
}