package sang.gondroid.cheesetodo.domain.model

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import sang.gondroid.cheesetodo.util.Constants
import java.io.Serializable

/**
 * DiffUtil : RecyclerView의 성능을 개선할 수 있게 해주는 유틸리티 클래스, 기존의 List와 교체할,
 * List를 비교해 실질적으로 업데이트가 필요한 Item들을 필터링함
 */
abstract class BaseModel (
     open val id : Long?
) : Serializable {
     init {
        Log.d(Constants.TAG, "BaseModel called")
     }

    companion object {
        /**
         * areItemsTheSame() : oldItem, newItem이 동일한 Item인지 체크, Item의 고유식별자를 이용하면 됨
         */
        val DIFF_CALLBACK : DiffUtil.ItemCallback<BaseModel> = object : DiffUtil.ItemCallback<BaseModel>() {
            override fun areItemsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
                Log.d(Constants.TAG, "BaseModel areItemsTheSame() called : ${oldItem.id} ${newItem.id}")

                return oldItem.id == newItem.id
            }

            /**
             * areContesTheSame() : oldItem, newItem이 동일한 내용물을 가졌는지 체크, areItemsTheSame()가 true이면 호출됨
             */
            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
                Log.d(Constants.TAG, "BaseModel areContentsTheSame() called : ${oldItem.id} ${newItem.id}")

                return oldItem == newItem
            }
        }
    }
 }