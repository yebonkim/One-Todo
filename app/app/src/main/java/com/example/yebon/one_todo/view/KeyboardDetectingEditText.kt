package com.example.yebon.one_todo.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText

class KeyboardDetectingEditText : AppCompatEditText {

    private var mKeyBoardHideListener: OnKeyboardHideListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setKeyBoardHideListener(keyboardHideListener: OnKeyboardHideListener) {
        mKeyBoardHideListener = keyboardHideListener
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            mKeyBoardHideListener?.onHideKeyboard()
        }
        return super.dispatchKeyEvent(event)
    }

    interface OnKeyboardHideListener {
        fun onHideKeyboard()
    }
}