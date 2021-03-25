package com.example.androidwall.dao

import androidx.room.*
import com.example.androidwall.models.Rule

@Dao
interface RuleDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertRule(rule: Rule)

    @Update
    fun updateRule(rule : Rule)

    @Query("SELECT * FROM Rule")
    fun getRules() : List<Rule>
}