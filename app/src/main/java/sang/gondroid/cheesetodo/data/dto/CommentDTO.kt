package sang.gondroid.cheesetodo.data.dto

import sang.gondroid.cheesetodo.domain.model.BaseModel

data class CommentDTO(
    val userEmail : String = "",
    val userName : String = "",
    val userPhoto : String = "",
    val userRank : String = "",
    val userScore : Long = 0,
    val comment : String = "",
    val date : Long = 0
)
