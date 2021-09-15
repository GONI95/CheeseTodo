package sang.gondroid.cheesetodo.data.db

import android.net.Uri

data class FireStoreMembershipDTO (
    val userEmail : String = "",
    val userName : String = "",
    val userPhoto : String = "",
    val userTodoCount : Long = 0,
    val userRank : String = "",
    val userScore : Long = 0,
    )