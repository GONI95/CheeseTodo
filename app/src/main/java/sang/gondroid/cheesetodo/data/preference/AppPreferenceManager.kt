package sang.gondroid.cheesetodo.data.preference

import android.content.Context
import android.content.SharedPreferences
import sang.gondroid.cheesetodo.BuildConfig

/**
 * 데이터 저장 및 로드 클래스
 */
class AppPreferenceManager(
    private val sharedPreferences: SharedPreferences
) {
    /**
     * PREFERENCES_NAME : 저장/불러오기를 위한 key
     *                    고유키로 앱에 할당된 저장소 - data/data/패키지 이름/shared_prefs에 PREFERENCES_NAME 저장
     *
     *  MODE_PRIVATE : preference의 저장 모드를 정의
     *                 [MODE_PRIVATE : 이 앱안에서 데이터 공유]
     *                 [MODE_WORLD_READABLE : 다른 앱과 데이터 읽기 공유]
     *                 [MODE_WORLD_WRITEABLE : 다른 앱과 데이터 쓰기 공유]
     */

    // getPreferences 메서드를 이용해 SharedPreference 선언과 초기화
    private val prefs by lazy { sharedPreferences }

    // SharedPreference의 데이터를 저장/편집 하기위한 Editor 프로퍼티 선언과 초기화
    private val editor by lazy { prefs.edit() }

    /**
     * 모든 저장 데이터 삭제
     * @param context
     */
    fun clear() {
        editor.clear()
        editor.apply()
    }

    fun putIdToken(idToken: String) {
        editor.putString(BuildConfig.KEY_ID_TOKEN, idToken)
        editor.apply()
    }

    fun getIdToken(): String? {
        return prefs.getString(BuildConfig.KEY_ID_TOKEN, null)
    }

    fun removeIdToken() {
        editor.putString(BuildConfig.KEY_ID_TOKEN, null)
        editor.apply()
    }

    fun setUserNameString(value: String?) {
        editor.putString(BuildConfig.KEY_USER_NAME, value)
        editor.apply()
    }

    fun getUserNameString() : String? {
        return prefs.getString(BuildConfig.KEY_USER_NAME, null)
    }

    fun removeUserNameString() {
        editor.putString(BuildConfig.KEY_USER_NAME, null)
        editor.apply()
    }
}