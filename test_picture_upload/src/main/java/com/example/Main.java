package com.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Main {

    static String url = "http://10.0.0.19:8080/sprites/id/452";

    public static void main(String[] args) throws IOException{
        SpriteData sprite = new SpriteData();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            setSpriteData(httpClient, sprite);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(new File("test.jpg"))) {
            fileOutputStream.write(sprite.data);
            System.out.println("Sprite saved to " + "test.jpg");
        }

    }

    private static void setSpriteData(CloseableHttpClient httpClient, SpriteData sprite) throws IOException{
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                byte[] responseBody = EntityUtils.toByteArray(response.getEntity());
                sprite = new SpriteData();
                sprite.setSpriteData(responseBody);
            } else {
                System.err.println("Failed to fetch sprite: " + response.getStatusLine());
            }
        }
    }
}