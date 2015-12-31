package com.beehivesnetwork.justword.ui.autocomplete;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;


import java.util.ArrayList;


public abstract class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList = new ArrayList<String>();

    public AutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        if(resultList!=null){
            return resultList.size();
        }
        else{
            return 0;
        }
    }

    @Override
    public String getItem(int index) {
        if(resultList.size()>index){
            return resultList.get(index);
        }
        else{
            return null;
        }
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
//                    resultList = autoComplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = autoComplete(constraint.toString());
                    filterResults.count = autoComplete(constraint.toString()).size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                resultList = (ArrayList<String>)results.values;

                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }


//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row = convertView;
//        ViewHolder holder = null;
//
//        if(row == null)
//        {
//            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//            row = inflater.inflate(layoutResourceId, parent, false);
//
//            holder = new WeatherHolder();
//            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
//            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
//
//            row.setTag(holder);
//        }
//        else
//        {
//            holder = (ViewHolder)row.getTag();
//        }
//
//        HashTag weather = resultList.get(position);
//        holder.txtTitle.setText(weather.title);
//        holder.imgIcon.setImageResource(weather.icon);
//
//        return row;
//    }
//
//    static class ViewHolder
//    {
//        ImageView imgIcon;
//        TextView txtTitle;
//    }

    public abstract ArrayList<String> autoComplete(String term);
}