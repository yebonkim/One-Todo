package com.example.yebon.one_todo.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.yebon.one_todo.R
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.dialog_ask_login.*

class AskLoginDialog(context: Context, private val goToAuthUI: () -> Unit) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_ask_login)

        login.setOnClickListener {
            goToAuthUI()
            dismiss()
        }
        later.setOnClickListener {
            dismiss()
        }
    }
}