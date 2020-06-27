import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'app/constants.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Flutter 实现分享微信来源动态'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            GestureDetector(
              onTap: () async {
                _shareWeChatApp(true);
              },
              child: Text(
                '点我分享微信消息会话',
              ),
            ),
            GestureDetector(
              onTap: () async {
                _shareWeChatApp(false);
              },
              child: Padding(
                padding: EdgeInsets.only(top: 30),
                child: Text(
                  '点我分享微信朋友圈',
                ),
              ),
            )
          ],
        ),
      ),
    );
  }

  /// 具体分享微信方式：true：消息会话 false：朋友圈
  /// 提前调取通道验证采用官方 SDK 还是借壳方案
  void _shareWeChatApp(bool isScene) async {
    /// 这里一定注意通道名称俩端一致
    const platform = const MethodChannel(channelName);
    int tempHitNum = 0;
    try {
      tempHitNum = await platform.invokeMethod(checkAppInstalled);
    } catch (e) {
      print(e);
    }
    if (tempHitNum > 0) {
      // 当前设备存在目标宿主 - 开始执行分享
      await platform.invokeMethod(shareWeChat, {
        'isScene': isScene,
        'shareTitle': '我是分享标题',
        'shareDesc': '我是分享内容',
        'shareUrl': 'https://juejin.im/post/5eb847e56fb9a0438e239243',
        'shareThumbnail':
            'https://user-gold-cdn.xitu.io/2018/9/27/16618fef8bbf66fb?imageView2/1/w/180/h/180/q/85/format/webp/interlace/1'
      });
    } else {
      // 当前设备不存在目前宿主
    }
  }
}
