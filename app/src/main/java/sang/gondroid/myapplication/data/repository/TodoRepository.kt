package sang.gondroid.myapplication.data.repository

import sang.gondroid.myapplication.data.entity.TodoEntity

interface TodoRepository {
    suspend fun getPlanList() : List<TodoEntity>

    suspend fun getPlanItem(id: Long): TodoEntity?

    suspend fun getPlanItem_Category(category: String): List<TodoEntity>

    suspend fun insertPlanItem(planEntity: TodoEntity): Long

    suspend fun updatePlanItem(planEntity: TodoEntity)

    suspend fun deletePlanItem(id: Long)

    suspend fun deleteAll()
}