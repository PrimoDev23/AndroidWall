package com.example.androidwall.dao

import androidx.room.*
import com.example.androidwall.models.Setting

@Dao
interface SettingDao {
     @Insert(onConflict = OnConflictStrategy.ABORT)
     fun insertSetting(setting: Setting)

    @Update
    fun updateSetting(setting : Setting)

    @Query("SELECT * FROM Setting")
    fun getSetting() : List<Setting>
}