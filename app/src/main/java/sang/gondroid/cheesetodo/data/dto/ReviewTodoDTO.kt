package sang.gondroid.cheesetodo.data.dto

import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.util.TodoCategory

data class ReviewTodoDTO(
    val modelId : Long?,
    val userEmail: String?,
    val userName: String?,
    val userPhoto: String?,
    val category: TodoCategory,
    val passOrNot: Boolean,
    val date: Long,
    val title: String,
    val todo: String,
    val difficult: String?,
    val comments: ArrayList<HashMap<String, CommentModel>>
)