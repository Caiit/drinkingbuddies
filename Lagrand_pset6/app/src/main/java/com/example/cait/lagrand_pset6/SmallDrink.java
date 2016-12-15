package com.example.cait.lagrand_pset6;

public class SmallDrink {

    private int id;
    private String name;
    private String img;
    private String bitImg;
    private boolean fav;

    // Needed for firebase
    public SmallDrink() { }

    public SmallDrink(int id, String name, String img, boolean fav) {
        this.id = id;
        this.name = name;
        this.img = img;
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

    public String getImg() {
        return img;
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
