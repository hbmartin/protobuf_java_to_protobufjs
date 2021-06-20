# Extract ProtobufJS Messages from Generated Java Classes

[![Maven Central](https://img.shields.io/maven-central/v/com.github.hbmartin/protobuf_java_to_protobufjs?color=6D3DEE)](https://repo.maven.apache.org/maven2/com/github/hbmartin/protobuf_java_to_protobufjs/)
[![reviewdog badge](https://github.com/hbmartin/protobuf_java_to_protobufjs/actions/workflows/reviewdog.yml/badge.svg)](https://github.com/hbmartin/protobuf_java_to_protobufjs/actions/workflows/reviewdog.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/hbmartin/protobuf_java_to_protobufjs/badge)](https://www.codefactor.io/repository/github/hbmartin/protobuf_java_to_protobufjs)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=hbmartin_protobuf_java_to_protobufjs&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=hbmartin_protobuf_java_to_protobufjs)
[![GitHub issues](https://img.shields.io/github/issues/hbmartin/protobuf_java_to_protobufjs)](https://github.com/hbmartin/protobuf_java_to_protobufjs/issues)
![GitHub top language](https://img.shields.io/github/languages/top/hbmartin/protobuf_java_to_protobufjs?color=FA8A0C)

Takes the Java classes generated from Protobuf 3 messages and return a Map suitable for JSON serialization and use in [ProtobufJS](https://github.com/protobufjs/protobuf.js)

The included demo app shows how this is used for decoding protobuf data in the Flipper network inspection tool:

![Flipper Demo Screenshot](media/screenshot.png)

## Download

Install in your build.gradle:

```
dependencies {
  implementation 'com.github.hbmartin:protobuf_java_to_protobufjs:0.1.0'
}
```


## Usage

This library exposes a single call, which accept a `GeneratedMessageV3` subclass and returns an object with a `rootFullName` property describing the protobuf path of the class and a `descriptors` property containing a nested map suitable for serializing to JSON and using in ProtobufJS:

```
    ProtobufGeneratedJavaToProtobufJs(Person::class.java)
```

Also provided is a Kotlin extension method that is equivalent to the above:

```
    Person::class.toJsDescriptors()
```

### Flipper

To add automated network inspection of protobuf payloads to Flipper, use the plugin:

```groovy
dependencies {
  debugImplementation 'com.facebook.flipper:flipper-retrofit2-protobuf-plugin:latest-version'
}
```

Then call `SendProtobufToFlipperFromRetrofit` for each service class.

```kotlin
import com.facebook.flipper.plugins.retrofit2protobuf.SendProtobufToFlipperFromRetrofit

SendProtobufToFlipperFromRetrofit("https://baseurl.com/", MyApiService::class.java)
```


## License

MIT License

Copyright (c) Harold Martin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
