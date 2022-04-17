import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HttpClientDemo {

    private static String API_HOST = "gorest.co.in";
    private static String TEST_GET_URL = "https://gorest.co.in/public/v2/users";
    private static String TEST_POST_URL = "https://gorest.co.in/public/v2/users";
    // TODO - change this value with an API token you acquire yourself.
    private static String API_TOKEN = "changeme";

    private static String USER_NAME = "Wilford Brimley";
    private ObjectMapper objectMapper = new ObjectMapper();

    // TODO add check for response code
    // add query string

    public void runQueries() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1000, TimeUnit.MILLISECONDS)
                .writeTimeout(1000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(TEST_GET_URL)
                .get()
                .build();

        // https://www.baeldung.com/okhttp-json-response
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response)
                    throws IOException {

                System.out.println("Success?: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    printUsersFromResponse(response);
                    sendPostCommand(client);
                }
            }

            public void onFailure(Call call, IOException e) {
                // error
                System.err.println("exception getting data: " + e);
            }
        });
    }



    private void sendPostCommand(OkHttpClient client) throws JsonProcessingException {
        // email must be unique
        var user = new User(99, USER_NAME, UUID.randomUUID() + "@brimley.org", "male", "active");
        var userJson = objectMapper.writeValueAsString(user);
        RequestBody body = RequestBody.create(userJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(TEST_POST_URL)
                .header("Authorization", "Bearer " + API_TOKEN)
                .post(body)
                .build();

        System.out.println("User json: " + userJson );
        System.out.println("Sending Post: " + request.toString());

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("got response: " + response.code());
                System.out.println("Success?: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    callGetWithQueryParameter(client);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println("exception posting data: " + e);
            }
        });
    }

    private void callGetWithQueryParameter(OkHttpClient client) {

        HttpUrl httpurl = new HttpUrl.Builder()
                .scheme("https")
                .host(API_HOST)
                .addPathSegment("public")
                .addPathSegment("v2")
                .addPathSegment("users")
                .addQueryParameter("name", USER_NAME)
                .build();


        Request request = new Request.Builder()
                .url(httpurl.url())
                .header("Authorization", "Bearer " + API_TOKEN)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("got response: " + response.code());
                System.out.println("Success?: " + response.isSuccessful());
                if (response.isSuccessful()) {
                    printUsersFromResponse(response);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println("exception posting data: " + e);
            }
        });
    }

    private void printUsersFromResponse(Response response) throws IOException {
        var responseBody = response.body();
        var users = objectMapper.readValue(responseBody.string(), User[].class);

        if (response.isSuccessful()) {
            System.out.println("**** All wilfords:");
            System.out.println("Response code:" + response.code());
            for (var user : users) {
                System.out.println("User: " + user);
            }
        }
    }
}
