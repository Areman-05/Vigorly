package com.example.vigorly

import com.example.vigorly.ui.profile.ProfileAvatarCatalog
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileAvatarCatalogTest {

    @Test
    fun isRemoteUrl_trueForHttpUrls() {
        assertTrue(ProfileAvatarCatalog.isRemoteUrl("https://example.com/avatar.jpg"))
    }

    @Test
    fun isRemoteUrl_falseForPresets() {
        assertFalse(ProfileAvatarCatalog.isRemoteUrl(ProfileAvatarCatalog.encode("spark")))
    }
}
