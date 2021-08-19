package sang.gondroid.cheesetodo.domain.usecase

import sang.gondroid.cheesetodo.data.repository.TodoRepository

class DeleteTodoUseCase(
    private val todoRepository: TodoRepository
) {

    suspend operator fun invoke(id: Long) {
        return todoRepository.deleteTodoItem(id)
    }
}