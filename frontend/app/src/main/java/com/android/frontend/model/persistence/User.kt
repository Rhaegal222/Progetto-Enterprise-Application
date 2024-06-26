package com.android.frontend.model.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Long?,

    @ColumnInfo(name = "refresh_token") val refreshToken: String?
)