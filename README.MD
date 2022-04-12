# LogKit

### 初始化
```kotlin
  DebugFunc.getInstance().initContext(this)  //初始化
  DebugFunc.getInstance().startLogsActivity() //查看已经输出的日志
```

### 添加悬浮日志窗口
```kotlin
lifecycle.addObserver(object :LifecycleEventObserver{
     override fun onStateChanged(source: LifecycleOwner, event: Event) {
           if (event == ON_RESUME){
                            DebugFunc.getInstance().addFlotLogView()
            }
            if (event == ON_STOP){
            DebugFunc.getInstance().removeFlotLogView()
            }
       }
})
```
### 添加日志
```kotlin
launch {
         repeat(100){
              DebugFunc.getInstance().saveLogToFile("这是一条日志日志日志：$it")
              delay(50)
         }
 }
```