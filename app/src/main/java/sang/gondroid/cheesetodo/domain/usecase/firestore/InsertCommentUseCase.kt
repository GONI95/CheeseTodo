package sang.gondroid.cheesetodo.domain.usecase.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.util.JobState

class InsertCommentUseCase(private val reviewTodoRepository : ReviewTodoRepository, private val ioDispatcher: CoroutineDispatcher) {
        /**
         * Firestore에 comments의 Insert 작업 요청
         */
        suspend operator fun invoke(commentModel: CommentModel, reviewTodoModel: ReviewTodoModel): JobState = withContext(ioDispatcher) {
            return@withContext reviewTodoRepository.insertComment(commentModel, reviewTodoModel)
        }
}