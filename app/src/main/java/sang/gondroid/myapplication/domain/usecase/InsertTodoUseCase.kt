package sang.gondroid.myapplication.domain.usecase

import android.util.Log
import sang.gondroid.myapplication.data.repository.TodoRepository
import sang.gondroid.myapplication.domain.model.TodoModel
import sang.gondroid.myapplication.util.Constants

@Suppress("UNCHECKED_CAST")
class InsertTodoUseCase(
    private val todoRepository: TodoRepository
) {
    private val THIS_NAME = this::class.simpleName

    suspend operator fun invoke(todoModel: TodoModel): Long {
        val todoEntity = todoModel.toEntity().also {
            Log.d(Constants.TAG, "$THIS_NAME todoEntity : $it")
        }
        return todoRepository.insertTodoItem(todoEntity)
    }
}