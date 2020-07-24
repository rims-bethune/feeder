package fr.univartois.feeder;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import android.view.View;
import android.widget.TextView;


import java.util.List;

public class RssItemAdapter extends ArrayAdapter<RssItem> {
    private List<RssItem> news;
    private Context context;
    public RssItemAdapter(@NonNull Context context, @NonNull List<RssItem> news) {
        super(context, 0, news);
        this.news = news;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();

            holder.rsstitle = convertView.findViewById(R.id.textView_rsstitle);
            holder.rsstext =  convertView.findViewById(R.id.textView_rsstext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RssItem item = this.news.get(position);
        holder.rsstitle.setText(item.title);
        holder.rsstext.setText(Html.fromHtml(item.description, Html.FROM_HTML_MODE_LEGACY));
        return convertView;
    }


    static class ViewHolder {
        TextView rsstitle;
        TextView rsstext;
    }

}