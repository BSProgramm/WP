package ru.badsprogramm.wp;


public class Category {
    String name, link, png, descrip;

    public Category(String name, String link, String png, String descrip){
        this.name = name;
        this.link = link;
        this.png = png;
        this.descrip = descrip;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getPng() { return png; }

    public String getDescrip() { return descrip; }
}
