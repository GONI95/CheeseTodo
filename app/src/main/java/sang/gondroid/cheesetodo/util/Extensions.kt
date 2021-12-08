package sang.gondroid.cheesetodo.util

import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sang.gondroid.cheesetodo.CheeseTodoApplication
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 1. 일반적인 Generic Function Body에서 타입 T는 런타임에는 Type erasure 때문에 접근이 불가능
 * 하지만 reified 타입 파라미터와 함께 inline 함수를 만들면, 런타임에 타입 T에 접근이 가능하며,
 * "변수 is T"를 통해 "변수"가 T의 인스턴스인지 검사할 수 있음
 *
 * 2. Type erasure : Generic은 컴파일 시간에 엄격한 Type 검사를 제공하지만,
 * Generic을 구현하기 위해 Java 컴파일러는 Type erasure를 적용합니다
 */
inline fun <reified T> List<BaseModel>.checkType() =
    all { it is T }


fun Long.toDateFormat(): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())
    return dateFormat.format(date)
}

fun Int.toImportanceString(): String = when(this) {
    0 -> CheeseTodoApplication.appContext!!.resources.getStringArray(R.array.importance_array).get(0)
    1 -> CheeseTodoApplication.appContext!!.resources.getStringArray(R.array.importance_array).get(1)
    2 -> CheeseTodoApplication.appContext!!.resources.getStringArray(R.array.importance_array).get(2)
    3 -> CheeseTodoApplication.appContext!!.resources.getStringArray(R.array.importance_array).get(3)
    else -> " "
}


fun Long.toUserRank() : String? {
    val value = this
    with(CheeseTodoApplication.appContext) {
        LogUtil.i(Constants.TAG, "toUserRank() : $value")
        return when (value) {
            in 0..100 -> this?.getString(MemberRank.Level1.memberRankStringId)
            in 100..200 -> this?.getString(MemberRank.Level2.memberRankStringId)
            in 200..300 -> this?.getString(MemberRank.Level3.memberRankStringId)
            in 300..400 -> this?.getString(MemberRank.Level4.memberRankStringId)
            in 400..500 -> this?.getString(MemberRank.Level5.memberRankStringId)
            in 500..600 -> this?.getString(MemberRank.Level6.memberRankStringId)
            in 600..700 -> this?.getString(MemberRank.Level7.memberRankStringId)
            in 700..800 -> this?.getString(MemberRank.Level8.memberRankStringId)
            in 800..900 -> this?.getString(MemberRank.Level9.memberRankStringId)
            in 900..1000 -> this?.getString(MemberRank.Level10.memberRankStringId)
            in 1000..1100 -> this?.getString(MemberRank.Level11.memberRankStringId)
            else -> this?.getString(MemberRank.Level12.memberRankStringId)
        }
    }
}

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("selectPosition")
    fun setSelection(view : Spinner, position : Int) {
        view.setSelection(position)
    }

    @JvmStatic
    @BindingAdapter("changedId")
    fun check(view : RadioGroup, id : Int) {
        view.check(view.getChildAt(id).id)
    }

    @JvmStatic
    @BindingAdapter("findImage")
    fun load(imageView : ImageView, todoCategory: TodoCategory) {

        val loadImage = when(todoCategory.ordinal) {
            1 -> {
                R.drawable.ic_android
            }
            2 -> {
                R.drawable.ic_kotlin
            }
            3 -> {
                R.drawable.ic_db
            }
            4 -> {
                R.drawable.ic_etc
            }
            else -> {
                R.drawable.ic_cheese
            }
        }

        Glide.with(imageView.context)
            .load(loadImage)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun load(imageView: ImageView, loadImage: String?) {
        Glide.with(imageView.context)
            .load(loadImage)
            .circleCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("setAdapter")
    fun <M : BaseModel> setAdapter(view : RecyclerView, adapter : BaseAdapter<M>?) {
        view.adapter = adapter
    }
}