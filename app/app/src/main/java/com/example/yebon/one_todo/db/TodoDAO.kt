package com.example.yebon.one_todo.db

import androidx.room.*
import com.example.yebon.one_todo.db.model.Todo

@Dao
interface TodoDAO {

    @Insert
    fun addNewTodo(todo: Todo)

    @Delete
    fun removeTodo(todo: Todo)

    @Update
    fun updateTodo(todo: Todo)

    @Query("SELECT * FROM todo WHERE year = :year AND month = :month ORDER BY month ASC")
    fun getTodos(year: Int, month: Int): List<Todo>

    @Query("SELECT MIN(year) FROM todo")
    fun getMinYear(): Int
}