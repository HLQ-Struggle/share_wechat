package com.hlq.struggle.utils;

import java.security.MessageDigest;

/**
 * @author：HLQ_Struggle
 * @date：2020/6/27
 * @desc：
 */
@SuppressWarnings("ALL")
public class MMessageUtils {

    public static String getMessageDigest(byte[] paramArrayOfbyte) {
        char[] arrayOfChar = new char[16];
        arrayOfChar[0] = '0';
        arrayOfChar[1] = '1';
        arrayOfChar[2] = '2';
        arrayOfChar[3] = '3';
        arrayOfChar[4] = '4';
        arrayOfChar[5] = '5';
        arrayOfChar[6] = '6';
        arrayOfChar[7] = '7';
        arrayOfChar[8] = '8';
        arrayOfChar[9] = '9';
        arrayOfChar[10] = 'a';
        arrayOfChar[11] = 'b';
        arrayOfChar[12] = 'c';
        arrayOfChar[13] = 'd';
        arrayOfChar[14] = 'e';
        arrayOfChar[15] = 'f';
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(paramArrayOfbyte);
            paramArrayOfbyte = messageDigest.digest();
            int i = paramArrayOfbyte.length;
            char[] arrayOfChar1 = new char[i * 2];
            byte b = 0;
            int j = 0;
            while (b < i) {
                byte b1 = paramArrayOfbyte[b];
                int k = j + 1;
                arrayOfChar1[j] = (char)arrayOfChar[b1 >>> 4 & 0xF];
                j = k + 1;
                arrayOfChar1[k] = (char)arrayOfChar[b1 & 0xF];
                b++;
            }
            return new String(arrayOfChar1);
        } catch (Exception exception) {
            return null;
        }
    }

    static byte[] signatures(String paramString1, String paramString2) {
        StringBuffer stringBuffer = new StringBuffer();
        if (paramString1 != null)
            stringBuffer.append(paramString1);
        stringBuffer.append(603979778);
        stringBuffer.append(paramString2);
        stringBuffer.append("mMcShCsTr");
        return getMessageDigest(stringBuffer.toString().substring(1, 9).getBytes()).getBytes();
    }

}
