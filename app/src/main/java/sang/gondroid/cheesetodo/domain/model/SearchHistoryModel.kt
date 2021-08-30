package sang.gondroid.cheesetodo.domain.model

import com.google.gson.annotations.SerializedName

data class SearchHistoryModel(
    @SerializedName("searchHistoryId")
    override val id: Long?,
    val timeSet:String,
    val value: String
    ) : BaseModel(id)