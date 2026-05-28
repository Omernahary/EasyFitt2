package omer.nahary.easyfitt;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import android.app.Activity;

public abstract class AIHELPER {

    static OkHttpClient client = new OkHttpClient();
    public static final String OPENAI_API_KEY = "NA UH";


    public static void runAIModel(Activity a, String prompt, Listener listener) {//פונקצייה הזאת אחראית על להריץ את המודל של OPENAI עם הPROMPT שניתן ואז להחזיר את התשובה של המודל דרך הLISTENER
        try {
            JSONObject body = new JSONObject();
            body.put("model", "gpt-4.1-mini");

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", prompt));
            body.put("messages", messages);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    a.runOnUiThread(() -> listener.onFailure(e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resBody = response.body() != null ? response.body().string() : "";
                    try {
                        JSONObject json = new JSONObject(resBody);
                        if (json.has("error")) {
                            String errMsg = json.getJSONObject("error").optString("message", "Unknown AI error");
                            a.runOnUiThread(() -> listener.onFailure(errMsg));
                            return;
                        }
                        String aiText = json.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        a.runOnUiThread(() -> listener.onSuccess(aiText.trim()));
                    } catch (Exception e) {
                        a.runOnUiThread(() -> listener.onFailure("Parse error: " + resBody));
                    }
                }
            });
        } catch (Exception e) {
            listener.onFailure("AI setup error: " + e.getMessage());
        }
    }
}
