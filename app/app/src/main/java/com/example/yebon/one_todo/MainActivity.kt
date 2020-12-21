package com.example.yebon.one_todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.yebon.one_todo.view.YearMonthDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val calendar by lazy {
        Calendar.getInstance()
    }

    private val onDismissListener = {selectedYear: Int, selectedMonth: Int ->
        year.text = selectedYear.toString()
        month.text = selectedMonth.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        year.text = getNowYear().toString()
        month.text = getNowMonth().toString()
        date_container.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.date_container -> showDatePicker()
        }
    }

    private fun showDatePicker() {
        YearMonthDialog(this, getSelectedYear(), getSelectedMonth(), onDismissListener).show()
    }

    private fun getNowYear() = calendar.get(Calendar.YEAR)
    private fun getNowMonth() = calendar.get(Calendar.MONTH) + 1
    private fun getSelectedYear() = Integer.parseInt(year.text.toString())
    private fun getSelectedMonth() = Integer.parseInt(month.text.toString())
}