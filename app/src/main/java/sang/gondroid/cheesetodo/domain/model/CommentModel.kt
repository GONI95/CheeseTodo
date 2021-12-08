package sang.gondroid.cheesetodo.domain.model

data class CommentModel(
    override val id: Long?,
    val memberEmail : String,
    val memberName : String,
    val memberPhoto : String,
    val memberRank : String,
    val memberScore : Long,
    val comment : String,
    val date : Long
) : BaseModel(id)