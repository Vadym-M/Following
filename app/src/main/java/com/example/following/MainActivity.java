package com.example.following;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final static String LINK_BOOL = "../../posledovaniya/posledovaniya-2021";
    private final static String LINK_BOOL_SECOND = "../../posledovaniya/bogosluzheniya";
    Runnable run;
    Thread thread;
    ArrayList<ParseElement> listElements = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        run = new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://xn--80adfddrquddgz.xn--p1ai/posledovaniya/posledovaniya-2021/").get();
                    Elements elements = doc.getElementsByClass("widget-content").select("a.link");

                   // Log.d("TAG", " TEST 1" + elements.size() + " / " + elements.select("a.link").text());
                    for(Element e: elements)
                    {

                        //Log.d("TAG", "Array size ->" + e.attr("href").length());
                        if(e.attr("href").length() > 37) {
                            if((e.attr("href").substring(0, 38).equals(LINK_BOOL)) || e.attr("href").substring(0, 34).equals(LINK_BOOL_SECOND)) {
                                listElements.add(new ParseElement(e.attr("href").substring(5), e.text()));
                            }
                           // Log.d("TAG", "TEST - >" + e.attr("href").substring(0, 38));
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // Log.d("TAG", "SIZE ARRAY /" + listElements.size() );
                for(ParseElement p:listElements)
                {
                   // Log.d("TAG", "Oto link -> " + p.getLink() +"\n"+ "Oto Text -> " + p.getText() + "\n");
                }
            }
        };thread = new Thread(run);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols()
        {
            @Override
            public String[] getMonths() {
                return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                        "июля", "августа", "сентября", "октября", "ноября", "декабря"};
            }
        };






        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        AdapterElement adapterElement = new AdapterElement(this, listElements);
        recyclerView.setAdapter(adapterElement);
    }
}