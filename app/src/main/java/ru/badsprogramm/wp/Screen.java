package ru.badsprogramm.wp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Screen extends AppCompatActivity implements View.OnClickListener {

    Elements obj;
    List<Card> cards = new ArrayList<>();
    TextView tv, stats, tv2, stats2, score, nowScore;
    RelativeLayout min, max, screenGame;
    LinearLayout screenScore, shareView;
    ImageView img, img2;
    Animation animation;
    int[] numbers;
    int i = 0;
    int parse = 0;
    LoadSub load;
    ProgressDialog status;
    FloatingActionButton share, re;
    int count = 0;


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
        screenGame = (RelativeLayout) findViewById(R.id.screenGame);
        screenScore = (LinearLayout) findViewById(R.id.screenScore);
        nowScore = (TextView) findViewById(R.id.nowScore);
        share = (FloatingActionButton) findViewById(R.id.share);
        re = (FloatingActionButton) findViewById(R.id.re);
        shareView = (LinearLayout) findViewById(R.id.shareView);
        animation = AnimationUtils.loadAnimation(this, R.anim.bounce_scrore);

        min.setOnClickListener(this);
        max.setOnClickListener(this);
        share.setOnClickListener(this);
        re.setOnClickListener(this);

        status = new ProgressDialog(Screen.this);
        status.setMessage("Загрузка...");
        status.setCancelable(false);
        new Connect().execute();
        load = new LoadSub();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.min:
                    if (i<count)
                    if (Integer.parseInt(cards.get(numbers[i]).getStats().replaceAll(" ", "")) > Integer.parseInt(cards.get(numbers[i + 1]).getStats().replaceAll(" ", ""))) {
                        i++;
                        score.setText(String.valueOf(i));
                        score.startAnimation(animation);
                    } else {
                        screenGame.setVisibility(View.GONE);
                        nowScore.setText(score.getText());
                        screenScore.setVisibility(View.VISIBLE);
                    }
                    else {
                        screenGame.setVisibility(View.GONE);
                        nowScore.setText(score.getText());
                        screenScore.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.max:
                    if (i<count)
                    if (Integer.parseInt(cards.get(numbers[i]).getStats().replaceAll(" ", "")) < Integer.parseInt(cards.get(numbers[i + 1]).getStats().replaceAll(" ", ""))) {
                        i++;
                        score.setText(String.valueOf(i));
                        score.startAnimation(animation);
                    } else {
                        screenGame.setVisibility(View.GONE);
                        nowScore.setText(score.getText());
                        screenScore.setVisibility(View.VISIBLE);
                    }else {
                        screenGame.setVisibility(View.GONE);
                        nowScore.setText(score.getText());
                        screenScore.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.share:
                    Bitmap bm = screenShot(shareView.getRootView());
                    File file = saveBitmap(bm, "bsprgrmm.png");
                    Log.i("chase", "filepath: " + file.getAbsolutePath());
                    Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Мой рекорд" + score.getText() + "в игре -Кто популярнее?- ");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "share"));
                    break;
                case R.id.re:
                    i = 0;
                    parse = 0;
                    count = 0;
                    numbers = new int[0];
                    status = new ProgressDialog(Screen.this);
                    status.setMessage("Загрузка...");
                    status.setCancelable(false);
                    new Connect().execute();
                    new LoadSub().doInBackground();
                    break;
            }
        } catch (NumberFormatException nfe) {
            Toast.makeText(getBaseContext(), "Integer ne sparsilsya", Toast.LENGTH_SHORT).show();
        }

        if (load.getStatus() != AsyncTask.Status.RUNNING){
            load = null;
            load = new LoadSub();
            load.execute();
        }

        if (i < numbers.length - 1) setCards(i);
    }

    //Создание скриншота
    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    //Сохранение БитКарты (Нужно для СкринШота)
    private static File saveBitmap(Bitmap bm, String fileName) {
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public class Connect extends AsyncTask<String, Void, String> {
        ProgressDialog dialog = new ProgressDialog(Screen.this);
        private boolean exception = false;
        boolean b;

        @Override
        protected void onPreExecute() {
            if (!b) {
                dialog.setMessage("Загрузка...");
                dialog.show();
                dialog.setCancelable(false);
            }
            b = true;
        }

        protected String doInBackground(String... arg) {
            Document doc;



            cards.clear();

            try {
                doc = Jsoup.connect("http://kumdang.ru/wp.html")
                        .get();

                obj = doc.select("div#object");

                for (Element now : obj) {
                    cards.add(new Card(obj.get(count).text().split(" ")[0], obj.get(count).text().split(" ")[1], now.text().split(" ")[2]));
                    count++;
                }

                //random
                numbers = new int[count];
                for (int i = 0; i < count; i++) {
                    int k = new Random().nextInt(count);
                    while (numbers[k] != 0) {
                        k = new Random().nextInt(count);
                    }
                    numbers[k] = i;
                }

                for (int i = parse; i < parse + 4; i++){
                    if (cards.get(numbers[i]).getStats().contains("youtube")){
                        doc = Jsoup.connect(cards.get(numbers[i]).getStats())
                                .userAgent("Chrome/32.0.1667.0")
                                .get();
                        cards.get(numbers[i]).setStats(doc.select("div.primary-header-actions>span>span").get(0).text());
                    }
                }

                parse = 4;

            } catch (IOException e) {
                exception = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (exception) {
            } else {
                screenGame.setVisibility(View.VISIBLE);
                score.setText("0");
                screenScore.setVisibility(View.GONE);
                setCards(0);
            }
            dialog.dismiss();
        }

    }


    public class LoadSub extends AsyncTask<String, Void, String> {

        boolean exception = false;
        boolean ie = false;

        protected String doInBackground(String... arg) {
            Document doc;

            try {
                for (int i = parse; i < count; i++) {
                    if (cards.get(numbers[i]).getStats().contains("youtube")) {
                        doc = Jsoup.connect(cards.get(numbers[i]).getStats())
                                .userAgent("Chrome/32.0.1667.0")
                                .get();
                        cards.get(numbers[i]).setStats(doc.select("div.primary-header-actions>span>span").get(0).text());
                    }
                }
            } catch (IndexOutOfBoundsException i) {
                ie = true;
            } catch (Exception e) {
                exception = true;
            }
            return null;
        }

    }

    private void setCards(int i) {
        tv.setText(cards.get(numbers[i]).getName());
        Picasso.with(this).load(cards.get(numbers[i]).getImg()).into(img);
        stats.setText(cards.get(numbers[i]).getStats());

        tv2.setText(cards.get(numbers[i + 1]).getName());
        Picasso.with(this).load(cards.get(numbers[i + 1]).getImg()).into(img2);
    }


}
