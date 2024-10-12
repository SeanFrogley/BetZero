package nz.ac.canterbury.seng303.betzero.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.FlowPreview
import nz.ac.canterbury.seng303.betzero.models.DailyLog
import nz.ac.canterbury.seng303.betzero.models.RelapseLog
import nz.ac.canterbury.seng303.betzero.models.UserProfile
import nz.ac.canterbury.seng303.betzero.screens.GettingStartedScreen
import nz.ac.canterbury.seng303.betzero.viewmodels.AnalyticsViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.CalendarViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.DailyLogViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.EmergencyViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.GettingStartedViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.InitialViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.UpdateUserProfileViewModel
import nz.ac.canterbury.seng303.betzero.viewmodels.UserProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nz.ac.canterbury.seng303.betzero.shared.preferences")

@FlowPreview
val dataAccessModule = module {
    single<Storage<UserProfile>>(named("userProfile")) {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<UserProfile>>(){}.type,
            preferenceKey = stringPreferencesKey("userProfile"),
            dataStore = androidContext().dataStore
        )
    }

    // Summaries storage
    single<Storage<DailyLog>>(named("dailyLog")) {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<DailyLog>>(){}.type,
            preferenceKey = stringPreferencesKey("dailyLog"),
            dataStore = androidContext().dataStore
        )
    }

    single<Storage<RelapseLog>>(named("relapseLog")) {
        PersistentStorage(
            gson = get(),
            type = object: TypeToken<List<RelapseLog>>(){}.type,
            preferenceKey = stringPreferencesKey("relapseLog"),
            dataStore = androidContext().dataStore
        )
    }

    single { Gson() }

    viewModel {
        UserProfileViewModel(
            userProfileStorage = get(named("userProfile"))
        )
    }
    viewModel {
        DailyLogViewModel(
            dailyLogStorage = get(named("dailyLog"))
        )
    }
    viewModel {
        InitialViewModel(
            userProfileStorage = get(named("userProfile"))
        )
    }
    viewModel {
        GettingStartedViewModel(
            userProfileStorage = get(named("userProfile"))
        )
    }
    viewModel {
        AnalyticsViewModel(
            userProfileStorage = get(named("userProfile"))
        )
    }
    viewModel {
        UpdateUserProfileViewModel(
            userProfileStorage = get(named("userProfile"))
        )
    }

    viewModel {
        EmergencyViewModel(
            userProfileStorage = get(named("userProfile"))
        )
    }

    viewModel {
        CalendarViewModel(
            userProfileStorage = get(named("userProfile")),
            dailyLogStorage = get(named("dailyLog")),
            relapseLogStorage = get(named("relapseLog")),
        )
    }
}