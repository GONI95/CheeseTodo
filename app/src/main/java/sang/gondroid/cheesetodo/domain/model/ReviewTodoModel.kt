package sang.gondroid.cheesetodo.domain.model

import android.net.Uri
import sang.gondroid.cheesetodo.util.TodoCategory

data class ReviewTodoModel(
    override val id: Long?,
    val modelId: Long,
    val userEmail: String,
    val userName: String,
    val userPhoto: String,
    val category: TodoCategory,
    val passOrNot: Boolean,
    val participating: ArrayList<String>,
    val passCount: Long,
    val date: Long,
    val title: String,
    val todo: String,
    val difficult: String,
    val comments: ArrayList<CommentModel>?
) : BaseModel(id)