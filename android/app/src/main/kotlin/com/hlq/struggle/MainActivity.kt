package com.hlq.struggle

import com.hlq.struggle.app.*
import com.hlq.struggle.utils.ShareWeChatUtils.Companion.checkAppInstalled
import com.hlq.struggle.utils.ShareWeChatUtils.Companion.shareWeChat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        // 处理 Flutter 传递过来的消息
        handleMethodChannel(flutterEngine)
    }

    private fun handleMethodChannel(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor, channelName).setMethodCallHandler { methodCall: MethodCall, result: MethodChannel.Result? ->
            when (methodCall.method) {
                checkAppInstalledChannel -> { // 获取命中 App 数量
                    result?.success(checkAppInstalled(activity))
                }
                shareWeChatChannel -> {  // 分享微信
                    val shareType = if (methodCall.argument<Boolean>("isScene")!!) {
                        shareWeChatSession
                    } else {
                        shareWeChatLine
                    }
                    result?.success(shareWeChat(
                            this, shareType,
                            methodCall.argument<String>("shareUrl")!!,
                            methodCall.argument<String>("shareTitle")!!,
                            methodCall.argument<String>("shareDesc")!!,
                            methodCall.argument<String>("shareThumbnail")!!, ""))
                }
                else -> {
                    result?.notImplemented()
                }
            }
        }
    }

}
