package com.example.cait.lagrand_pset6;

import java.util.ArrayList;

/**
 * Drinking Buddies
 * Caitlin Lagrand (10759972)
 * Native App Studio Assignment 6
 *
 * The Drink object contains all the information about a drink, which can be set and obtained
 * using the get and set methods.
 */

class Drink {

    private int id;
    private String name;
    private String category;
    private String alcoholic;
    private String glass;
    private String instructions;
    private String img;
    private ArrayList<String> ingredients;
    private ArrayList<String> measures;
    private boolean fav;

    /**
     * Constructor with no arguments needed for firebase.
     */
    Drink() { }

    /**
     * Constructs a Drink object by setting the information.
     */
    Drink(int id, String name, String category, String alcoholic, String glass, String instructions,
          String img, ArrayList<String> ingredients, ArrayList<String> measures, boolean fav) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.alcoholic = alcoholic;
        this.glass = glass;
        this.instructions = instructions;
        this.img = img;
        this.ingredients = ingredients;
        this.measures = measures;
        this.fav = fav;
    }

    /**
     * The get methods.
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    String getCategory() {
        return category;
    }

    String getAlcoholic() {
        return alcoholic;
    }

    String getGlass() {
        return glass;
    }

    String getInstructions() {
        return instructions;
    }

    String getImg() {
        return img;
    }

    ArrayList<String> getIngredients() {
        return ingredients;
    }

    ArrayList<String> getMeasures() {
        return measures;
    }

    boolean getFav() {
        return fav;
    }

    /**
     * The set methods.
     */
    public void setFav(boolean fav) {
        this.fav = fav;
    }
}
