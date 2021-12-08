package sang.gondroid.cheesetodo.domain.model

import android.net.Uri
import sang.gondroid.cheesetodo.util.TodoCategory

data class ReviewTodoModel(
    override val id: Long?,
    val modelId: Long,
    val memberEmail: String,
    val memberName: String,
    val memberPhoto: String,
    val category: TodoCategory,
    val date: Long,
    val title: String,
    val todo: String,
    val difficult: String
) : BaseModel(id)