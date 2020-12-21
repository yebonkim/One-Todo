package com.example.yebon.one_todo.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.yebon.one_todo.R
import kotlinx.android.synthetic.main.dialog_year_month.*

class YearMonthDialog(context: Context,
                      private val year: Int,
                      private val month: Int,
                      private val onDismissListener: (selectedYear: Int, selectedMonth: Int) -> Unit) : Dialog(context) {

    private val JAN = 1;
    private val DEC = 12;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_year_month)

        // Todo - set minValue from db
        year_picker.minValue = 2020
        year_picker.maxValue = year
        year_picker.wrapSelectorWheel = false

        month_picker.minValue = JAN
        month_picker.maxValue = DEC
        month_picker.wrapSelectorWheel = false
        month_picker.value = month

        confirm.setOnClickListener {
            dismiss()
            onDismissListener(year_picker.value, month_picker.value)
        }
    }
}