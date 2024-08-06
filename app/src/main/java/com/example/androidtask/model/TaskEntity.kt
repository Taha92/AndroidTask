package com.example.androidtask.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val task: String,

    @ColumnInfo(name = "task_title")
    val title: String,

    @ColumnInfo(name = "task_description")
    val description: String,

    @ColumnInfo(name = "task_color_code")
    val colorCode: String,

    @ColumnInfo(name = "wage_type")
    val wageType: String,

    @ColumnInfo(name = "business_unit")
    val businessUnit: String,
)
