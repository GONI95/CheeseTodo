package sang.gondroid.cheesetodo.domain.model

data class CommentModel(
    override val id: Long?,
    val userEmail : String,
    val userName : String,
    val userPhoto : String,
    val userRank : String,
    val userScore : Long,
    val comment : String,
    val date : Long
) : BaseModel(id)