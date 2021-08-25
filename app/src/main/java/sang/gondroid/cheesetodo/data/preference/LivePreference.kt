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
 * 관찰 작업을 위한 LiveData class의 확장 : MutableLiveData를 상속받아 Observer 사용을 Custom
 */
class LivePreference<T> constructor(
    private val updates: Observable<String>,
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: T?
) : MutableLiveData<T>() {

    private val THIS_NAME = this::class.simpleName

    private var disposable: Disposable? = null

    /**
     * 관찰 상태에 따라 처리를 할 수 있는 메서드를 재정의
     * 1. onActive() : 관찰자가 존재하는 경우, 관찰 상태가 해제되면 onInactive() 호출
     * 2. onInactive() : 관찰자가 존재하지 않는 경우, 작업을 마쳤으니 Disposable를 해제
     */
    override fun onActive() {
        super.onActive()

        LogUtil.d(Constants.TAG, "$THIS_NAME onActive() called")

        value = (preferences.all[key] as T) ?: defaultValue
        LogUtil.d(Constants.TAG, "$THIS_NAME getAll() called : $value")

        disposable = updates
            .filter { t -> t == key }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableObserver<String>() {
                override fun onComplete() {
                    LogUtil.d(Constants.TAG, "$THIS_NAME onActive() : onComplete")
                }

                override fun onNext(t: String) {
                    LogUtil.d(Constants.TAG, "$THIS_NAME onActive() : onNext : $value")
                    postValue((preferences.all[t] as T) ?: defaultValue)
                }

                override fun onError(e: Throwable) {
                    LogUtil.e(Constants.TAG, "$THIS_NAME onActive() : onError")
                }
            })
    }

    override fun onInactive() {
        LogUtil.d(Constants.TAG, "$THIS_NAME onInactive() called")
        super.onInactive()
        disposable?.dispose()
    }
}