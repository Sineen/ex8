package com.example.ex8;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
class Server {

    final String LOG_TAG = "CS";
    final String SERVER_URL = "https://hujipostpc2019.pythonanywhere.com";
    private final String TOKEN_URL = "https://hujipostpc2019.pythonanywhere.com/users/$/token/";
    private final String USER_INFO_URL = "https://hujipostpc2019.pythonanywhere.com/user/";
    private final String EDIT_URL = "https://hujipostpc2019.pythonanywhere.com/user/edit/";

    private ClientInfo clientInfo;
    private OkHttpClient httpClient;
    private MainActivity handler;


    public Server(ClientInfo user_info, MainActivity mainActivity) {
        httpClient = new OkHttpClient();
        this.clientInfo = user_info;
        this.handler = mainActivity;
    }

    public void setClientPrettyName(String name) {


        if (name.length() < 1) {
            return;
        }

        MediaType JSON = MediaType.parse("application/json");
        String json = "{ \"pretty_name\" : \"" + name + "\" }";
        RequestBody requestBody = RequestBody.create(JSON, json.getBytes());
        final Request request = new Request.Builder().url(EDIT_URL).post(requestBody).addHeader("Content-Type", "application/json").addHeader("Authorization", "token " + clientInfo.getToken()).build();
        Log.d(LOG_TAG, "setClientPrettyName request string " + request.body().toString());

        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "setClientPrettyName failed");
                Log.d(LOG_TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(LOG_TAG, "setClientPrettyName onResponse");
                JSONObject jsonO, jsonOut;

                try {
                    jsonOut = new JSONObject(response.body().string());
                    jsonO = new JSONObject(jsonOut.optString("data"));
                    clientInfo.setUsername(jsonO.optString("username"));
                    clientInfo.setPretty_name(jsonO.optString("pretty_name"));
                    clientInfo.setImageURL( SERVER_URL + jsonO.optString("imageURL"));
                    Log.d(LOG_TAG, "setClientPrettyName are: " + clientInfo.getToken() + clientInfo.getPretty_name() + clientInfo.getUsername() + clientInfo.getImageURL());
                    handler.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.prettyNameCallback();
                        }
                    });

                } catch (JSONException | IOException exception) {
                    Log.d(LOG_TAG, "setClientPrettyName ERROR " + exception.getMessage());
                }
            }
        });

    }

    public void getToken() {
        Request request = new Request.Builder().url(TOKEN_URL.replace("$", clientInfo.getUsername())).build();

        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "getToken failed");
                Log.d(LOG_TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(LOG_TAG, "getToken onResponse");
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response.body().string());
                    clientInfo.setToken(jsonObject.getString("data"));
                    Log.d(LOG_TAG, "getToken got" + clientInfo.getToken());
                    handler.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.updateClientInfo();
                        }
                    });

                } catch (JSONException ex) {
                    Log.d(LOG_TAG, "getToken Json ERROR");
                }
            }
        });
    }

    public void getClientDetails() {
        Request request = new Request.Builder().url(USER_INFO_URL).addHeader("Authorization", "token " + clientInfo.getToken()).build();

        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(LOG_TAG, "getClientDetails failed");
                Log.d(LOG_TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(LOG_TAG, "getClientDetails onResponse");
                JSONObject jsonObject;
                JSONObject jsonObjectOut;

                try {
                    jsonObjectOut = new JSONObject(response.body().string());
                    jsonObject = new JSONObject(jsonObjectOut.optString("data"));
                    clientInfo.setUsername(jsonObject.optString("username"));
                    clientInfo.setPretty_name(jsonObject.optString("pretty_name"));
                    clientInfo.setImageURL(jsonObject.optString("imageURL"));
                    Log.d(LOG_TAG, "getClientDetails are: " + clientInfo.getToken() + clientInfo.getPretty_name() + clientInfo.getUsername() + clientInfo.getImageURL());
                    handler.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.existedUserCycle();
                        }
                    });

                } catch (JSONException ex) {
                    Log.d(LOG_TAG, "getClientDetails Json ERROR");
                }
            }
        });
    }
}

