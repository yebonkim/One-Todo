package com.example.yebon.one_todo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yebon.one_todo.db.model.Todo

@Database(entities = arrayOf(Todo::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        val DATABASE_NAME = "TODO"
        val INVALID_YEAR = 0
    }
    abstract fun todoDao(): TodoDAO
}