package com.example.cait.lagrand_pset6;

/**
 * Drinking Buddies
 * Caitlin Lagrand (10759972)
 * Native App Studio Assignment 6
 *
 * The SmallDrink object contains the id, name, img and favourite of a drink,
 * which can be set and obtained using the get and set methods.
 */

class SmallDrink {

    private int id;
    private String name;
    private String img;
    private boolean fav;

    /**
     * Constructor with no arguments needed for firebase.
     */
    SmallDrink() { }

    /**
     * Constructs a Drink object by setting the information.
     */
    SmallDrink(int id, String name, String img, boolean fav) {
        this.id = id;
        this.name = name;
        this.img = img;
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

    public String getImg() {
        return img;
    }

    public boolean getFav() {
        return fav;
    }

    /**
     * The set methods.
     */
    public void setFav(boolean fav) {
        this.fav = fav;
    }

}
