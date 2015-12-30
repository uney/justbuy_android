package com.android.ex.chips.autocomplete;

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
            return "";
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

    public abstract ArrayList<String> autoComplete(String term);
}