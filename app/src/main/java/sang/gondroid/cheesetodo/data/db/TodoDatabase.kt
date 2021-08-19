package sang.gondroid.cheesetodo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import sang.gondroid.cheesetodo.data.entity.TodoEntity

@Database(
    entities = [TodoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "TodoDatabase.db"
    }

    abstract fun todoDao() : TodoDao
}