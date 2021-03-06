package net.melove.app.chat.application;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tendcloud.tenddata.TCAgent;

import net.melove.app.chat.util.MLLog;

/**
 * Created by lzan13 on 2015/8/10
 * MLApplication类，项目的入口，做一些初始化操作， 这里继承自MultiDex的Application，解决项目方法数超过65536问题
 */
public class MLApplication extends MultiDexApplication {

    // 全局的上下文对象
    private static Context context;

    // 内存溢出检测观察者
    private static RefWatcher watcher;


    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        // 调用自定义初始化方法,封装在 MLHyphenate 类中
        MLHyphenate.getInstance().initHyphenate(context);

        // 调用 TalkingData 初始化统计平台代码
        initTalkingData();

        // 初始化 LeakCanary
        watcher = LeakCanary.install(this);

    }

    public static Context getContext() {
        return context;
    }


    public static RefWatcher getRefWatcher() {
        return watcher;
    }


    /**
     * TalkingData 统计平台初始化
     */
    private void initTalkingData() {
        TCAgent.LOG_ON = true;
        String channel = "";
        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            channel = ai.metaData.getString("TD_CHANNEL_ID", "dev");
            MLLog.i("channel %s", channel);
        } catch (NullPointerException e) {
            MLLog.e("channel %s, %s", channel, e.getMessage());
        } catch (PackageManager.NameNotFoundException e) {
            MLLog.e("channel %s, %s", channel, e.getMessage());
        }
        // 初始化 TalkingData
        TCAgent.init(this, MLConstants.TD_APP_ID, channel);
        // 是否启用 TalkingData 的错误报告
        TCAgent.setReportUncaughtExceptions(true);
    }

}
