package sang.gondroid.cheesetodo.data.preference

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil

/**
 * Gon : 관찰 작업을 위한 LiveData class를 확장하는 LivePreferenece class를 생성 했습니다.
 */
class LivePreference<T> constructor(
    private val updates: Observable<String>,
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: T?
) : MutableLiveData<T>() {

    private val THIS_NAME = this::class.simpleName

    /**
     * Gon : 구독을 해지하기위해 disposable 객체를 선언했습니다.
     *       subscribe()를 호출하면 Disposable이 반환됩니다.
     */
    private var disposable: Disposable? = null

    /**
     * Gon : 관찰 상태에 따라 처리를 할 수 있는 메서드를 재정의 합니다.
     *       관찰자가 존재하는 경우 호출됩니다.
     */
    override fun onActive() {
        super.onActive()

        LogUtil.d(Constants.TAG, "$THIS_NAME onActive() called")

        /**
         * Gon : setValue()를 통해, 값을 업데이트하고 모든 관찰자에게 알릴 수 있습니다.
         *       - 현재 key값에 매칭된 데이터를 가져와 HomeFragment 및 ReviewFragment의 관찰자에게 알립니다 -
         *       - value를 초기화하지 않으면 key값에 매칭된 데이터가 변경된 경우에만 관찰자에게 보내기 때문에 null이 전달됩니다. -
         */
        value = (preferences.all[key] as T) ?: defaultValue
        LogUtil.d(Constants.TAG, "$THIS_NAME onActive() value : $value")

        disposable = updates
            .filter { t -> t == key }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<String>() {
                override fun onComplete() {
                    LogUtil.d(Constants.TAG, "$THIS_NAME onActive() : onComplete")
                }

                override fun onNext(t: String) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME onActive() : onNext - key : $t")
                    postValue((preferences.all[t] as T) ?: defaultValue)
                }

                override fun onError(e: Throwable) {
                    LogUtil.e(Constants.TAG, "$THIS_NAME onActive() : onError - e : $e")
                }
            })
    }

    /**
     * Gon : 관찰 상태에 따라 처리를 할 수 있는 메서드를 재정의 합니다.
     *       관찰 상태가 해제되는 경우, 호출됩니다.
     */
    override fun onInactive() {
        super.onInactive()

        LogUtil.d(Constants.TAG, "$THIS_NAME onInactive() called")

        /**
         * Gon : 관찰자가 존재하기 않기 때문에, 메모리 누수를 방지가히 위해 명시적으로 해제했습니다.
         */
        disposable?.dispose()
    }
}