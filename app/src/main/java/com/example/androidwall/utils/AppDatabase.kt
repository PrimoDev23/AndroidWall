package com.example.androidwall.utils

import android.content.Context
import androidx.room.*
import com.example.androidwall.dao.RuleDao
import com.example.androidwall.dao.SettingDao
import com.example.androidwall.models.FirewallModeConverter
import com.example.androidwall.models.Rule
import com.example.androidwall.models.Setting

@Database(entities = [Setting::class, Rule::class], version = 1)
@TypeConverters(FirewallModeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingDao() : SettingDao
    abstract fun rulesDao() : RuleDao

    companion object {
        private var INSTANCE : AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase {
            if(INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "AndroidWallDB"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}