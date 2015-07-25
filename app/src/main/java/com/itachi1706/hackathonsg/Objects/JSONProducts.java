package com.itachi1706.hackathonsg.Objects;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.JSONObjects
 */
public class JSONProducts {

    public int id;
    public String title;
    public String retailprice,offerprice;
    public String image;
    public String store;
    public int stock;
    public String barcode;

    public JSONProducts(){}

    public JSONProducts(int id, String title, String retailprice, String offerprice, String image, int stock, String store) {
        this.id = id;
        this.title = title;
        this.retailprice = retailprice;
        this.offerprice = offerprice;
        this.image = image;
        this.stock = stock;
        this.store = store;

    }

    public JSONProducts(int id, String title, String retailprice, String offerprice, int stock, String store) {
        this.id = id;
        this.title = title;
        this.retailprice = retailprice;
        this.offerprice = offerprice;
        this.stock = stock;
        this.store = store;
    }

    public JSONProducts(int id, String title, String retailprice, int stock, String store) {
        this.id = id;
        this.title = title;
        this.retailprice = retailprice;
        this.stock = stock;
        this.offerprice = "";
        this.store = store;
    }

    public int getID(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public String getRetailPrice(){
        return this.retailprice;
    }

    public String getOfferPrice(){
        return this.offerprice;
    }

    public String getImage(){
        return this.image;
    }

    public int getStockInt(){
        return this.stock;
    }

    public boolean getStock(){
        return (this.stock == 1);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRetailprice(String retailprice) {
        this.retailprice = retailprice;
    }

    public void setOfferprice(String offerprice) {
        this.offerprice = offerprice;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
