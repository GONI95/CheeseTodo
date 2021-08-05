package sang.gondroid.myapplication.domain.model

import android.graphics.ColorSpace
import android.os.Parcel
import android.os.Parcelable
import sang.gondroid.myapplication.data.entity.TodoEntity
import sang.gondroid.myapplication.util.TodoCategory
import java.io.Serializable

data class TodoModel(
    override var id: Long,
    val date: Long,
    val dateString: String,
    val category: TodoCategory,
    val importanceId: Int,
    val title: String,
    val todo: String,
    val difficult: String
) : BaseModel(id) {

    fun toEntity() = TodoEntity(
        id, date, category, importanceId, title, todo, difficult
    )
}