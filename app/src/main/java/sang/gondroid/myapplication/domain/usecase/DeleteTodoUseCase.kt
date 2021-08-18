package sang.gondroid.myapplication.domain.usecase

import sang.gondroid.myapplication.data.repository.TodoRepository

class DeleteTodoUseCase(
    private val todoRepository: TodoRepository
) {

    suspend operator fun invoke(id: Long) {
        return todoRepository.deleteTodoItem(id)
    }
}