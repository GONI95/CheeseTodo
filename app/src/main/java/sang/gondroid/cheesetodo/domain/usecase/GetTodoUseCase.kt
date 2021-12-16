package sang.gondroid.cheesetodo.domain.usecase

import sang.gondroid.cheesetodo.data.entity.TodoEntity
import sang.gondroid.cheesetodo.data.repository.TodoRepository
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil

class GetTodoUseCase(
    private val todoRepository: TodoRepository
) {
    private val THIS_NAME = this::class.simpleName

    suspend operator fun invoke(id : Long): TodoModel {
        LogUtil.d(Constants.TAG, "$THIS_NAME invoke() called")

        return todoRepository.getTodoItem(id).toModel()
    }
}