package sang.gondroid.cheesetodo.data.preference

import android.content.SharedPreferences
import io.reactivex.rxjava3.subjects.PublishSubject
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil

class LiveSharedPreferences(private val preferences: SharedPreferences) {
    private val THIS_NAME = this::class.simpleName

    /**
     * PublishSubject : 구독 이후에 갱신된 값에 대해서만 값을 받아오는 Subject
     */
    private val publisher = PublishSubject.create<String>()

    /**
     * SharedPreference의 기본 설정이 변경될 때 호출될 콜백 인터페이스(반환 : data에 대한 Key)
     */
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        LogUtil.d(Constants.TAG, "$THIS_NAME OnSharedPreferenceChangeListener called : $key")
        publisher.onNext(key)
    }

    /**
     * doOnSubscribe : Observable이 구독될 때 호출되는 콜백 함수를 등록할 수 있습니다.
     * doOnDispose : Observable이 구독 해지될 때 호출되는 콜백 함수를 등록할 수 있습니다.
     *
     * registerOnSharedPreferenceChangeListener : 기본 설정이 변경될 때 호출될 콜백을 등록합니다.
     * unregisterOnSharedPreferenceChangeListener : 이전 콜백을 등록 취소합니다.
     *
     * hasObservers : LiveData가 observer를 한개라도 가지고 있다면 True
     */
    private val updates = publisher
        .doOnSubscribe {
            LogUtil.d(Constants.TAG, "$THIS_NAME doOnSubscribe called : $it")
            preferences.registerOnSharedPreferenceChangeListener(listener)
        }
        .doOnDispose {
            LogUtil.d(Constants.TAG, "$THIS_NAME doOnDispose called")
            if (!publisher.hasObservers()) {
                preferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
    }

    fun getString(key: String, defaultValue: String?): LivePreference<String> {
        LogUtil.d(Constants.TAG, "$THIS_NAME getString() called")
        return LivePreference(updates, preferences, key, defaultValue)
    }
}