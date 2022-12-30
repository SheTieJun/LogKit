## 夸应用调试

1. 在项目添加

``` 
    Implementation 'com.github.SheTieJun.LogKit:logkit-messenger:tag'
```

2. 需要在服务APP 和 客户端APP 相关声明可见（解决在Android 11上 bindService 失败）

在服务器app项目添加:

```xml

<queries>
    <package android:name="你的项目包名" />
</queries>
```

同时需要启动服务

```kotlin
 startService(Intent(this, SLogServerService::class.java))
```

在你的项目中添加

```xml

<queries>
    <!--   me.shetj.logkit.demo需要替换成对应服务器包名-->
    <package android:name="me.shetj.logkit.demo" />
</queries>
```

``` kotlin 
// 客户端APP需要bind你的服务器 
//  me.shetj.logkit.demo需要替换成对应服务器包名 
 SLogMessenger.getInstance().bindService(this,"me.shetj.logkit.demo")
```

4. 发送消息

``` 
  SLogMessenger.getInstance().sendMsg("这是消息消息")
```

5. 客户端Timer使用

```kotlin
class SLogMessengerTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        SLogMessenger.getInstance().sendMsg("这是消息消息")
    }
}
```
 
  
