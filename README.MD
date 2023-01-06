# Slog

## 初始化

```kotlin

SLog.init(this)

```

## 开启和关闭

```kotlin
SLog.getInstance().start()
```

```kotlin
SLog.getInstance().stop()
```

## 添加日志

### 方式一

```kotlin
SLog.v("这是一条日志日志日志")
```

### 方式二

use Timber,

```Kotlin
class SLogTree : Timber.Tree() {

    private val slog: SLog = SLog.getInstance()
    private val defaultTag = "log"

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val tag = tag ?: defaultTag

        when (priority) {
            Log.VERBOSE -> slog.v(tag, message)
            Log.INFO -> slog.i(tag, message)
            Log.DEBUG -> slog.d(tag, message)
            Log.WARN -> slog.w(tag, message)
            Log.ERROR -> slog.e(tag, message)
            else -> slog.v(tag, message)
        }
    }
}


Timber.plant(SLogTree())
```

## 效果图片

| 效果图片                    |                     |                     |
|-------------------------|---------------------|---------------------|
| ![](img/def_pic_1.webp) | ![](img/pic_1.webp) | ![](img/pic_2.webp) |


## 接入

[![](https://jitpack.io/v/SheTieJun/LogKit.svg)](https://jitpack.io/#SheTieJun/LogKit)

Step 1. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	    debugImplementation 'com.github.SheTieJun.LogKit:logkit-debug:tag'
        releaseImplementation 'com.github.SheTieJun.LogKit:logkit-release:tag'
	}



## 夸应用输出日志 
- 需要解决在Android 11上 bindService 失败

- 详细使用方式见 demo
``` 
	dependencies {
	    implementation 'com.github.SheTieJun.LogKit:logkit-messenger:tag'
	}
```
-[相关文档](messenger)

## ToDo
- [X] ~~增加通过悬浮球关闭整个日志功能 : 长按弹窗关闭~~