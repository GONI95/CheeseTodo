package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.MemberRepository
import sang.gondroid.cheesetodo.util.JobState

class MemberVerificationUseCase (private val memberRepository: MemberRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Gon : Firestore member 콜렉션에서 사용자 정보를 가져오거나, Firestore member 콜렉션에 사용자 정보를 추가하는 memberVerification() 호출
     *       [update - 21.12.4]
     */
    suspend operator fun invoke(): JobState = withContext(ioDispatcher) {
        return@withContext memberRepository.memberVerification()
    }
}