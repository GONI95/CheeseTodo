package sang.gondroid.cheesetodo.domain.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.db.FireStoreMemberDTO
import sang.gondroid.cheesetodo.domain.model.FireStoreMemberModel

class MapperToFireStoreMemberModel : Mapper<FireStoreMemberDTO, FireStoreMemberModel> {

    override fun map(input: FireStoreMemberDTO): FireStoreMemberModel {
        return FireStoreMemberModel(
            memberName = input.memberName,
            memberEmail = input.memberEmail,
            memberPhoto = input.memberPhoto,
            memberTodoCount = input.memberTodoCount,
            memberScore = input.memberScore,
            memberRank = input.memberRank
        )
    }
}