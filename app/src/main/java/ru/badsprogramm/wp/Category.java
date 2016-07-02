package ru.badsprogramm.wp;


public class Category {
    String name, link, png;

    public Category(String name, String link, String png){
        this.name = name;
        this.link = link;
        this.png = png;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getPng() { return png; }
}
