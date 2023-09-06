package com.nttsolmare.game.android.privacyshower.splash.domain.operation

import com.nttsolmare.game.android.privacyshower.core.data.local.LinkDatabase

internal class GetLink(
    private val linkDatabase: LinkDatabase,
){
    suspend operator fun invoke(): String? {
        return linkDatabase.getLink()
    }
}