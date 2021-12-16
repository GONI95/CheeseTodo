package sang.gondroid.cheesetodo.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import sang.gondroid.cheesetodo.data.entity.TodoEntity

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_table")
    suspend fun getAll() : List<TodoEntity>

    @Query("SELECT * FROM todo_table WHERE id=:id")
    suspend fun getItem(id : Long) : TodoEntity

    @Query("SELECT * FROM todo_table WHERE category=:category")
    suspend fun getList_Category(category : String) : List<TodoEntity>

    @Insert
    suspend fun insert(planEntity: TodoEntity): Long

    @Query("DELETE FROM todo_table WHERE id=:id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM todo_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(planEntity: TodoEntity)
}