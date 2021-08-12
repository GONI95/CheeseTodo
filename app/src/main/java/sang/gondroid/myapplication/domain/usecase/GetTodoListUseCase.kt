package sang.gondroid.myapplication.domain.usecase

import sang.gondroid.myapplication.data.repository.TodoRepository
import sang.gondroid.myapplication.domain.model.TodoModel

class GetTodoListUseCase (
    private val todoRepository: TodoRepository
    ) {

    /**
     * Todo의 category별 select 작업 요청
     */
    suspend operator fun invoke(category: String): List<TodoModel> {
        return todoRepository.getTodoList_Category(category).map {
            TodoModel(
                id = it.id,
                date = it.date,
                category = it.category,
                importanceId = it.importanceId,
                title = it.title,
                todo = it.todo,
                difficult = it.difficult
            )
        }
    }

    /**
     * Todo의 select 작업 요청
     */
    suspend operator fun invoke(): List<TodoModel> {
        return todoRepository.getTodoList().map {
            TodoModel(
                id = it.id,
                date = it.date,
                category = it.category,
                importanceId = it.importanceId,
                title = it.title,
                todo = it.todo,
                difficult = it.difficult
            )
        }
    }
}