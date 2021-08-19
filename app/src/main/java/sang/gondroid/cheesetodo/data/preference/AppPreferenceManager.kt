package sang.gondroid.cheesetodo.data.preference

import android.content.Context
import android.content.SharedPreferences

/**
 * 데이터 저장 및 로드 클래스
 */
class AppPreferenceManager(
    private val context: Context
) {

    companion object {
        const val PREFERENCES_NAME = "sang.gondroid.cheesetodo-pref"
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1
        private const val DEFAULT_VALUE_LONG = -1L
        private const val DEFAULT_VALUE_FLOAT = -1f

        const val KEY_ID_TOKEN = "ID_TOKEN"
        const val KEY_USER_NAME = "USER_NAME"
        //저장 모드
        private const val KEY_SAVE_MODE = "KEY_SAVE_MODE"
        //검색어 기록
        private const val KEY_SEARCH_HISTORY = "KEY_SEARCH_HISTORY" // shared에 저장될 data key

    }

    /**
     * PREFERENCES_NAME : 저장/불러오기를 위한 key
     *                    고유키로 앱에 할당된 저장소 - data/data/패키지 이름/shared_prefs에 PREFERENCES_NAME 저장
     *
     *  MODE_PRIVATE : preference의 저장 모드를 정의
     *                 [MODE_PRIVATE : 이 앱안에서 데이터 공유]
     *                 [MODE_WORLD_READABLE : 다른 앱과 데이터 읽기 공유]
     *                 [MODE_WORLD_WRITEABLE : 다른 앱과 데이터 쓰기 공유]
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    // getPreferences 메서드를 이용해 SharedPreference 선언과 초기화
    private val prefs by lazy { getPreferences(context) }

    // SharedPreference의 데이터를 저장/편집 하기위한 Editor 프로퍼티 선언과 초기화
    private val editor by lazy { prefs.edit() }

    /**
     * String 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * boolean 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * int 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setInt(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * long 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setLong(key: String?, value: Long) {
        editor.putLong(key, value)
        editor.apply()
    }

    /**
     * float 값 저장
     * @param context
     * @param key
     * @param value
     */
    fun setFloat(key: String?, value: Float) {
        editor.putFloat(key, value)
        editor.apply()
    }

    /**
     * String 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getString(key: String?): String? {
        return prefs.getString(key, DEFAULT_VALUE_STRING)
    }

    /**
     * boolean 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getBoolean(key: String?): Boolean {
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    /**
     * int 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getInt(key: String?): Int {
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }

    /**
     * long 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getLong(key: String?): Long {
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

    /**
     * float 값 로드
     * @param context
     * @param key
     * @return
     */
    fun getFloat(key: String?): Float {
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    /**
     * 키 값 삭제
     * @param context
     * @param key
     */
    fun removeKey(key: String?) {
        editor.remove(key)
        editor.apply()
    }

    /**
     * 모든 저장 데이터 삭제
     * @param context
     */
    fun clear() {
        editor.clear()
        editor.apply()
    }

    fun putIdToken(idToken: String) {
        editor.putString(KEY_ID_TOKEN, idToken)
        editor.apply()
    }

    fun getIdToken(): String? {
        return prefs.getString(KEY_ID_TOKEN, null)
    }

    fun removeIdToken() {
        editor.putString(KEY_ID_TOKEN, null)
        editor.apply()
    }

    fun setUserNameString(value: String?) {
        editor.putString(KEY_USER_NAME, value)
        editor.apply()
    }

    fun getUserNameString() : String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun removeUserNameString() {
        editor.putString(KEY_USER_NAME, null)
        editor.apply()
    }
}