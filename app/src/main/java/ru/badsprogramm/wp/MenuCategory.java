package ru.badsprogramm.wp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

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
    Elements name, link, png, descrip;
    LinearLayout error;
    NestedScrollView def;
    CardView back, reconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_category);

        error = (LinearLayout) findViewById(R.id.error);
        def = (NestedScrollView) findViewById(R.id.def);
        back = (CardView) findViewById(R.id.back);
        reconnect = (CardView) findViewById(R.id.reconnect);

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(700);
        rv.setItemAnimator(itemAnimator);

        adapter = new RVAcategory(category, getApplicationContext());

        new Connect().execute();
    }

    public  void btnBack(View v){
        onBackPressed();
    }

    public void btnConnect(View v) {
        new Connect().execute();
    }

    public class Connect extends AsyncTask<String, Void, String> {

        ProgressDialog dialog = new ProgressDialog(MenuCategory.this);
        private boolean exception = false;
        boolean b;

        @Override
        protected void onPreExecute() {
            if(!b){dialog.setMessage("Загрузка...");dialog.show();dialog.setCancelable(false);}
            b = true;
        }

        protected String doInBackground(String... arg) {
            Document doc;

            try {
                doc = Jsoup.connect("http://kumdang.ru/wp-categories.html")
                        .get();

                name = doc.select("div#category");
                link = doc.select("div#link");
                png = doc.select("div#png");
                descrip = doc.select("div#descrip");

                int i = 0;
                for (Element now : name) {
                    category.add(new Category(name.get(i).text(), link.get(i).text(), png.get(i).text(), descrip.get(i).text()));
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
                error.setVisibility(View.VISIBLE);
                def.setVisibility(View.GONE);
            }
            else {
                error.setVisibility(View.GONE);
                def.setVisibility(View.VISIBLE);
                rv.setAdapter(adapter);
            }
            dialog.dismiss();
        }

    }
}
