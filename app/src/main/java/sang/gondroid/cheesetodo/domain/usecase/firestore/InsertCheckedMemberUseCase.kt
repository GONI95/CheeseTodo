package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.JobState

class InsertCheckedMemberUseCase(private val reviewTodoRepository: ReviewTodoRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Firestore에 ReviewTodo의 CheckedMember 컬렉션에 Insert 작업 요청
     */
    suspend operator fun invoke(reviewTodoModel: ReviewTodoModel) : JobState = withContext(ioDispatcher) {
        return@withContext reviewTodoRepository.insertCheckedMember(reviewTodoModel)
    }
}