package com.example.following;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

public class AdapterElement extends RecyclerView.Adapter<AdapterElement.ViewHolder> {
    private final static String rootLink = "https://последование.рф";
    Runnable run;
    Thread thread;
    private LayoutInflater inflater;
    private Context context;
    ArrayList<ParseElement> elements;

    public AdapterElement(Context context, ArrayList<ParseElement> elements) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.elements = elements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.main_element, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols()
        {
            @Override
            public String[] getMonths() {
                return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                        "июля", "августа", "сентября", "октября", "ноября", "декабря"};
            }
        };
        String string = elements.get(position).getText();
        SimpleDateFormat dataFormat = new SimpleDateFormat("d MMMM yyyy 'г.'", dateFormatSymbols);
        Date date = null;
        try {
            date = dataFormat.parse(string);
            if(elements.get(position).getText().contains(dataFormat.format(date)))
            {
                SpannableString spannableString = new SpannableString(elements.get(position).getText());
               spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, dataFormat.format(date).length(), 0);

                holder.textParse.setText(spannableString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.layoutElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Document doc = Jsoup.connect(rootLink.concat(elements.get(position).getLink())).get();
                            Elements elements = doc.getElementsByClass("widget-content").select("a.link");

                            // Log.d("TAG", " TEST 1" + elements.size() + " / " + elements.select("a.link").text());
                            for(Element e: elements)
                            {

                                //Log.d("TAG", "Array size ->" + e.attr("href").length());
                                if(e.attr("href").length() > 37) {
                                    if((e.attr("href").substring(e.attr("href").length() - 3).equals("htm"))) {
                                        Log.d("TAG", "OTO LIMK HTML FILE /" + e.attr("href"));
                                        Intent intent = new Intent(context, OpenFile.class);
                                        intent.putExtra("link",e.attr("href"));
                                        context.startActivity(intent);
                                    }
                                    // Log.d("TAG", "TEST - >" + e.attr("href").substring(0, 38));
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                };thread = new Thread(run);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textParse;
        LinearLayout layoutElement;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textParse = itemView.findViewById(R.id.textParse);
            layoutElement = itemView.findViewById(R.id.layoutElement);
        }
    }
}
