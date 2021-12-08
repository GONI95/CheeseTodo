package sang.gondroid.cheesetodo.data.dto

import sang.gondroid.cheesetodo.domain.model.BaseModel

data class CommentDTO(
    val memberEmail : String = "",
    val memberName : String = "",
    val memberPhoto : String = "",
    val memberRank : String = "",
    val memberScore : Long = 0,
    val comment : String = "",
    val date : Long = 0
)
