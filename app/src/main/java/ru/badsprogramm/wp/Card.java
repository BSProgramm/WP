package ru.badsprogramm.wp;


public class Card {

    String name, img, stats;

    public Card(String name, String img, String stats) {
        if(name.contains("_")){
            this.name = name.replaceAll("_", " ");
        }
        else this.name = name;
        this.img = img;
        this.stats = stats;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getStats() {
        return stats;
    }
}
