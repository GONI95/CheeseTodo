package sang.gondroid.cheesetodo.data.db

import android.net.Uri

data class FireStoreMemberDTO (
    val memberEmail : String = "",
    val memberName : String = "",
    val memberPhoto : String = "",
    val memberTodoCount : Long = 0,
    val memberRank : String = "",
    val memberScore : Long = 0,
    )