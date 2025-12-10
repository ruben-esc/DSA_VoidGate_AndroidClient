package com.example.restclientapp.model;

public class ObjetoCompra {
    private int itemId;
    private int userId;

    public ObjetoCompra(int itemId, int userId) {
        this.itemId = itemId;
        this.userId = userId;
    }
    public int getItemId(){
        return this.itemId;
    }
    public int getUserId(){
        return this.userId;

    }
}