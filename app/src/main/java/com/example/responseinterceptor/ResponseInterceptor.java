package com.example.responseinterceptor;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class ResponseInterceptor {

    private static final String TAG = "ResponseInterceptor";

    // 目标URL和要替换的JSON响应
    private static final String TARGET_URL = "https://trade.10000da56.com/gateway/lmt-driver/driver-compliance/common-check";
    private static final String REPLACEMENT_JSON = "{\"model\":{\"passed\":true,\"message\":null,\"code\":null},\"total\":0,\"succeed\":true,\"code\":\"OK-000\",\"message\":\"操作成功\"}";

    private static XposedModule module;

    public static void init(ClassLoader classLoader, XposedModule xposedModule) throws Throwable {
        module = xposedModule;

        // 1. 加载OkHttp的Builder类
        Class<?> builderClass = classLoader.loadClass("okhttp3.OkHttpClient$Builder");

        // 2. Hook Builder的build()方法
        module.hook(builderClass.getDeclaredMethod("build"))
                .intercept(new XposedInterface.Hooker() {
                    @Override
                    public Object intercept(XposedInterface.Chain chain) throws Throwable {
                        // 先执行原始方法，获取OkHttpClient实例
                        Object client = chain.proceed();

                        // 如果是OkHttpClient，则向其中添加我们的拦截器
                        if (client instanceof OkHttpClient) {
                            OkHttpClient okHttpClient = (OkHttpClient) client;

                            // 创建自定义拦截器
                            Interceptor customInterceptor = new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request();
                                    String url = request.url().toString();

                                    // 检查URL是否匹配
                                    if (url.equals(TARGET_URL)) {
                                        Log.i(TAG, "Intercepted target URL: " + url);

                                        // 调用原始请求，获取原始响应
                                        Response originalResponse = chain.proceed(request);

                                        // 构造一个新的响应体，内容为我们的JSON
                                        ResponseBody newBody = ResponseBody.create(
                                                originalResponse.body().contentType(),
                                                REPLACEMENT_JSON
                                        );

                                        // 构造并返回新的Response
                                        return originalResponse.newBuilder()
                                                .body(newBody)
                                                .build();
                                    }

                                    // 不匹配则透传
                                    return chain.proceed(request);
                                }
                            };

                            // 将自定义拦截器添加到OkHttpClient中
                            // 注意：OkHttpClient的拦截器列表是final的，需要通过反射修改
                            try {
                                java.lang.reflect.Field interceptorsField = OkHttpClient.class.getDeclaredField("interceptors");
                                interceptorsField.setAccessible(true);
                                List<Interceptor> interceptors = (List<Interceptor>) interceptorsField.get(okHttpClient);
                                if (interceptors != null) {
                                    interceptors.add(customInterceptor);
                                    Log.i(TAG, "Custom interceptor added successfully.");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Failed to inject interceptor", e);
                            }
                        }

                        return client;
                    }
                });

        Log.i(TAG, "Hook installed successfully.");
    }
}
