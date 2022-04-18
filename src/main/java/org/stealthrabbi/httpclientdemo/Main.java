package org.stealthrabbi.httpclientdemo;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.MalformedURLException;

public class Main {


    public static void main(String[] args) throws JsonProcessingException, MalformedURLException {
        System.out.println("hello");

        var demo = new HttpClientDemo("https", "gorest.co.in");
        demo.getUserList();
        demo.createUser();
        demo.callGetWithQueryParameter();

        System.out.println("goodbye");
    }
}
