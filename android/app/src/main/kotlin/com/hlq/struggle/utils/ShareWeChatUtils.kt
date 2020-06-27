package com.hlq.struggle.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hlq.struggle.app.appInfoJson
import com.hlq.struggle.bean.AppInfoBean
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage.IMediaObject
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * @author：HLQ_Struggle
 * @date：2020/6/27
 * @desc：
 */
@Suppress("SpellCheckingInspection")
class ShareWeChatUtils {

    companion object {

        /**
         * 解析本地缓存 App 信息
         */
        private fun getLocalAppCache(): ArrayList<AppInfoBean> {
            return Gson().fromJson(
                    appInfoJson,
                    object : TypeToken<ArrayList<AppInfoBean>>() {}.type
            )
        }

        /**
         * 检测用户设备安装 App 信息
         */
        fun checkAppInstalled(context: Context): Int {
            var tempCount = 0
            // 获取本地宿主 App 信息
            val appInfoList = getLocalAppCache()
            // 获取用户设备已安装 App 信息
            val packageManager = context.packageManager
            val installPackageList = packageManager.getInstalledPackages(0)
            if (installPackageList.isEmpty()) {
                return 0
            }
            for (packageInfo in installPackageList) {
                for (appInfo in appInfoList) {
                    if (packageInfo.packageName == appInfo.packageName) {
                        tempCount++
                    }
                }
            }
            return tempCount
        }

        /**
         * 命中已安装 App
         */
        private fun hitInstalledApp(context: Context): AppInfoBean? {
            // 获取本地宿主 App 信息
            val appInfoList = getLocalAppCache()
            // 获取用户设备已安装 App 信息
            val packageManager = context.packageManager
            // 能进入方法说明本地已存在命中 App，使用时还需要预防
            val installPackageList = packageManager.getInstalledPackages(0)
            for (packageInfo in installPackageList) {
                for (appInfo in appInfoList) {
                    if (packageInfo.packageName == appInfo.packageName) {
                        return appInfo
                    }
                }
            }
            return null
        }

        /**
         * 分享微信
         */
        fun shareWeChat(
                context: Context,
                shareType: Int,
                url: String,
                title: String,
                text: String,
                paramString4: String?,
                umId: String?
        ) {
            Glide.with(context).asBitmap().load(paramString4)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                                param1GlideException: GlideException?,
                                param1Object: Any,
                                param1Target: Target<Bitmap?>,
                                param1Boolean: Boolean
                        ): Boolean {
                            LogUtils.logE(" ---> Load Image Failed")
                            return false
                        }

                        override fun onResourceReady(
                                param1Bitmap: Bitmap?,
                                param1Object: Any,
                                param1Target: Target<Bitmap?>,
                                param1DataSource: DataSource,
                                param1Boolean: Boolean
                        ): Boolean {
                            LogUtils.logE(" ---> Load Image Ready")
                            val i =
                                    send(
                                            context,
                                            shareType,
                                            url,
                                            title,
                                            text,
                                            param1Bitmap
                                    )
                            val stringBuilder = StringBuilder()
                            stringBuilder.append("send index: ")
                            stringBuilder.append(i)
                            LogUtils.logE(" ---> Ready stringBuilder.toString() :$stringBuilder")
                            return false
                        }
                    }).preload(200, 200)
        }

        private fun send(
                paramContext: Context,
                paramInt: Int,
                paramString1: String,
                paramString2: String,
                paramString3: String,
                paramBitmap: Bitmap?
        ): Int {
            val stringBuilder = StringBuilder()
            stringBuilder.append("share url: ")
            stringBuilder.append(paramString1)
            LogUtils.logE(" ---> send :$stringBuilder")
            val wXWebpageObject = WXWebpageObject()
            wXWebpageObject.webpageUrl = paramString1
            val wXMediaMessage = WXMediaMessage(wXWebpageObject as IMediaObject)
            wXMediaMessage.title = paramString2
            wXMediaMessage.description = paramString3
            wXMediaMessage.thumbData =
                    bmpToByteArray(
                            paramContext,
                            Bitmap.createScaledBitmap(paramBitmap!!, 150, 150, true),
                            true
                    )
            val req = SendMessageToWX.Req()
            req.transaction =
                    buildTransaction(
                            "webpage"
                    )
            req.message = wXMediaMessage
            req.scene = paramInt
            val bundle = Bundle()
            req.toBundle(bundle)
            return sendToWx(
                    paramContext,
                    "weixin://sendreq?appid=wxd930ea5d5a258f4f",
                    bundle
            )
        }

        private fun buildTransaction(paramString: String): String {
            var paramString: String? = paramString
            paramString = if (paramString == null) {
                System.currentTimeMillis().toString()
            } else {
                val stringBuilder = StringBuilder()
                stringBuilder.append(paramString)
                stringBuilder.append(System.currentTimeMillis())
                stringBuilder.toString()
            }
            return paramString
        }

        private fun bmpToByteArray(
                paramContext: Context?,
                paramBitmap: Bitmap,
                paramBoolean: Boolean
        ): ByteArray? {
            val byteArrayOutputStream =
                    ByteArrayOutputStream()
            try {
                paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                if (paramBoolean) paramBitmap.recycle()
                val arrayOfByte = byteArrayOutputStream.toByteArray()
                byteArrayOutputStream.close()
                return arrayOfByte
            } catch (iOException: IOException) {
                iOException.printStackTrace()
            }
            return null
        }

        private fun sendToWx(
                paramContext: Context?,
                paramString: String?,
                paramBundle: Bundle?
        ): Int {
            return send(
                    paramContext,
                    "com.tencent.mm",
                    "com.tencent.mm.plugin.base.stub.WXEntryActivity",
                    paramString,
                    paramBundle
            )
        }

        private fun send(
                paramContext: Context?,
                packageName: String?,
                className: String?,
                paramString3: String?,
                paramBundle: Bundle?
        ): Int {
            if (paramContext == null || packageName == null || packageName.isEmpty() || className == null || className.isEmpty()) {
                LogUtils.logE(" ---> send fail, invalid arguments")
                return -1
            }
            val appInfoBean = hitInstalledApp(paramContext)
            val intent = Intent()
            intent.setClassName(packageName, className)
            if (paramBundle != null) intent.putExtras(paramBundle)
            intent.putExtra("_mmessage_sdkVersion", 603979778)
            intent.putExtra("_mmessage_appPackage", appInfoBean?.packageName)
            val stringBuilder = StringBuilder()
            stringBuilder.append("weixin://sendreq?appid=")
            stringBuilder.append(appInfoBean?.packageSign)
            intent.putExtra("_mmessage_content", stringBuilder.toString())
            intent.putExtra(
                    "_mmessage_checksum",
                    MMessageUtils.signatures(paramString3, paramContext.packageName)
            )
            intent.addFlags(268435456).addFlags(134217728)
            return try {
                paramContext.startActivity(intent)
                val sb = StringBuilder()
                sb.append("send mm message, intent=")
                sb.append(intent)
                LogUtils.logE(" ---> sb :$sb")
                0
            } catch (exception: Exception) {
                exception.printStackTrace()
                LogUtils.logE(" --->  send fail, target ActivityNotFound")
                -1
            }
        }
    }
}