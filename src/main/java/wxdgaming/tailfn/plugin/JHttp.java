package wxdgaming.tailfn.plugin;

import wxdgaming.tailfn.IJSPlugin;
import wxdgaming.tailfn.http.HttpClient;

/**
 * http client
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-04 17:39
 **/
public class JHttp implements IJSPlugin {

    @Override public String getName() {
        return "JHttp";
    }

    public String get(String url) {
        return HttpClient.get(url);
    }

    public String postForm(String url, String formData) {
        return HttpClient.postForm(url, formData);
    }

    public String postJson(String url, String json) {
        return HttpClient.postJson(url, json);
    }

}
