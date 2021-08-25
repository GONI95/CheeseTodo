package sang.gondroid.cheesetodo.domain.model

import android.net.Uri

data class FireStoreMembershipModel (
    val userEmail : String,
    val userName : String,
    val userPhoto : Uri,
    val userTodoCount : Number,
    val userRank : String,
    val userScore : Number,
)