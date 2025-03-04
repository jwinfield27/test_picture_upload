package com.example;

import java.util.Base64;

public class SpriteGsonContainer {
    public int id;
    public String name;
    public String data;

    public byte[] getImageData(){
        return Base64.getDecoder().decode(data);
    }
}
