package sang.gondroid.cheesetodo.data.repository

import sang.gondroid.cheesetodo.util.JobState

interface MemberRepository {
    suspend fun memberVerification(): JobState
    suspend fun deleteAccount() : JobState
}