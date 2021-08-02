package sang.gondroid.myapplication.domain.model

import android.annotation.SuppressLint
import android.graphics.ColorSpace
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import sang.gondroid.myapplication.util.Constants
import java.io.Serializable

abstract class BaseModel (
     open val id : Long?
) : Serializable {
     init {
        Log.d(Constants.TAG, "Model $THIS_NAME called")
    }

    companion object {
        private val THIS_NAME = this::class.simpleName

        /**
         * areItemsTheSame() : 두 객체가 동일한 항목을 나타내는지 확인
         * areContesTheSame() : 두 항목의 데이터가 같은지 확인
         */
        val DIFF_CALLBACK : DiffUtil.ItemCallback<BaseModel> = object : DiffUtil.ItemCallback<BaseModel>() {
            override fun areItemsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
                Log.d(Constants.TAG, "${THIS_NAME} areItemsTheSame() called")

                return oldItem.id == newItem.id
                // id가 같아도 여러화면에서 쓰이면서 다른 데이터를 가져올 수 있어 타입까지 확인
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
                Log.d(Constants.TAG, "${THIS_NAME} areContentsTheSame() called")

                return oldItem === newItem
                //같은 객체인지 비교하는 같은 내용이 들어간지 판단하기위해,
            }

        }
    }
 }