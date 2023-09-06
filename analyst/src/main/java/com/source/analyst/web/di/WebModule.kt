/*
package com.nttsolmare.game.android.privacyshower.web.di

import com.nttsolmare.game.android.privacyshower.core.data.local.LinkDatabase
import com.nttsolmare.game.android.privacyshower.web.opearator.*
import com.nttsolmare.game.android.privacyshower.web.opearator.CreateFile
import com.nttsolmare.game.android.privacyshower.web.opearator.HandleOnBackPressed
import com.nttsolmare.game.android.privacyshower.web.opearator.SaveLink
import com.nttsolmare.game.android.privacyshower.web.opearator.SetWebSettings
import com.nttsolmare.game.android.privacyshower.web.opearator.WebOperator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal object WebModule {

    @Provides
    @ActivityScoped
    fun provideWebOperator(
        linkDatabase: LinkDatabase,
    ): WebOperator {
        return WebOperator(
            createFile = CreateFile(),
            handleOnBackPressed = HandleOnBackPressed(),
            setWebSettings = SetWebSettings(),
            saveLink = SaveLink(linkDatabase),
        )
    }
}*/
