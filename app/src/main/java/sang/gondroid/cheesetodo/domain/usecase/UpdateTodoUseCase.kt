package sang.gondroid.cheesetodo.domain.usecase

import android.util.Log
import sang.gondroid.cheesetodo.data.repository.TodoRepository
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.Constants


class UpdateTodoUseCase(
    private val todoRepository: TodoRepository
) {
    private val THIS_NAME = this::class.simpleName

    suspend operator fun invoke(todoModel: TodoModel) {
        val todoEntity = todoModel.toEntity().also {
            Log.d(Constants.TAG, "$THIS_NAME todoEntity : $it")
        }

        return todoRepository.updateTodoItem(todoEntity)
    }
}