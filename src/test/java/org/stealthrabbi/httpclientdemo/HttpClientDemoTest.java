package org.stealthrabbi.httpclientdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import org.junit.Assert;
import org.stealthrabbi.httpclientdemo.domain.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class HttpClientDemoTest {

    private MockWebServer mockWebServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createServer() {
        mockWebServer = new MockWebServer();
    }

    @After
    public void cleanup() throws IOException {
        if (this.mockWebServer != null) {
            this.mockWebServer.shutdown();
        }
    }

    @Test
    public void getUsersSuccess() throws IOException {
        var userList = new ArrayList<User>();
        userList.add(new User(5, "test1", "test@email.org", "male", "active"));
        userList.add(new User(6, "test2", "test2@email.org", "female", "active"));
        var userListResponse = mapper.writeValueAsString(userList);

        mockWebServer.enqueue(new MockResponse().setBody(userListResponse));

        mockWebServer.start();
        var testUrl = mockWebServer.url("demo1");

        var demo = buildDemoClient(testUrl);
        var queriedUsers = demo.getUserList();

        Assert.assertEquals(userList.get(0).getEmail(), queriedUsers.get(0).getEmail());
    }

    @Test
    public void getUsersNonSuccess() throws IOException {
        var userList = new ArrayList<User>();
        userList.add(new User(5, "test1", "test@email.org", "male", "active"));
        userList.add(new User(6, "test2", "test2@email.org", "female", "active"));
        var userListResponse = mapper.writeValueAsString(userList);

        mockWebServer.enqueue(new MockResponse().setBody(userListResponse).setResponseCode(400));

        mockWebServer.start();
        var testUrl = mockWebServer.url("demo1");

        var demo = buildDemoClient(testUrl);
        var queriedUsers = demo.getUserList();

        Assert.assertNull(queriedUsers);
    }

    @Test
    public void getUsersIoError() throws IOException {
        mockWebServer.start();
        var testUrl = mockWebServer.url("demo1");

        var demo = buildDemoClient(testUrl);
        var queriedUsers = demo.getUserList();

        Assert.assertNull(queriedUsers);
    }

    // TODO other tests

    private HttpClientDemo buildDemoClient(HttpUrl url) throws MalformedURLException {
        return new HttpClientDemo(url.url());
    }
}
