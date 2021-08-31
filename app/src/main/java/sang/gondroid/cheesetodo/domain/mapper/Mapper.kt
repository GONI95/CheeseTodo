package sang.gondroid.cheesetodo.domain.mapper

import sang.gondroid.cheesetodo.data.dto.ReviewTodoDTO
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel

interface Mapper<From, To> {
    suspend fun map(input : From) : To
}
