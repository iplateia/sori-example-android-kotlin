package com.iplateia.sori.example

internal object SoriCredentialConfiguration {
    private val commonPlaceholderPatterns = setOf(
        "CHANGE_ME",
        "CHANGEME",
        "PLACEHOLDER",
        "REPLACE_ME",
        "REPLACE_WITH",
    )

    private val commonPlaceholderTokens = setOf(
        "EXAMPLE",
        "SAMPLE",
        "TODO",
    )

    private val appIdPlaceholders = setOf(
        "APP_ID",
        "EXAMPLE_APP_ID",
        "SAMPLE_APP_ID",
        "SORI_APP_ID",
        "YOUR_APP_ID",
        "YOUR_APP_ID_HERE",
    )

    private val appIdPlaceholderPatterns = setOf(
        "EXAMPLE_APP_ID",
        "SAMPLE_APP_ID",
        "SORI_APP_ID",
        "YOUR_APP_ID",
    )

    private val secretKeyPlaceholders = setOf(
        "EXAMPLE_SECRET_KEY",
        "SAMPLE_SECRET_KEY",
        "SECRET_KEY",
        "SORI_SECRET_KEY",
        "YOUR_SECRET_KEY",
        "YOUR_SECRET_KEY_HERE",
    )

    private val secretKeyPlaceholderPatterns = setOf(
        "EXAMPLE_SECRET_KEY",
        "SAMPLE_SECRET_KEY",
        "SORI_SECRET_KEY",
        "YOUR_SECRET_KEY",
    )

    fun isLocallyConfigured(appId: String, secretKey: String): Boolean {
        return isConfiguredValue(appId, appIdPlaceholders, appIdPlaceholderPatterns) &&
            isConfiguredValue(secretKey, secretKeyPlaceholders, secretKeyPlaceholderPatterns)
    }

    private fun isConfiguredValue(
        value: String,
        fieldPlaceholders: Set<String>,
        fieldPlaceholderPatterns: Set<String>,
    ): Boolean {
        val normalized = value
            .trim()
            .removeSurrounding("\"")
            .removeSurrounding("'")
            .uppercase()
            .replace(Regex("[^A-Z0-9]+"), "_")
            .trim('_')

        val tokens = normalized.split('_')
        return normalized.isNotEmpty() &&
            normalized !in fieldPlaceholders &&
            commonPlaceholderPatterns.none(normalized::contains) &&
            commonPlaceholderTokens.none(tokens::contains) &&
            fieldPlaceholderPatterns.none(normalized::contains)
    }
}
