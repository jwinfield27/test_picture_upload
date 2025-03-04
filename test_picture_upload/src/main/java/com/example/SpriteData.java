package com.example;

public class SpriteData {
    public byte[] sprite_data;
    public String sprite_name;

    SpriteData(){}

    SpriteData(byte[] data, String sprite_name){
        this.sprite_data = data;
        this.sprite_name = sprite_name;
    }

    public void setSpriteData(byte[] data){
        this.sprite_data = data;
    }
}

