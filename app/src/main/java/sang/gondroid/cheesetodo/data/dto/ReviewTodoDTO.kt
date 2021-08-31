package sang.gondroid.cheesetodo.data.dto

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.util.TodoCategory
import sang.gondroid.cheesetodo.util.toTodoCategory

data class ReviewTodoDTO(
    val modelId: Long = 0,
    val userEmail: String = "",
    val userName: String = "",
    val userPhoto: String = "",
    val category: TodoCategory = TodoCategory.ALL,
    val passOrNot: Boolean = false,
    val date: Long = 0,
    val title: String = "",
    val todo: String = "",
    val difficult: String = "",
    val comments: ArrayList<HashMap<String, CommentModel>>? = ArrayList()
)