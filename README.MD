# Slog

- 文件日志输出、分享到其他应用
- 实时可视化界面
- 日志过滤
- 监测前后台变化自动隐藏悬浮窗

## 初始化

```kotlin

SLog.init(this) //必须主线程初始化

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

### 方式二（推荐）

#### 输出日志 SLog + Timber 

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
        //slog.log(priority,s,message)
    }
}
Timber.plant(SLogTree())
```

#### 输出网络请求 SLog + HttpSLogInterceptor

SLog + [HttpSLogInterceptor](app/src/main/java/me/shetj/logkit/HttpSLogInterceptor.kt),达到输出网络请求

## 添加日志到文件

```kotlin
SLog.getInstance().logFile(VERBOSE, "logFile", "这是一条错误VERBOSE日志")
```
文件命名：`[TAG]MM-dd HH时mm分`

> 按TAG和每分钟进行输出文件，方便查找
>
> 所以需要合理使用TAG,进行日志文件输出


## 自动检测前后台变化，隐藏日志相关悬浮窗，默认开启

```groovy
//true 开启，false 关闭
SLog.getInstance().autoHide(context, true)
```

## 效果图片

| 效果图片                    | 日志展示 1              | 日志展示 2              | 日志文件管理             | Logo长按菜单           |
|-------------------------|---------------------|---------------------|--------------------|--------------------|
| ![](img/def_pic_1.webp) | ![](img/pic_1.webp) | ![](img/pic_2.webp) | ![](img/日志管理.webp) | ![](img/长按菜单.webp) |

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

## 跨应用输出日志 -[相关文档](messenger)

- 需要解决在Android >11上 bindService 失败的问题：应用可见性

## ToDo

- [X] ~~增加通过悬浮球关闭整个日志功能 : 长按弹窗关闭~~
- [X] ~~增加隱藏方法：检测APP是否在前台（如果启动就显示）或者后台（自动隐藏悬浮窗）~~

## 其他

1. 请注意增加`${applicationId}.FileProvider` 用于分享
```xml
<provider android:name="androidx.core.content.FileProvider" android:authorities="${applicationId}.FileProvider"
    android:exported="false" android:grantUriPermissions="true">
    <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/base_file_path" />
</provider>
```

