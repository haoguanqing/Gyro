package com.ghao.apps.gyro.util

import android.util.Log

fun log(message: () -> String) {
    Log.e("HGQQQ", message())
}
