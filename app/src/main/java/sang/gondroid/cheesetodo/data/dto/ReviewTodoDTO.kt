package sang.gondroid.cheesetodo.data.dto

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.util.TodoCategory

data class ReviewTodoDTO(
    val modelId: Long = 0,
    val memberEmail: String = "",
    val memberName: String = "",
    val memberPhoto: String = "",
    val category: TodoCategory = TodoCategory.ALL,
    val date: Long = 0,
    val title: String = "",
    val todo: String = "",
    val difficult: String = ""
)