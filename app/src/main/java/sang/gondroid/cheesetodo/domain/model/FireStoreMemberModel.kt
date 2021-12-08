package sang.gondroid.cheesetodo.domain.model

import android.net.Uri

data class FireStoreMemberModel (
    val memberEmail : String,
    val memberName : String,
    val memberPhoto : String,
    val memberTodoCount : Number,
    val memberRank : String,
    val memberScore : Number,
)