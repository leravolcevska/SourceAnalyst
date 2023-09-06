package com.nttsolmare.game.android.fithlib.core.util

import java.net.URLEncoder

internal fun encodeUrl(url: String): String {
    return try {
        URLEncoder.encode(url, "utf-8")
    } catch (e: Exception) {
        "null"
    }
}
