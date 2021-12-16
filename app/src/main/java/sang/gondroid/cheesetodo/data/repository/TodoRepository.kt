package sang.gondroid.cheesetodo.data.repository

import sang.gondroid.cheesetodo.data.entity.TodoEntity

interface TodoRepository {
    suspend fun getTodoList() : List<TodoEntity>

    suspend fun getTodoItem(id: Long): TodoEntity

    suspend fun getTodoList_Category(category: String): List<TodoEntity>

    suspend fun insertTodoItem(todoEntity: TodoEntity): Long

    suspend fun updateTodoItem(todoEntity: TodoEntity)

    suspend fun deleteTodoItem(id: Long)

    suspend fun deleteAll()
}