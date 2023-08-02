# SORI Example

This is a sample source code of how to implement SORI SDK in your project in Kotlin.
You can use this example to build your own audio recognition service from scratch.

## How to build

Request your APP_ID and APP_KEY from [SORI Console Site](https://console.soriapi.com/application/).
Then, put your APP_ID and APP_KEY in `app/src/main/res/values/secrets.xml` file.

```xml
<string name="SORI_APP_ID">PUT_YOUR_APP_ID_HERE</string>
<string name="SORI_SECRET_KEY">PUT_YOUR_SECRET_KEY_HERE</string>
```

If you want to build release version, you should put your signing key information in `app/key.properties` file.
And .jks file should be located in valid location.

