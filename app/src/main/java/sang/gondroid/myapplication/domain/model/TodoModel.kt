package sang.gondroid.myapplication.domain.model

import sang.gondroid.myapplication.data.entity.TodoEntity
import sang.gondroid.myapplication.util.TodoCategory

data class TodoModel(
    override val id: Long?,
    val date: Long,
    val category: TodoCategory,
    val importanceId: Int,
    val title: String,
    val todo: String,
    val difficult: String?
) : BaseModel(id) {

    fun toEntity() = TodoEntity(
        id, date, category, importanceId, title, todo, difficult
    )
}