package nathanielwendt.mpc.ut.edu.iotinfluence.comm;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by nathanielwendt on 3/8/16.
 */
public abstract class APIRequest {
    private RequestParams reqParams = new RequestParams();
    private AsyncHttpClient client;

    public APIRequest(boolean async){
        if(async){
            client = new AsyncHttpClient();
        } else {
            client = new SyncHttpClient();
        }
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), reqParams, responseHandler);
    }

    public void post(String url, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), reqParams, responseHandler);
    }

    public void put(String url, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), reqParams, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return getBaseUrl() + relativeUrl;
    }

    public abstract String getBaseUrl();

    public void param(String key, Object value){
        reqParams.put(key, value);
    }

    public void header(String key, String value){
        client.addHeader(key, value);
    }
}
