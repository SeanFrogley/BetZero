package nz.ac.canterbury.seng303.betzero

import android.app.Application
import kotlinx.coroutines.FlowPreview
import nz.ac.canterbury.seng303.betzero.datastore.dataAccessModule
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    @OptIn(FlowPreview::class)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(dataAccessModule)
        }
    }
}
