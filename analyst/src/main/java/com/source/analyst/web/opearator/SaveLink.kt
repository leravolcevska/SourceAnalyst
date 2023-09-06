package com.nttsolmare.game.android.privacyshower.web.opearator

import com.nttsolmare.game.android.privacyshower.core.data.local.LinkDatabase

internal class SaveLink(
    private val linkDatabase: LinkDatabase
) {
    suspend operator fun invoke(link : String){
        return linkDatabase.saveLink(link)
    }
}