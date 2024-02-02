// IByteData.aidl
package moe.qiufuluo.shamrock.xposed.ipc.bytedata;

import moe.qiufuluo.shamrock.xposed.ipc.bytedata.IByteDataSign;

interface IByteData {
    IByteDataSign sign(String uin, String data, in byte[] salt);
}