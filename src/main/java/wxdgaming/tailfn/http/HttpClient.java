package wxdgaming.tailfn.http;

import wxdgaming.tailfn.GraalvmUtil;
import wxdgaming.tailfn.Throw;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClient {

    public static String get(String url) {
        // 创建HttpClient实例
        try (java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build()) {
            // 构建HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(java.time.Duration.ofSeconds(20))
                    .build();
            // 发送请求并获取HttpResponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 输出响应体
            return response.body();
        } catch (Exception e) {
            GraalvmUtil.appendFile(Throw.ofString(e));
            return e.toString();
        }
    }

    public static String postForm(String url, String formData) {
        return post(url, "application/x-www-form-urlencoded", formData);
    }

    public static String postJson(String url, String json) {
        return post(url, "application/json", json);
    }

    public static String post(String url, String contentType, String jsonPayload) {
        // 创建HttpClient实例
        try (java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build()) {
            // 构建HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", contentType)
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            // 发送请求并获取HttpResponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // 输出响应体
            return response.body();
        } catch (Exception e) {
            GraalvmUtil.appendFile(Throw.ofString(e));
            return e.toString();
        }
    }

}
