package sang.gondroid.cheesetodo.data.preference

import android.content.SharedPreferences
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import sang.gondroid.cheesetodo.util.Constants
import sang.gondroid.cheesetodo.util.LogUtil

/**
 * Gon : Observable SharedPreference 역할을 하는 Class 입니다.
 */
class LiveSharedPreferences(private val preferences: SharedPreferences) {
    private val THIS_NAME = this::class.simpleName

    /**
     * Gon : 구독 이후에 갱신된 값에 대해서만 값을 받아오는 PublishSubject를 선언과 초기화 했습니다.
     *       Subject : Observer 역할과 Overvable 역할을 모두 합니다.
     */
    private val publisher = PublishSubject.create<String>()
    /**
     * Gon : SharedPreference의 기본 설정이 변경되면 호출될 콜백 인터페이스를 정의했습니다. [반환값 : 변경된 data에 대한 key]
     *       호출 시 publisher
     */
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        LogUtil.d(Constants.TAG, "$THIS_NAME OnSharedPreferenceChangeListener called : $key")
        publisher.onNext(key)
    }

    /**
     * Gon : Observable(publisher) 구독 유무에 따라 리스너를 등록 및 취소합니다.
     *
     *       doOnSubscribe - Observable을 구독했을 때 호출되는 콜백 함수입니다. (내부에서 구독 시점에 원하는 작업을 할 수 있습니다.)
     *       doOnDispose - Observable의 구독을 해지했을 때 호출되는 콜백 함수입니다.
     *
     *       registerOnSharedPreferenceChangeListener - SharedPreference의 기본 설정이 변경될 때 호출될 콜백 리스너를 등록합니다.
     *       unregisterOnSharedPreferenceChangeListener - 콜백 리스너를 등록 취소합니다.
     *
     *       hasObservers - Subject를 관찰하는 하나의 observer라도 있다면 True를 반환합니다.
     */
    private val updates = publisher
        .doOnSubscribe { disposable ->
            LogUtil.d(Constants.TAG, "$THIS_NAME doOnSubscribe called : $disposable")
            preferences.registerOnSharedPreferenceChangeListener(listener)
        }.doOnDispose {
            LogUtil.d(Constants.TAG, "$THIS_NAME doOnDispose called")
            if (!publisher.hasObservers()) {
                preferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }

    /**
     * Gon : LiveData를 확장한 class인 LivePreference를 반환하는 getter 메서드를 작성하여 Observer를 통해 관찰이 가능해집니다.
     *       SharedPreferences로부터 value를 검색하기 위한 key, value가 null인 경우 초기화하기 위해 defaultValue를 매개변수로 받습니다.
     */
    fun getString(key: String, defaultValue: String?): LivePreference<String> {
        return LivePreference(updates, preferences, key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean?): LivePreference<Boolean> {
        return LivePreference(updates, preferences, key, defaultValue)
    }
}