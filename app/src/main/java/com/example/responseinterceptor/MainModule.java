package com.example.responseinterceptor;

import android.util.Log;
import androidx.annotation.NonNull;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam;

public class MainModule extends XposedModule {

    private static final String TAG = "ResponseInterceptor";

    @Override
    public void onPackageReady(@NonNull PackageReadyParam param) {
        // 检查当前加载的是否是我们的目标应用
        if (!param.getPackageName().equals("com.wanlianyida.driver")) {
            return;
        }

        log(Log.INFO, TAG, "Target package loaded, starting hook...");

        try {
            // 初始化并启动OkHttp拦截器
            ResponseInterceptor.init(param.getClassLoader(), this);
        } catch (Throwable t) {
            log(Log.ERROR, TAG, "Failed to initialize interceptor", t);
        }
    }
}
