package sang.gondroid.cheesetodo.data.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.db.FireStoreMemberDTO
import sang.gondroid.cheesetodo.data.firebase.HandlerFireStore
import sang.gondroid.cheesetodo.domain.mapper.MapperToFireStoreMemberModel
import sang.gondroid.cheesetodo.util.JobState

class MemberRepositoryImpl (
    private val handlerFireStore: HandlerFireStore,
    private val mapperToFireStoreMemberModel: MapperToFireStoreMemberModel,
    private val ioDispatcher: CoroutineDispatcher
) : MemberRepository {

    override suspend fun memberVerification(): JobState = withContext(ioDispatcher) {
        return@withContext when (val result = handlerFireStore.memberVerification()) {

            is JobState.Success.Registered<*> -> JobState.Success.Registered(mapperToFireStoreMemberModel.map(result.data as FireStoreMemberDTO))

            else -> result
        }
    }

    override suspend fun deleteAccount(): JobState = withContext(ioDispatcher) {
        return@withContext handlerFireStore.deleteAccount()
    }
}