package sang.gondroid.myapplication

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import sang.gondroid.myapplication.di.appModule

/**
 * AndroidManifest.xml - android:name 설정이 필요
 */
class CheeseTodoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CheeseTodoApplication)
            modules(appModule)
        }
    }

    /**
     * App 종료 시 : appContext가 null이 되도록 설정
     */
    override fun onTerminate() {
        super.onTerminate()
        appContext = null
    }

    companion object {
        var appContext : Context? = null
            private set
    }
}