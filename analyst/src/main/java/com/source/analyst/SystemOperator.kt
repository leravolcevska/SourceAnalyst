package com.source.analyst

import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SystemOperator {
    suspend fun getGoogleId(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                AdvertisingIdClient.getAdvertisingIdInfo(context).id
            } catch (e: Exception) {
                if (e is CancellationException) throw CancellationException()

                null
            }
        }
    }

    suspend fun checkBotStatus(context: Context): Boolean {
        val adbStatus = try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) != 0
        } catch (e: Exception) {
            true
        }


        val isBatteryFull = withContext(Dispatchers.IO) {
            try {
                val batteryManager =
                    context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

                val lvl = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    .toFloat()

                lvl == 100f
            } catch (e: Exception) {
                true
            }
        }

        return adbStatus && isBatteryFull
    }
}