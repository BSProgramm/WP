package ru.badsprogramm.wp;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    String link;
    Elements obj;
    List<Card> cards = new ArrayList<>();
    ImageLoader imageLoader;
    DisplayImageOptions options;
    TextView tv,stats,tv2,stats2;
    RelativeLayout min, max;
    ImageView img,img2;
    int[] numbers;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_screen);

        link = getIntent().getStringExtra("LINK");

        tv = (TextView) findViewById(R.id.text);
        tv2 = (TextView) findViewById(R.id.text2);
        min = (RelativeLayout) findViewById(R.id.min);
        max = (RelativeLayout) findViewById(R.id.max);
        stats = (TextView) findViewById(R.id.stats);
        stats2 = (TextView) findViewById(R.id.stats2);
        img = (ImageView) findViewById(R.id.img);
        img2 = (ImageView) findViewById(R.id.img2);
        min.setOnClickListener(this);
        max.setOnClickListener(this);

        UIL();
        new Connect().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.min:
                if(Integer.parseInt(cards.get(numbers[i]).getStats()) > Integer.parseInt(cards.get(numbers[i+1]).getStats())){
                    i++;
                }
                else min.setEnabled(false);
            break;
            case R.id.max:
                if(Integer.parseInt(cards.get(numbers[i]).getStats()) < Integer.parseInt(cards.get(numbers[i+1]).getStats())){
                    i++;
                }
                else max.setEnabled(false);
            break;
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
                doc = Jsoup.connect(link)
                        .get();

                obj = doc.select("div#object");

                for(Element now : obj){
                    cards.add(new Card(obj.get(count).text().split(" ")[0], obj.get(count).text().split(" ")[1], obj.get(count).text().split(" ")[2]));
                    count++;
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
                numbers = new int[count];
                for (int i = 0; i < count; i++){
                    int k = new Random().nextInt(count);
                    while (numbers[k] != 0){
                        k = new Random().nextInt(count);
                    }
                    numbers[k] = i;
                }
                setCards(0);
            }
            dialog.dismiss();
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
