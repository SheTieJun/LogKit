## 夸应用调试

1. 在项目添加

```groovy 
implementation 'com.github.SheTieJun.LogKit:logkit-messenger:tag'
```

2. 需要在服务APP 和 客户端APP 相关声明可见（解决在Android 11上 bindService 失败）

在服务器app项目添加:
```groovy
implementation  project('com.github.SheTieJun.LogKit:logkit-debug:tag')
```
同时添加下面代码保证可见性
```xml

<queries>
    <package android:name="你的项目包名" />
</queries>
```

在你的项目中添加

```xml

<queries>
    <!--   me.shetj.logkit.demo需要替换成对应服务器包名-->
    <package android:name="me.shetj.logkit.demo" />
</queries>
```

3. 客户端连接服务器

```kotlin 
// 客户端APP需要bind你的服务器 
// me.shetj.logkit.demo需要替换成对应服务器包名 
SLogMessenger.getInstance().bindService(this,"me.shetj.logkit.demo")
```

4. 发送消息

```kotlin
SLogMessenger.getInstance().sendMsg("这是消息消息")
SLogMessenger.getInstance().v(tag, message)
SLogMessenger.getInstance().i(tag, message)
SLogMessenger.getInstance().d(tag, message)
SLogMessenger.getInstance().w(tag, message)
SLogMessenger.getInstance().e(tag, message) 
```

5. 客户端Timber使用

```kotlin
class SLogMessengerTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val tag = tag ?: "Messenger"
        when (priority) {
            Log.VERBOSE -> SLogMessenger.getInstance().v(tag, message)
            Log.INFO -> SLogMessenger.getInstance().i(tag, message)
            Log.DEBUG -> SLogMessenger.getInstance().d(tag, message)
            Log.WARN -> SLogMessenger.getInstance().w(tag, message)
            Log.ERROR -> SLogMessenger.getInstance().e(tag, message)
            else -> SLogMessenger.getInstance().v(tag, message)
        }
    }
}
```
 
  

