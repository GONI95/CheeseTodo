package sang.gondroid.cheesetodo.domain.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.db.FireStoreMemberDTO
import sang.gondroid.cheesetodo.domain.model.FireStoreMemberModel

class MapperToFireStoreMemberModel (
    private val ioDispatcher: CoroutineDispatcher
) : Mapper<FireStoreMemberDTO, FireStoreMemberModel> {

    override suspend fun map(input: FireStoreMemberDTO): FireStoreMemberModel = withContext(ioDispatcher) {
        return@withContext FireStoreMemberModel(
            memberName = input.memberName,
            memberEmail = input.memberEmail,
            memberPhoto = input.memberPhoto,
            memberTodoCount = input.memberTodoCount,
            memberScore = input.memberScore,
            memberRank = input.memberRank
        )
    }
}