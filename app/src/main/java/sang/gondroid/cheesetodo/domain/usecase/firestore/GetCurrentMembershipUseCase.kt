package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.MembershipRepository
import sang.gondroid.cheesetodo.util.JobState

class GetCurrentMembershipUseCase (private val membershipRepository: MembershipRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Firestore에 Users에 Get 작업 요청
     */
    suspend operator fun invoke(): JobState = withContext(ioDispatcher) {
        return@withContext membershipRepository.getCurrentMembership()
    }
}