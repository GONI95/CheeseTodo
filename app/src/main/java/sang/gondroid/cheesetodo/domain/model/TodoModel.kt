package sang.gondroid.cheesetodo.domain.model

import sang.gondroid.cheesetodo.data.entity.TodoEntity
import sang.gondroid.cheesetodo.util.TodoCategory

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