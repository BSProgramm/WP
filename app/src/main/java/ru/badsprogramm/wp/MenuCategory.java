package ru.badsprogramm.wp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.badsprogramm.wp.RV.RVAcategory;

public class MenuCategory extends AppCompatActivity {

    RecyclerView rv;
    RVAcategory adapter;
    List<Category> category = new ArrayList<>();
    Elements name, link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv);

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());

        adapter = new RVAcategory(category);

        new Connect().execute();
    }

    public class Connect extends AsyncTask<String, Void, String> {

        ProgressDialog dialog = new ProgressDialog(MenuCategory.this);
        private boolean exception = false;
        boolean b;

        @Override
        protected void onPreExecute() {
            if(!b){dialog.setMessage("Загрузка...");dialog.show();dialog.setCancelable(false);}
            b=true;
        }

        protected String doInBackground(String... arg) {
            Document doc;

            try {
                doc = Jsoup.connect("http://kumdang.ru/wp-categories.html")
                        .get();

                name = doc.select("div#category");
                link = doc.select("div#link");

                int i = 0;
                for (Element now : name) {
                    category.add(new Category(name.get(i).text(), link.get(i).text()));
                    i++;
                }

            } catch (IOException e) {
                exception = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (exception){

            }
            else {
                rv.setAdapter(adapter);
            }
            dialog.dismiss();
        }

    }
}
