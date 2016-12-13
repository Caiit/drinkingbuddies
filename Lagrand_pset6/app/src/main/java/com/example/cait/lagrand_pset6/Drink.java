package com.example.cait.lagrand_pset6;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

public class Drink {

    private int id;
    private String name;
    private String category;
    private String alcoholic;
    private String glass;
    private String instructions;
    private String img;
    private ArrayList<String> ingredients;
    private ArrayList<String> measures;
    private String dateModified;
    private String bitImg;
    private boolean fav;

    // Needed for firebase
    public Drink() { }

    public Drink(int id, String name, String category, String alcoholic, String glass, String instructions,
                 String img, ArrayList<String> ingredients, ArrayList<String> measures,
                 String dateModified, boolean fav) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.alcoholic = alcoholic;
        this.glass = glass;
        this.instructions = instructions;
        this.img = img;
        this.ingredients = ingredients;
        this.measures = measures;
        this.dateModified = dateModified;
        this.fav = fav;
    }

    /********************
     * The get methods. *
     ********************/

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAlcoholic() {
        return alcoholic;
    }

    public String getGlass() {
        return glass;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getImg() {
        return img;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getMeasures() {
        return measures;
    }

    public String getDateModified() {
        return dateModified;
    }

    public String getBitImg() {
        return bitImg;
    }

    public boolean getFav() {
        return fav;
    }

    /********************
     * The set methods. *
     ********************/

    public void setBitImg(String bitImg) {
        this.bitImg = bitImg;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
