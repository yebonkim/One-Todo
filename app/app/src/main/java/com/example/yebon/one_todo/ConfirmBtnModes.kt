package com.example.yebon.one_todo

import androidx.annotation.IntDef

class ConfirmBtnModes {
    @IntDef(NONE, INSERT_MODE, CONTENT_UPDATE_MODE, DONE_UPDATE_MODE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ConfirmBtnMode

    companion object {
        const val NONE = 0
        const val INSERT_MODE = 1
        const val CONTENT_UPDATE_MODE = 2
        const val DONE_UPDATE_MODE = 3
    }
}