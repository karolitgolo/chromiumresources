# Jcef resources for Windows XP
This library of Java, contains binary files of Jcef for
Windows XP 32 and 64 bit. This library tested with Windows XP 32 bit SP 3
and Windows 10 Home Edition 64 bit.

## Add library

Add repository to ```build.gradle``` file:

```
repositories {
    maven {
        url "http://dl.bintray.com/itgolo/libs"
    }
}
```

Add dependencies to ```build.gradle``` file:

```
compile group: 'pl.itgolo.libs', name: 'chromiumresources', version: '1.+'
```

## How it's working
After this library to dependencies, you can be add to application code:

```
File dirAppRoot = new File(".");
CreateBinJcef createBinJcef = new CreateBinJcef(dirAppRoot);
createBinJcef.copyFromResource();
```

The code unpack bin files JCef to directory ```app/chromium```. Library automation
detect architecture of processor.

The library automation add path system to directory ```app/chromium```.

## Publish lib in Bintray

For publish package in Bintray use in gradle ```publishLibBintray```.
In this command run be all tests unit, integration and functional.

## Include licenses
To your project must be include licenses about dependencies:
chromium (cef), gluegen and jogl.