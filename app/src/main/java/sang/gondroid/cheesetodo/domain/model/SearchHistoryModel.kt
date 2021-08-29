package sang.gondroid.cheesetodo.domain.model

data class SearchHistoryModel(
    override val id: Long?,
    val timeSet:String,
    val value: String
    ) : BaseModel(id)