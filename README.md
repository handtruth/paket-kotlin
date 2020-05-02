Paket Kotlin Library
========================

This library defines base for communication protocol. Right now
[mctools] uses it in Minecraft protocol implementation. Currently
compression is not supported. See also [C++] version.

Usage
------------------------

You can use this library to define your own communication protocol.

### Add Library as Dependency

This library is not finished yet. That's because it depends on
[kotlinx.io]. Currently [kotlinx.io] is in active development. I have
builds of [kotlinx.io] in my local Maven repository. So, you need to
add https://mvn.handtruth.com Maven repository. I do NOT guarantee that
this repository will persist. Also, this library is meant to be multi
platform, but currently only JVM target is supported.

In Gradle you can add dependency this way.

```kotlin
// Gradle Kotlin DSL

repositories {
    jcenter()
    maven("https://mvn.handtruth.com")
}

dependencies {
    implementation("com.handtruth.mc:paket-kotlin:$paketVersion")
    // Or you can specify JVM target explicitly
    //implementation("com.handtruth.mc:paket-kotlin-jvm:$paketVersion")
}
```

### Packet Declaration

You need to define your packets for protocol. For example, let's create
packet class with string and integer list fields.

```kotlin
enum class ExampleID {
    One, Two, Three
}

class ExamplePaket(str: String = "", list: List<Int> = emptyList()) : Paket() {
    override val id = ExampleID.One

    val str by string(str)
    val list by listOfVarInt(list)
}
```

### Packet Transmission

If you want to transfer this packet you need `PaketTransmitter`
implementation. There are some implementations for [ktor] channel,
[korio] async stream and [kotlinx.io] `Input` / `Output`.

Neither [ktor] nor [korio] are dependencies of this library so you need
to declare them yourself in Maven or Gradle.

Example:

```kotlin
val ts = PaketTransmitter(input, output)

val paketA = ExamplePaket("example", listOf(1, 2, 3))
ts.send(paketA) // Send packet to output
if (ts.catchAs<ExampleID>() == ExampleID.One) { // Check packet ID
    val paketB = ExamplePaket()
    ts.receive(paketB) // Receive packet from input
}
```

Known Issues
-------------------------------

1. This library can be slow. That's because I use some workarounds for
   bugs in [kotlinx.io] builds. We will resolve this issue when
   [kotlinx.io] will be updated.

[ktor]: https://ktor.io
[korio]: https://korlibs.soywiz.com/korio/
[kotlinx.io]: https://github.com/Kotlin/kotlinx-io
[mctools]: https://github.com/handtruth/mctools
[C++]: https://github.com/handtruth/paket-cpp
