package com.example.responseinterceptor;

import android.util.Log;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam;

public class MainModule extends XposedModule {

    private static final String TAG = "ResponseInterceptor";
    private static final String TARGET_PACKAGE = "com.wanlianyida.driver";

    @Override
    public void onPackageReady(PackageReadyParam param) {
        String packageName = param.getPackageName();
        Log.i(TAG, "onPackageReady: " + packageName);

        // 只对目标应用生效
        if (!TARGET_PACKAGE.equals(packageName)) {
            return;
        }

        Log.i(TAG, "Target package loaded, installing OkHttp hook...");
        try {
            ResponseInterceptor.init(param.getClassLoader(), this);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to install hook", t);
        }
    }
}
