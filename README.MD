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

use Timer,

```Kotlin
class SLogTree : Timber.Tree() {

    private val vlog: SLog = SLog.getInstance()
    private val defaultTag = ""

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val tag = tag ?: defaultTag

        when (priority) {
            Log.VERBOSE -> vlog.v(tag, message)
            Log.INFO -> vlog.i(tag, message)
            Log.DEBUG -> vlog.d(tag, message)
            Log.WARN -> vlog.w(tag, message)
            Log.ERROR -> vlog.e(tag, message)
            else -> vlog.v(tag, message)
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