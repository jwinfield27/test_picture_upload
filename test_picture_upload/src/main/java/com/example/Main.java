package com.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Main {

    static String single_url = "http://10.0.0.19:8080/sprites/id/452";
    static String multiple_url = "http://10.0.0.19:8080/sprites/contains/test";

    public static void main(String[] args) throws IOException{
        // getSingleImage();
        getMultipleImages();
    }

    private static void setSpriteData(CloseableHttpClient httpClient, SpriteData sprite) throws IOException{
        HttpGet request = new HttpGet(single_url);
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

    private static void getSingleImage() throws IOException {
        SpriteData sprite = new SpriteData();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            setSpriteData(httpClient, sprite);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(new File("test.jpg"))) {
            fileOutputStream.write(sprite.data);
            System.out.println("Sprite saved to " + "test.jpg");
        }
    }

    private static void getMultipleImages() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            saveImageDirectory(setMultipleSpritesData(httpClient));
        }
    }

    private static List<SpriteData> setMultipleSpritesData(CloseableHttpClient httpclient) throws IOException{
        HttpGet request = new HttpGet(multiple_url);
        List<SpriteData> sprites = new ArrayList<SpriteData>();
        try(CloseableHttpResponse response = httpclient.execute(request)){
            if (response.getStatusLine().getStatusCode() == 200){
                String body = EntityUtils.toString(response.getEntity());
                for(String line : body.split("\n")){
                    byte[] image = Base64.getDecoder().decode(line);
                    sprites.add(new SpriteData(image));
                }
            } else {
                System.err.println("Failed to fetch sprite collection at: " + response.getStatusLine());
            }
        }
        return sprites;
    }

    private static void saveImageDirectory(List<SpriteData> sprites) throws IOException {
        int image_num = 1;
        for(SpriteData sprite : sprites){
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File("test.jpg"))) {
                fileOutputStream.write(sprite.data);
                System.out.println("Sprite saved to " + "test-" + String.valueOf(image_num) + ".jpg");
            }
        }
    } 
}