package ru.badsprogramm.wp;


public class Category {
    String name, link;

    public Category(String name, String link){
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
