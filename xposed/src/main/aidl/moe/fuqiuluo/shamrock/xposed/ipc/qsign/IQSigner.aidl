// IQSigner.aidl
package moe.qiufuluo.shamrock.xposed.ipc.qsign;

import moe.qiufuluo.shamrock.xposed.ipc.qsign.IQSign;

interface IQSigner {
    IQSign sign(String cmd, int seq, String uin, in byte[] buffer);

    byte[] energy(String module, in byte[] salt);

    byte[] xwDebugId(String uin, String start, String end);

    List<String> getCmdWhiteList();
}