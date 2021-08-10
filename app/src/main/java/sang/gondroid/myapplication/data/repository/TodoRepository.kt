package sang.gondroid.myapplication.data.repository

import sang.gondroid.myapplication.data.entity.TodoEntity

interface TodoRepository {
    suspend fun getTodoList() : List<TodoEntity>

    suspend fun getTodoItem(id: Long): TodoEntity?

    suspend fun getTodoItem_Category(category: String): List<TodoEntity>

    suspend fun insertTodoItem(planEntity: TodoEntity): Long

    suspend fun updateTodoItem(planEntity: TodoEntity)

    suspend fun deleteTodoItem(id: Long)

    suspend fun deleteAll()
}