package sang.gondroid.cheesetodo.domain.model

import android.net.Uri

data class FireStoreMemberModel (
    val userEmail : String,
    val userName : String,
    val userPhoto : String,
    val userTodoCount : Number,
    val userRank : String,
    val userScore : Number,
)