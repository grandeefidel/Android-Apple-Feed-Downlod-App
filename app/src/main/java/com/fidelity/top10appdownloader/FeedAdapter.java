package com.fidelity.top10appdownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater mLayoutInflater;
    private List<FeedEntry> applcations;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applcations) {
        super(context, resource);
        this.layoutResource = resource;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.applcations = applcations;
    }

    @Override
    public int getCount() {
        return applcations.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder =(ViewHolder) convertView.getTag();
        }
//        TextView tvName = convertView.findViewById(R.id.tvName);
//        TextView tvArtist = convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary = convertView.findViewById(R.id.tvSummary);

        FeedEntry consoleApp = applcations.get(position);

        viewHolder.tvName.setText(consoleApp.getName());
        viewHolder.tvArtist.setText(consoleApp.getArtist());
        viewHolder.tvSummary.setText(consoleApp.getSummary());

        return convertView;
    }
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        public ViewHolder(View view) {
            this.tvName = view.findViewById(R.id.tvName);
            this.tvArtist = view.findViewById(R.id.tvArtist);
            this.tvSummary = view.findViewById(R.id.tvSummary);
        }
    }
}
