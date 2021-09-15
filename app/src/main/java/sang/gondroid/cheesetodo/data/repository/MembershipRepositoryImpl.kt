package sang.gondroid.cheesetodo.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.db.FireStoreMembershipDTO
import sang.gondroid.cheesetodo.data.firebase.HandleFireStore
import sang.gondroid.cheesetodo.domain.mapper.MapperToFireStoreMembershipModel
import sang.gondroid.cheesetodo.util.JobState

class MembershipRepositoryImpl (
    private val handleFireStore: HandleFireStore,
    private val mapperToFireStoreMembershipModel: MapperToFireStoreMembershipModel,
    private val ioDispatcher: CoroutineDispatcher
) : MembershipRepository {

    override suspend fun getCurrentMembership(): JobState = withContext(ioDispatcher) {
        val result = handleFireStore.getCurrentMembership()

        return@withContext when (result) {
            is JobState.Success.Registered<*> -> {
                JobState.Success.Registered(mapperToFireStoreMembershipModel.map(result.data as FireStoreMembershipDTO))
            }
            else -> result
        }
    }
}