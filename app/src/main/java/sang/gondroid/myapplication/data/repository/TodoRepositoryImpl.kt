package sang.gondroid.myapplication.data.repository

import sang.gondroid.myapplication.data.entity.TodoEntity

class TodoRepositoryImpl() : TodoRepository {
    override suspend fun getPlanList(): List<TodoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlanItem(id: Long): TodoEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun getPlanItem_Category(category: String): List<TodoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPlanItem(planEntity: TodoEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlanItem(planEntity: TodoEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlanItem(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}