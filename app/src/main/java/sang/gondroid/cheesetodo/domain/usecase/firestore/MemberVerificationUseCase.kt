package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.MemberRepository
import sang.gondroid.cheesetodo.util.JobState

class MemberVerificationUseCase (private val memberRepository: MemberRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Firestore에 Users에 Get 작업 요청
     */
    suspend operator fun invoke(): JobState = withContext(ioDispatcher) {
        return@withContext memberRepository.memberVerification()
    }
}