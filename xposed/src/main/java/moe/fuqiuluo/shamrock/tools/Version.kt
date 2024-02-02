package moe.qiufuluo.shamrock.tools

import mqq.app.MobileQQ

private val context = MobileQQ.getContext()
private val packageManager = context.packageManager

private fun getPackageInfo(packageName: String) = packageManager.getPackageInfo(packageName, 0)

val ShamrockVersion: String = getPackageInfo("moe.qiufuluo.shamrock.hided").versionName
