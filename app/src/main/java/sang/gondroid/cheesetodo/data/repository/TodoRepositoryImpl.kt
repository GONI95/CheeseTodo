package sang.gondroid.cheesetodo.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.db.TodoDao
import sang.gondroid.cheesetodo.data.entity.TodoEntity

/**
 * 데이터 조회, 조작하는 작업을 하기위한 DB로의 접근을 담당하는 TodoDao를 이용해 Interface 구현체를 호출
 */
class TodoRepositoryImpl(
    private val todoDao : TodoDao,
    private val ioDispatcher: CoroutineDispatcher
) : TodoRepository {
    override suspend fun getTodoList(): List<TodoEntity>
        = withContext(ioDispatcher) { todoDao.getAll() }

    override suspend fun getTodoItem(id: Long): TodoEntity
        = withContext(ioDispatcher) { todoDao.getItem(id) }


    override suspend fun getTodoList_Category(category: String): List<TodoEntity>
        = withContext(ioDispatcher) { todoDao.getList_Category(category) }

    override suspend fun insertTodoItem(todoEntity: TodoEntity): Long
        = withContext(ioDispatcher) { todoDao.insert(todoEntity) }

    override suspend fun updateTodoItem(todoEntity: TodoEntity)
        = withContext(ioDispatcher) { todoDao.update(todoEntity) }

    override suspend fun deleteTodoItem(id: Long)
        = withContext(ioDispatcher) { todoDao.delete(id) }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}