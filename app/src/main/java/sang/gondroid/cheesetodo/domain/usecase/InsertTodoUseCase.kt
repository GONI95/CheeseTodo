package sang.gondroid.cheesetodo.domain.usecase

import android.util.Log
import sang.gondroid.cheesetodo.data.repository.TodoRepository
import sang.gondroid.cheesetodo.domain.model.TodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil

@Suppress("UNCHECKED_CAST")
class InsertTodoUseCase(
    private val todoRepository: TodoRepository
) {
    private val THIS_NAME = this::class.simpleName

    /**
     * domain, data Layer의 중재자 역할을 하는 todoRepository 객체를 이용해 Insert 작업 메서드 호출
     */
    suspend operator fun invoke(todoModel: TodoModel): Long {
        val todoEntity = todoModel.toEntity().also {
            LogUtil.d(Constants.TAG, "$THIS_NAME todoEntity : $it")
        }
        return todoRepository.insertTodoItem(todoEntity)
    }
}