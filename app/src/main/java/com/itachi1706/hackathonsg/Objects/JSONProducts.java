package com.itachi1706.hackathonsg.Objects;

/**
 * Created by Kenneth on 25/7/2015
 * for Hackathon@SG in package com.itachi1706.hackathonsg.JSONObjects
 */
public class JSONProducts {

    public int id;
    public String title;
    public double retailprice,offerprice;
    public String image;
    public int stock;

    public JSONProducts(int id, String title, double retailprice, double offerprice, String image, int stock) {
        this.id = id;
        this.title = title;
        this.retailprice = retailprice;
        this.offerprice = offerprice;
        this.image = image;
        this.stock = stock;
    }

    public JSONProducts(int id, String title, double retailprice, double offerprice, int stock) {
        this.id = id;
        this.title = title;
        this.retailprice = retailprice;
        this.offerprice = offerprice;
        this.stock = stock;
    }

    public JSONProducts(int id, String title, double retailprice, int stock) {
        this.id = id;
        this.title = title;
        this.retailprice = retailprice;
        this.stock = stock;
        this.offerprice = 0;
    }

    public int getID(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public double getRetailPrice(){
        return this.retailprice;
    }

    public double getOfferPrice(){
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

    public void setRetailprice(double retailprice) {
        this.retailprice = retailprice;
    }

    public void setOfferprice(double offerprice) {
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
}
