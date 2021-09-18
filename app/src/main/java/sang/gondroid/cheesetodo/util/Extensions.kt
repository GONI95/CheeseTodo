package sang.gondroid.cheesetodo.util

import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.common.util.Base64Utils
import sang.gondroid.cheesetodo.CheeseTodoApplication
import sang.gondroid.cheesetodo.R
import sang.gondroid.cheesetodo.domain.model.BaseModel
import sang.gondroid.cheesetodo.domain.model.ReviewTodoModel
import sang.gondroid.cheesetodo.widget.base.BaseAdapter
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher

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


fun String.toTodoCategory() : TodoCategory = when(this) {
    TodoCategory.ANDROID.name -> TodoCategory.ANDROID
    TodoCategory.LANGUAGE.name -> TodoCategory.LANGUAGE
    TodoCategory.DB.name -> TodoCategory.DB
    TodoCategory.OTHER.name -> TodoCategory.OTHER
    else -> TodoCategory.ALL
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

fun encrypt(input: String, key: PrivateKey): String {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val encrypt = cipher.doFinal(input.toByteArray())
    return  Base64Utils.encode(encrypt)
}

fun decrypt(input: String, key: PublicKey): String {
    val byteEncrypt: ByteArray = Base64Utils.decode(input)
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.DECRYPT_MODE, key)
    val decrypt = cipher.doFinal(byteEncrypt)
    return String(decrypt)
}