package com.example.yebon.one_todo.db

import androidx.room.*
import com.example.yebon.one_todo.db.model.Todo
import io.reactivex.Single

@Dao
interface TodoDAO {

    @Insert
    fun addNewTodo(todo: Todo): Single<Long>

    @Delete
    fun removeTodo(todo: Todo): Single<Int>

    @Update
    fun updateTodo(todo: Todo): Single<Int>

    @Query("SELECT * FROM todo WHERE year = :year AND month = :month AND day = :day")
    fun getTodo(year: Int, month: Int, day: Int): Single<Todo>

    @Query("SELECT * FROM todo WHERE year = :year AND month = :month ORDER BY day DESC")
    fun getTodos(year: Int, month: Int): Single<List<Todo>>

    @Query("SELECT MIN(year) FROM todo")
    fun getMinYear(): Single<Int>

    @Query("SELECT MAX(year) FROM todo")
    fun getMaxYear(): Single<Int>
}