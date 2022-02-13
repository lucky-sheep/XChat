package cn.tim.xchat.network;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

//Log拦截器代码
public class LogInterceptor implements Interceptor {
    private final static String TAG = "okhttp";
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        //log 信息
        StringBuilder builder = new StringBuilder();
        Request request = chain.request();

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        String requestStartMessage = "Method ----> " + request.method() + "\nAddress: "
                + request.url() + "\nHttp Version:" + protocol;

        builder.append(requestStartMessage);

        if (hasRequestBody) {
            if (requestBody.contentType() != null) {
                builder.append("\nHeader ----> \nContent-Type: ").append(requestBody.contentType());
            }
        }
        builder.append("\nRequest parameters ----> ");
        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                builder.append(name).append(": ").append(headers.value(i));
            }
        }

        if (!hasRequestBody) {
            builder.append("\nRequestEnd --> END ").append(request.method());
        } else if (bodyEncoded(request.headers())) {
            builder.append("\nRequestEnd --> END ").append(request.method())
                    .append(" (encoded body omitted)");
        } else {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                if(charset == null) charset = UTF8;
                builder.append(buffer.readString(charset));
                builder.append("\nRequestEnd --> END ").append(request.method()).append(" (")
                        .append(requestBody.contentLength()).append("-byte body)");
            } else {
                builder.append("\nRequestEnd --> END ").append(request.method()).append(" (binary ")
                        .append(requestBody.contentLength()).append("-byte body omitted)");
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            builder.append("\nRequest Error ----> ").append(e.getMessage());
            Log.e(TAG, "Request Info:\n" + builder);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        Headers responseHeaders = response.request().headers();
        builder.append("\nHeader ----> \n").append(responseHeaders.toString());
        if (requestBody != null) {
            assert responseBody != null;
            long contentLength = responseBody.contentLength();
            builder.append("code ----> ").append(response.code()).append(" 用时:(").append(tookMs).append("ms)");

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    builder.append("\nCouldn't decode the response body; charset is likely malformed.");
                    builder.append("\n<-- END HTTP");

                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
                builder.append("\n<-- END HTTP (binary ").append(buffer.size()).append("-byte body omitted)");
                return response;
            }

            if (contentLength != 0) {
                builder.append("\nGet data ---->");
                if(charset == null) charset = UTF8;
                builder.append("\n").append(buffer.clone().readString(charset));
            }
            builder.append("\n<-- Response END HTTP (").append(buffer.size()).append("-byte body)");
        }
        Log.d(TAG, "response:\n" + builder);
        return response;
    }

    private boolean isPlaintext(Buffer buffer){
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            // Truncated UTF-8 sequence.
            return false;
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}