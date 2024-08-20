package com.lilstiffy.mockgps.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibratorService {
    private var vibrator_s: VibratorManager? = null
    private var vibrator: Vibrator? = null

    fun initialise(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibrator_s =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        } else {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibrator_s?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
        } else {
            vibrator?.vibrate(300L)
        }
    }
}