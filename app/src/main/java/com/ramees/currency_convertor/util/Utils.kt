package com.ramees.currency_convertor.util

import android.content.res.Resources

class Utils {

    companion object{

        /** Returns height of deice display in pixels */
        fun screenHeight() = Resources.getSystem().displayMetrics.heightPixels
    }
}