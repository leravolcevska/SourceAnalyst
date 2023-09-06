package com.nttsolmare.game.android.privacyshower.core.data.local

interface LinkDatabase {
    suspend fun saveLink(link : String)

    suspend fun getLink() : String?
}