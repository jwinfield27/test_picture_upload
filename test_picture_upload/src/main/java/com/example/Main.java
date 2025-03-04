package com.example;

import java.lang.reflect.Type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

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
            fileOutputStream.write(sprite.sprite_data);
            System.out.println("Sprite saved to " + "test.jpg");
        }
    }

    private static void getMultipleImages() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            List<SpriteGsonContainer> sprites = setMultipleSpritesData(httpClient);
            if(sprites == null){
                System.out.println("could not find any sprites matching subtring");
                System.exit(0);
            }
            saveImageDirectory(sprites);
        }
    }

    private static List<SpriteGsonContainer> setMultipleSpritesData(CloseableHttpClient httpclient) throws IOException{
        List<SpriteGsonContainer> sprites = null;
        HttpGet request = new HttpGet(multiple_url);
        try(CloseableHttpResponse response = httpclient.execute(request)){
            if (response.getStatusLine().getStatusCode() == 200){
                String body = EntityUtils.toString(response.getEntity());
                sprites = parseForDataFields(body);
            } else {
                System.err.println("Failed to fetch sprite collection at: " + response.getStatusLine());
            }
        }
        return sprites;
    }

    private static void saveImageDirectory(List<SpriteGsonContainer> sprites) throws IOException {
        for(SpriteGsonContainer sprite : sprites){
            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(sprite.name+".jpg"))) {
                fileOutputStream.write(sprite.getImageData());
                System.out.println("Sprite saved to " + sprite.name + ".jpg");
            }
        }
    }

    private static List<SpriteGsonContainer> parseForDataFields(String data){
        Type spriteListType = new TypeToken<ArrayList<SpriteGsonContainer>>(){}.getType();
        List<SpriteGsonContainer> sprites = new Gson().fromJson(data, spriteListType);
        return sprites;
    }
}