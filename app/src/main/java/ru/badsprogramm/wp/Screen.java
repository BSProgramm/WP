package ru.badsprogramm.wp;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Screen extends AppCompatActivity implements View.OnClickListener{

    Elements obj;
    List<Card> cards = new ArrayList<>();
    ImageLoader imageLoader;
    DisplayImageOptions options;
    TextView tv,stats,tv2,stats2, score;
    RelativeLayout min, max;
    ImageView img,img2;
    Animation animation;
    int[] numbers;
    int i = 0;
    int parse = 0;
    LoadSub load;
    ProgressDialog status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_screen);

        tv = (TextView) findViewById(R.id.text);
        tv2 = (TextView) findViewById(R.id.text2);
        min = (RelativeLayout) findViewById(R.id.min);
        max = (RelativeLayout) findViewById(R.id.max);
        stats = (TextView) findViewById(R.id.stats);
        stats2 = (TextView) findViewById(R.id.stats2);
        img = (ImageView) findViewById(R.id.img);
        img2 = (ImageView) findViewById(R.id.img2);
        score = (TextView) findViewById(R.id.score);
        animation = AnimationUtils.loadAnimation(this, R.anim.bounce_scrore);

        min.setOnClickListener(this);
        max.setOnClickListener(this);

        UIL();
        load = new LoadSub();
        status = new ProgressDialog(Screen.this);
        status.setMessage("Загрузка...");
        status.setCancelable(false);
        new Connect().execute();
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.min:
                    if(Integer.parseInt(cards.get(numbers[i]).getStats().replaceAll(" ", "")) > Integer.parseInt(cards.get(numbers[i+1]).getStats().replaceAll(" ",""))){
                        i++;
                        score.setText(String.valueOf(i));
                        score.startAnimation(animation);
                        max.setEnabled(true);
                    }
                    else min.setEnabled(false);
                    break;
                case R.id.max:
                    if(Integer.parseInt(cards.get(numbers[i]).getStats().replaceAll(" ", "")) < Integer.parseInt(cards.get(numbers[i+1]).getStats().replaceAll(" ",""))){
                        i++;
                        score.setText(String.valueOf(i));
                        score.startAnimation(animation);
                        min.setEnabled(true);
                    }
                    else max.setEnabled(false);
                    break;
            }
        }
        catch (NumberFormatException nfe){
            //Toast.makeText(getBaseContext(), "Integer ne sparsilsya", Toast.LENGTH_SHORT).show();
            status.show();
        }

        if (load.getStatus() != AsyncTask.Status.RUNNING){
            load = null;
            load = new LoadSub();
            load.execute();
        }

        if (i < numbers.length - 1) setCards(i);
        else super.onBackPressed();
    }

    public class Connect extends AsyncTask<String, Void, String> {

        ProgressDialog dialog = new ProgressDialog(Screen.this);
        private boolean exception = false;
        boolean b;
        int count = 0;

        @Override
        protected void onPreExecute() {
            if(!b){dialog.setMessage("Загрузка...");dialog.show();dialog.setCancelable(false);}
            b=true;
        }

        protected String doInBackground(String... arg) {
            Document doc;

            try {
                doc = Jsoup.connect("http://kumdang.ru/wp.html")
                        .get();

                obj = doc.select("div#object");

                for(Element now : obj){
                    cards.add(new Card(obj.get(count).text().split(" ")[0], obj.get(count).text().split(" ")[1], now.text().split(" ")[2]));
                    count++;
                }

                //Random
                numbers = new int[count];
                for (int i = 0; i < count; i++){
                    int k = new Random().nextInt(count);
                    while (numbers[k] != 0){
                        k = new Random().nextInt(count);
                    }
                    numbers[k] = i;
                }

                for (int i = parse; i < parse + 6; i++){
                    if (cards.get(numbers[i]).getStats().contains("youtube")){
                        doc = Jsoup.connect(cards.get(numbers[i]).getStats())
                                .userAgent("Chrome/32.0.1667.0")
                                .get();
                        cards.get(numbers[i]).setStats(doc.select("div.primary-header-actions>span>span").get(0).text());
                    }
                }

                parse = 6;

            } catch (IOException e) {
                exception = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (exception){
                //Toast.makeText(getBaseContext(), "Bratan, tut oshibka", Toast.LENGTH_SHORT).show();
            }
            else {
                setCards(0);
            }
            dialog.dismiss();
        }

    }

    public class LoadSub extends AsyncTask<String, Void, String>{

        boolean exception = false;
        boolean ie = false;

        @Override
        protected void onPreExecute() {
            if (i >= parse - 2) status.show();
        }

        protected String doInBackground(String... arg) {
            Document doc;

            try {
                for (int i = parse; i < parse + 4; i++){
                    if (cards.get(numbers[i]).getStats().contains("youtube")){
                        doc = Jsoup.connect(cards.get(numbers[i]).getStats())
                                .userAgent("Chrome/32.0.1667.0")
                                .get();
                        cards.get(numbers[i]).setStats(doc.select("div.primary-header-actions>span>span").get(0).text());
                    }
                }
                parse = parse + 4;
            }
            catch (IndexOutOfBoundsException i){
                ie = true;
            }
            catch (Exception e){
                exception = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            status.dismiss();
            if (exception){
                //Toast.makeText(getBaseContext(), "Ne zagruzil", Toast.LENGTH_SHORT).show();
            }
            else {
                if (ie){
                    //Toast.makeText(getBaseContext(), "Vse Zagruzil", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(getBaseContext(), "Zagruzil", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setCards(int i){
        tv.setText(cards.get(numbers[i]).getName());
        imageLoader.displayImage(cards.get(numbers[i]).getImg(), img, options);
        stats.setText(cards.get(numbers[i]).getStats());

        tv2.setText(cards.get(numbers[i+1]).getName());
        imageLoader.displayImage(cards.get(numbers[i+1]).getImg(), img2, options);
        //stats2.setText(cards.get(numbers[i+1]).getStats());
    }

    private void UIL(){
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        imageLoader = ImageLoader.getInstance();
        File Dir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(Dir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .imageDecoder(new BaseImageDecoder(true)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();

        imageLoader.init(config);
    }
}
