package sang.gondroid.cheesetodo.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import sang.gondroid.cheesetodo.util.TodoCategory

@Entity(tableName = "todo_table")
data class TodoEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Long?,
    @ColumnInfo(name = "date")
    val date : Long,
    @ColumnInfo(name = "category")
    val category: TodoCategory,
    @ColumnInfo(name = "importanceId")
    val importanceId : Int,
    @ColumnInfo(name = "title")
    val title : String,
    @ColumnInfo(name = "todo")
    val todo : String,
    @ColumnInfo(name = "difficult")
    val difficult : String?
)