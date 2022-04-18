package org.stealthrabbi.httpclientdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.stealthrabbi.httpclientdemo.domain.User;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HttpClientDemo {

    private static String API_HOST = "";
    private static URL TEST_GET_URL;
    private static URL TEST_POST_URL;
    private static String PROTOCOL = "";


    // TODO - change this value with an API token you acquire yourself.
    private static String API_TOKEN = "665ead9f8fc17ae49e0fd2358f2ae5110b010f6d5460866c5488b22a5a0ac29c";

    private static String USER_NAME = "Wilford Brimley";
    private ObjectMapper objectMapper = new ObjectMapper();

    // TODO add check for response code
    // add query string

    public HttpClientDemo(String protocol, String baseUrl) throws MalformedURLException {
        API_HOST = baseUrl;
        PROTOCOL = protocol;

        TEST_GET_URL = new URL(PROTOCOL + "://" +  baseUrl + "/public/v2/users");
        TEST_POST_URL = new URL(PROTOCOL + "://" +  baseUrl + "/public/v2/users");
    }

    public HttpClientDemo(URL baseUri) throws MalformedURLException {
        TEST_GET_URL = new URL(baseUri.toString() + "/public/v2/users");
        TEST_POST_URL = new URL(baseUri.toString() + "/public/v2/users");
        API_HOST = TEST_GET_URL.getHost();
        PROTOCOL = TEST_GET_URL.getProtocol();
    }

    public List<User> getUserList() {
        OkHttpClient client = getClient();

        Request request = new Request.Builder()
                .url(TEST_GET_URL)
                .get()
                .build();

        // https://www.baeldung.com/okhttp-json-response
        Call call = client.newCall(request);
        try {
            var response = call.execute();
            System.out.println("Success?: " + response.isSuccessful());
            if (response.isSuccessful()) {
                return printUsersFromResponse(response);
            }
        } catch (IOException e) {
            System.err.println("exception getting data: " + e);

        }
        return null;
    }

    public void createUser() throws JsonProcessingException {
        OkHttpClient client = getClient();

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
        try {
            var response = call.execute();
            System.out.println("got response: " + response.code());
            System.out.println("Success?: " + response.isSuccessful());
        } catch (IOException e) {
            System.err.println("exception posting data: " + e);
        }
    }

    public void callGetWithQueryParameter() {
        HttpUrl httpurl = HttpUrl.parse(TEST_GET_URL.toString()).newBuilder()
                .addQueryParameter("name", USER_NAME)
                .build();

        Request request = new Request.Builder()
                .url(httpurl.url())
                .header("Authorization", "Bearer " + API_TOKEN)
                .build();

        Call call = this.getClient().newCall(request);
        try {
            var response = call.execute();
            System.out.println("got get-with-query response: " + response.code());
            System.out.println("Success?: " + response.isSuccessful());
            if (response.isSuccessful()) {
                printUsersFromResponse(response);
            }
        } catch (IOException e) {
            System.err.println("exception posting data: " + e);
        }
    }

    private List<User> getUsersFromResponse(Response response) throws IOException {
        var responseBody = response.body();
        System.out.println("Response code:" + response.code());
        if (response.isSuccessful()) {
            var users = objectMapper.readValue(responseBody.string(), User[].class);
            return List.of(users);
        }
        return Collections.emptyList();
    }

    private List<User> printUsersFromResponse(Response response) throws IOException {
        var users = this.getUsersFromResponse(response);
        System.out.println("Response code:" + response.code());
        for (var user : users) {
            System.out.println("User: " + user);
        }
        return users;
    }

    @NotNull
    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1000, TimeUnit.MILLISECONDS)
                .writeTimeout(1000, TimeUnit.MILLISECONDS)
                .build();
        return client;
    }

}
