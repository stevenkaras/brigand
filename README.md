# Brigand
Brigand is the opportunistic bastard of all communication libraries

## Synopsis
It will allow other apps to be blamed for used power while making its own network transfers

## Usage

Just drop this into your `build.gradle` file:

```groovy
dependencies {
    compile 'com.getout-tlv.brigand:Brigand:1.1.+'
}

### Local Project Usage

If you'd prefer to work with a local copy of the project, then just put this in your `settings.gradle` file:

```groovy
include ':libraries:Brigand'
project(':libraries:Brigand').projectDir = new File(settingsDir, '../brigand/Brigand')
```

and then add this to your application's `build.gradle` file:

```groovy
dependencies {
    compile project(':libraries:Brigand')
}
```

## License

Brigand is freely available under the MIT license