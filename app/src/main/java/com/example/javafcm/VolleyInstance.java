package com.example.javafcm;

import static com.example.javafcm.Constants.CONTENT_TYPE;
import static com.example.javafcm.Constants.SERVER_KEY;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VolleyInstance extends AppCompatActivity {

    IResult mResultCallback;
    Context mContext;
    RequestQueue mQueue;

    public VolleyInstance(IResult resultCallback, Context context) {
        mResultCallback = resultCallback;
        mContext = context;
        mQueue = Volley.newRequestQueue(mContext);
    }

    public void post(String requestType, String url, JSONObject jsonObject, String requestBody) {
        try {
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonObject,
                            response -> {
                                if (mResultCallback != null)
                                    mResultCallback.notifySuccess(requestType, response);
                            },
                            error -> {
                                if (mResultCallback != null)
                                    mResultCallback.notifyError(requestType, error);
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", CONTENT_TYPE);
                            params.put("Authorization", "key=" + SERVER_KEY);
                            return params;
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() {
                            return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
                        }
                    };

            mQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
