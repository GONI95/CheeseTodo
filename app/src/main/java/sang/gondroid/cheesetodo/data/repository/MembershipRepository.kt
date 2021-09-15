package sang.gondroid.cheesetodo.data.repository

import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.JobState

interface MembershipRepository {
    suspend fun getCurrentMembership(): JobState
}