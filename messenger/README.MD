## 跨应用调试,基于Messenger
(不推荐使用，非必要不要使用这种方式集成)
1. 在项目添加

```groovy 
implementation 'com.github.SheTieJun.LogKit:logkit-messenger:tag'
```

2. 需要在服务APP 和 客户端APP 相关声明可见（解决在Android 11上 bindService 失败）

在服务器app项目添加:

```groovy
implementation project('com.github.SheTieJun.LogKit:logkit-debug:tag')
```

在客户端的项目中同时添加，保存应用可见性

```xml

<queries>
    <!--   me.shetj.logkit.demo需要替换成对应服务器包名，当前是app的应用-->
    <package android:name="me.shetj.logkit.demo" />
</queries>
```

3. 客户端连接服务器

```kotlin 
// 客户端APP需要bind你的服务器 
// me.shetj.logkit.demo需要替换成对应服务器包名 
SLogMessenger.getInstance().bindService(this,"me.shetj.logkit.demo")
```

4. 发送日志消息

```kotlin
SLogMessenger.getInstance().sendMsg("这是消息消息")
SLogMessenger.getInstance().v(tag, message)
SLogMessenger.getInstance().i(tag, message)
SLogMessenger.getInstance().d(tag, message)
SLogMessenger.getInstance().w(tag, message)
SLogMessenger.getInstance().e(tag, message) 
```

5. 发送日志消息到文件

```kotlin
SLogMessenger.getInstance().v(tag, message, pushFile)
SLogMessenger.getInstance().i(tag, message, pushFile)
SLogMessenger.getInstance().d(tag, message, pushFile)
SLogMessenger.getInstance().w(tag, message, pushFile)
SLogMessenger.getInstance().e(tag, message, pushFile) 
```

6. 客户端Timber使用

```kotlin
class SLogMessengerTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val tag = tag ?: "Messenger"
        when (priority) {
            Log.VERBOSE -> SLogMessenger.getInstance().v(tag, message)
            Log.INFO -> SLogMessenger.getInstance().i(tag, message)
            Log.DEBUG -> SLogMessenger.getInstance().d(tag, message)
            Log.WARN -> SLogMessenger.getInstance().w(tag, message)
            Log.ERROR -> SLogMessenger.getInstance().e(tag, message, true)
            else -> SLogMessenger.getInstance().v(tag, message)
        }
    }
}
```

7.自动检测前后台变化，隐藏悬浮窗
```groovy
//true 开启，false 关闭
SLogMessenger.getInstance().autoHide(context,true)
```


## 建议

- 服务器开启自启动，这样用使用的时候，可以自动唤起服务，不需要打开服务器APP
 
  

