package com.iplateia.sori.example

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SoriCredentialConfigurationTest {
    @Test
    fun rejectsBlankValues() {
        assertFalse(SoriCredentialConfiguration.isLocallyConfigured("", "secret-value"))
        assertFalse(SoriCredentialConfiguration.isLocallyConfigured("app-id", "   "))
    }

    @Test
    fun rejectsDocumentedPlaceholderValues() {
        assertFalse(
            SoriCredentialConfiguration.isLocallyConfigured(
                "\"YOUR_APP_ID_HERE\"",
                "\"YOUR_SECRET_KEY_HERE\"",
            ),
        )
    }

    @Test
    fun rejectsOtherObviousTemplateValues() {
        assertFalse(
            SoriCredentialConfiguration.isLocallyConfigured(
                "<sori-app-id>",
                "replace me",
            ),
        )
    }

    @Test
    fun rejectsPatternedPlaceholderValues() {
        assertFalse(SoriCredentialConfiguration.isLocallyConfigured("TODO", "secret-value"))
        assertFalse(
            SoriCredentialConfiguration.isLocallyConfigured(
                "replace-with-your-app-id",
                "secret-value",
            ),
        )
        assertFalse(
            SoriCredentialConfiguration.isLocallyConfigured(
                "YOUR_APP_ID_HERE_123",
                "secret-value",
            ),
        )
        assertFalse(
            SoriCredentialConfiguration.isLocallyConfigured(
                "app-id",
                "replace-with-your-secret-key",
            ),
        )
    }

    @Test
    fun acceptsNonPlaceholderValuesWithoutClaimingServerValidity() {
        assertTrue(
            SoriCredentialConfiguration.isLocallyConfigured(
                "locally-configured-app-id",
                "locally-configured-secret",
            ),
        )
    }
}
