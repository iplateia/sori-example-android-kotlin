package com.iplateia.sori.example

internal object SoriCredentialConfiguration {
    private val commonPlaceholders = setOf(
        "CHANGE_ME",
        "CHANGEME",
        "EXAMPLE",
        "PLACEHOLDER",
        "REPLACE_ME",
        "SAMPLE",
    )

    private val appIdPlaceholders = setOf(
        "APP_ID",
        "EXAMPLE_APP_ID",
        "SAMPLE_APP_ID",
        "SORI_APP_ID",
        "YOUR_APP_ID",
        "YOUR_APP_ID_HERE",
    )

    private val secretKeyPlaceholders = setOf(
        "EXAMPLE_SECRET_KEY",
        "SAMPLE_SECRET_KEY",
        "SECRET_KEY",
        "SORI_SECRET_KEY",
        "YOUR_SECRET_KEY",
        "YOUR_SECRET_KEY_HERE",
    )

    fun isLocallyConfigured(appId: String, secretKey: String): Boolean {
        return isConfiguredValue(appId, appIdPlaceholders) &&
            isConfiguredValue(secretKey, secretKeyPlaceholders)
    }

    private fun isConfiguredValue(value: String, fieldPlaceholders: Set<String>): Boolean {
        val normalized = value
            .trim()
            .removeSurrounding("\"")
            .removeSurrounding("'")
            .uppercase()
            .replace(Regex("[^A-Z0-9]+"), "_")
            .trim('_')

        return normalized.isNotEmpty() &&
            normalized !in commonPlaceholders &&
            normalized !in fieldPlaceholders
    }
}
