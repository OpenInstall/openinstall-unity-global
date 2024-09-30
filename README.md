# openinstall-unity-global
 方便 unity 集成使用 openinstall global
 **正在开发中...**
	
## 导入 openinstallglobal.unitypackage
右击 `Assets` ，选择 `Import Package` 中的 `Custom Package...`    

在文件选择框中选中 `openinstallglobal.unitypackage`，导入所有文件

![导入package](https://res.cdn.openinstall.io/doc/unity-import.png)

将 `Assets/Plugins/OpenInstall` 下的 `OpenInstall.prefab` 拖入对应的场景中使用 openinstall 实现业务逻辑

![设置prefab](https://res.cdn.openinstall.io/doc/unity-prefab.jpg)

## 平台配置

前往 [openinstall global 官网](https://www.openinstallglobal.com/)，注册账户，登录管理控制台，创建应用后，跳过 "集成指引"，在 "应用集成" 的对应平台的 "应用配置" 中获取 `appkey` 和 `scheme` 以及 iOS 的关联域名。

![获取appkey和scheme](https://res.cdn.openinstall.io/doc/ios-appkey.png)

### iOS 平台

无需写一句Object-C代码，只需进行如下配置  

_备注_：  
- 如果用户使用了 `IMPL_APP_CONTROLLER_SUBCLASS` 宏生成自己的 `customAppController`文件（或其它自定义名称）,请在该文件中添加一键拉起的回调方法，并删除掉 `Assets/Plugins/iOS/libs` 中的 `CustomAppController.mm` 文件；  

#### 初始化配置

在 Info.plist 文件中配置 appKey 键值对，如下：
``` xml
<key>com.openinstall.APP_KEY</key>
<string>从openinstall用户控制台获取的应用appkey</string>
```

#### universal links配置（iOS9以后推荐使用）

对于iOS，为确保能正常跳转，AppID必须开启Associated Domains功能，请到[苹果开发者网站](https://developer.apple.com)，选择Certificate, Identifiers & Profiles，选择相应的AppID，开启Associated Domains。
![开启Associated Domains](https://res.cdn.openinstall.io/doc/ios-ulink-1.png)

**注意：当AppID重新编辑过之后，需要更新相应的mobileprovision证书。**

如果已经开启过Associated Domains功能，进行下面操作：  

- 在左侧导航器中点击您的项目
- 选择 `Capabilities` 标签
- 打开 `Associated Domains` 开关
- 添加 openinstall 官网后台中应用对应的关联域名
![设置 ulink](https://res.cdn.openinstall.io/doc/ios-ulink-3.png)

**以下配置为可选项**  
openinstall可兼容微信openSDK1.8.6以上版本的通用链接跳转功能，注意微信SDK初始化方法中，传入正确格式的universal link链接：  

``` objc
//your_wxAppID从微信后台获取，yourAppkey从openinstall后台获取
[WXApi registerApp:@"your_wxAppID" universalLink:@"https://yourAppkey.openinstall.io/ulink/"];
```

微信开放平台后台Universal links配置，要和上面代码中的保持一致  

![微信后台配置](https://res.cdn.openinstall.io/doc/ios-wx-ulink.jpg)

- 微信SDK更新参考[微信开放平台更新文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/iOS.html)  


#### scheme 配置

在 `Info.plist` 文件中，在 `CFBundleURLTypes` 数组中添加应用对应的 `scheme`，或者在工程“TARGETS-Info-URL Types”里快速添加，图文配置请看[Unity3d接入指南](https://www.openinstall.io/doc/unity3d_sdk.html)  

``` xml
	<key>CFBundleURLTypes</key>
	<array>
	    <dict>
		<key>CFBundleTypeRole</key>
		<string>Editor</string>
		<key>CFBundleURLName</key>
		<string>openinstall</string>
		<key>CFBundleURLSchemes</key>
		<array>
		    <string>"从openinstall官网后台获取应用的scheme"</string>
		</array>
	    </dict>
	</array>
```

### Android 平台

#### 简单配置
将 `sample/Android` 目录中的 `AndroidManifest.xml` 文件拷贝到项目的 `Assets/Plugin/Android/` 目录下，并修改文件内容：**将 openinstall 为应用分配的 `appkey` 和 `scheme` 替换至相应位置**  

#### 自定义配置
- 如果项目已存在自己的 `AndroidManifest.xml` 文件，根据下图标注的内容做相应的更改  
![修改AndroidManifest](https://res.cdn.openinstall.io/doc/unity-manifest.png)

- 如果需要使用自己的拉起 `Activity` ，可参考 `sample/Android/src` 目录中的 `OgUnityActivity.java` 在拉起 `Activity` 的 `onCreate(Bundle savedInstanceState)` 和 `onNewIntent(Intent intent)` 中添加拉起处理代码

- 如果需要使用自定义的 `Application`，请参考 `sample/Android/src` 目录中的 `OgUnityApplication.java` 在自定义 `Application` 的 `onCreate()` 函数中调用初始化方法 `OpenInstall.initialize(this);`。

## 使用指南

使用 `OpenInstall` 之前，请先导入命名空间
``` c
using io.openinstall.unity;
```
然后通过 `GameObject` 获取 `OpenInstall` 实例
``` c
private OpenInstall openinstall;
void Start () {
    openinstall = GameObject.Find("OpenInstall").GetComponent<OpenInstall>();
}
```
### 1 初始化
确保用户同意《隐私政策》之后，再初始化 openinstall。参考 [应用合规指南](https://www.openinstall.io/doc/rules.html)
```
openinstall.Init();
```
> **注意：** `openinstall.Init(permission);` 接口已移除，请使用新的初始化接口

### 2 快速安装和一键拉起

完成文档前面iOS和Android介绍的一键拉起相关配置

在 `Start` 方法中，获取到实例并初始化之后注册拉起回调，并在回调中获取拉起数据
``` c
openinstall.RegisterWakeupHandler(getWakeupFinish);
```
``` c
public void getWakeupFinish(OpenInstallData wakeupData)
{
    Debug.Log("OpenInstallUnity getWakeupFinish : 渠道编号=" +wakeupData.channelCode 
            + "， 自定义数据=" + wakeupData.bindData);
}
```

### 3 携带参数安装（高级版功能）

在应用需要安装参数时，调用以下 api 获取由 SDK 保存的安装参数，可设置超时时长（一般为8～15s），单位秒
``` c
openinstall.GetInstall(10, getInstallFinish);
```
``` c
public void getInstallFinish(OpenInstallData installData)
{
    Debug.Log("OpenInstallUnity getInstallFinish : 渠道编号=" + installData.channelCode 
            + "，自定义数据=" + installData.bindData
			+ "，是否需要重试=" + installData.shouldRetry);
}
```
_备注_：  
- 注意这个安装参数尽量不要自己保存，在每次需要用到的时候调用该方法去获取，因为如果获取成功sdk会保存在本地  
- 该方法可重复获取参数，如需只要在首次安装时获取，可设置标记，详细说明可参考openinstall官网的常见问题

### 4 渠道统计（高级版功能）

SDK 会自动完成访问量、点击量、安装量、活跃量、留存率等统计工作。其它业务相关统计由开发人员使用 api 上报

#### 4.1 注册上报
根据自身的业务规则，在确保用户完成 app 注册的情况下调用 api
``` c
openinstall.ReportRegister();
```
#### 4.2 效果点上报
统计终端用户对某些特殊业务的使用效果，如充值金额，分享次数等等。  
请在 [openinstall 控制台](https://developer.openinstall.io/) 的 “效果点管理” 中添加对应的效果点  
![创建效果点](https://res.cdn.openinstall.io/doc/effect_point.png)  
调用接口进行效果点的上报，第一个参数对应控制台中的 **效果点ID**  
``` c
openinstall.ReportEffectPoint("effect_test", 1);
```

#### 4.3 效果点明细统计
请在 [openinstall 控制台](https://developer.openinstall.io/) 的 “效果点管理” 中添加对应的效果点，并启用“记录明细”，添加自定义参数  
``` c
Dictionary<string,string> extraDict = new Dictionary<string, string>();
extraDict.Add("k1", "v1");
extraDict.Add("k2", "v2");
openinstall.ReportEffectPoint("effect_detail", 1, extraDict);
```

### 5 裂变分享（高级版功能）
分享上报主要是统计某个具体用户在某次分享中，分享给了哪个平台，再通过JS端绑定被分享的用户信息，进一步统计到被分享用户的激活回流等情况。
``` c
openinstall.ReportShare("123456", "QQ", reportFinish);
```
可根据返回的数据中的`shouldRetry`决定是否需要重试，以及`message`查看失败的原因
``` c
public void reportFinish(OpenInstallData shareData)
{
	Debug.Log("OpenInstallUnity reportFinish : shouldRetry=" + shareData.shouldRetry);
}
```

## 导出apk/ipa包并上传

代码集成完毕后，需要导出安装包上传openinstall后台，openinstall会自动完成所有的应用配置工作。  
![上传安装包](https://res.cdn.openinstall.io/doc/upload-ipa-jump.png)  

上传完成后即可开始在线模拟测试，体验完整的App安装/拉起流程；待测试无误后，再完善下载配置信息。  
![在线测试](https://res.cdn.openinstall.io/doc/js-test.png)
