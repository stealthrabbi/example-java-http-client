//import org.apache.http.client.HttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpClientDemo {

    private static String TEST_URL = "https://gorest.co.in/public/v2/users";

    public void go() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1000, TimeUnit.MILLISECONDS)
                .writeTimeout(1000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(TEST_URL)
                .get()
                .build();

        // https://www.baeldung.com/okhttp-json-response
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response)
                    throws IOException {

                var objectMapper = new ObjectMapper();
                var responseBody = response.body();
                var users = objectMapper.readValue(responseBody.string(), User[].class);

                System.out.println("Response code:" + response.code());
                for (var user: users) {
                    System.out.println("User: " + user);
                }
            }

            public void onFailure(Call call, IOException e) {
                // error
                System.err.println("exception getting data: " + e);
            }
        });
    }
}
