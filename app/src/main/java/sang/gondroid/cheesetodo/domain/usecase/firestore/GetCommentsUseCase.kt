package sang.gondroid.cheesetodo.domain.usecase.firestore

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.dto.CommentDTO
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil

class GetCommentsUseCase(private val reviewTodoRepository: ReviewTodoRepository, private val ioDispatcher: CoroutineDispatcher) {

    /**
     * Firestore에 ReviewTodo의 Comment 컬렉션에 Get 작업 요청
     */
    suspend operator fun invoke(model: ReviewTodoModel) : Observable<List<CommentDTO>> = withContext(ioDispatcher) {
        LogUtil.d(Constants.TAG, "GetCommentsUseCase getComments()")
        return@withContext reviewTodoRepository.getComments(model)
    }
}