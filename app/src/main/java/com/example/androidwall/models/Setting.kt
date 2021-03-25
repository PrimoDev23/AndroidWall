package com.example.androidwall.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    var enabled: Boolean,
    var mode: FirewallMode)