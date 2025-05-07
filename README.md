# SORI Example

This is a sample source code of how to implement SORI SDK in your project in Kotlin.
You can get inspired by this example to build your own audio recognition service.
Please note that this is a sample code and not a production-ready code. Only for educational purposes.


## How to build

Request your application id and secret key from [SORI Console Site](https://console.soriapi.com/account/application/).
Then, put your `SORI_APP_ID` and `SORI_SECRET_KEY` in local.properties file.
Please start with rename `local.properties.dist` to `local.properties` and put your information in it.

```properties
SORI_APP_ID="YOUR_APP_ID_HERE"
SORI_SECRET_KEY="YOUR_SECRET_KEY_HERE"
```

This will hydrate the `SORI_APP_ID` and `SORI_SECRET_KEY` variables into the resource strings at build time.
So, you can account for them in your code as follows:

```kotlin
sori = SORIAudioRecognizer(
    getString(R.string.SORI_APP_ID),
    getString(R.string.SORI_SECRET_KEY),
)
```

Or manage your secret key in your own way. like using environment variables or other secret management tools.
