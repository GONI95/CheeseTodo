package sang.gondroid.cheesetodo.domain.usecase.firestore

import sang.gondroid.cheesetodo.data.repository.ReviewTodoRepository
import sang.gondroid.cheesetodo.domain.model.CommentModel
import sang.gondroid.cheesetodo.util.JobState

class InsertCommentUseCase(private val reviewTodoRepository : ReviewTodoRepository) {
        /**
         * Firestore에 comments의 Insert 작업 요청
         */
        suspend operator fun invoke(commentModel: CommentModel, modelId: Long): JobState {
            return reviewTodoRepository.insertComment(commentModel, modelId)
        }
}