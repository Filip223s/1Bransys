package mk.mca.bransys.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import mk.mca.bransys.Interfaces.Service;
import mk.mca.bransys.Interfaces.CustomItemClickListener;
import mk.mca.bransys.ModelData.Network;
import mk.mca.bransys.Activities.OnItemClick_Activity;
import mk.mca.bransys.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CustomViewHolder> implements Filterable {

    private Service service;
    private ArrayList<Network> dataList;
    private ArrayList<Network> dataList2;
    private Context context;
    public CustomItemClickListener mListener;

    public void setOnClickListener(CustomItemClickListener listener) {
        mListener = listener;
    }

    public MainAdapter(ArrayList<Network> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        dataList2= new ArrayList<>(dataList);

    }



    class CustomViewHolder extends RecyclerView.ViewHolder  {
        public final View mView;
     public    TextView txtID;
        TextView cityCode;
        TextView countryCode;
        CustomItemClickListener onLongClickItem;


        CustomViewHolder(View itemView, final CustomItemClickListener onLongClickItem) {
            super(itemView);
            mView = itemView;
            txtID = mView.findViewById(R.id.textView_main);
            cityCode = mView.findViewById(R.id.textView_main1);
            countryCode = mView.findViewById(R.id.textView_main2);
            this.onLongClickItem = onLongClickItem;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Network clickedItem = dataList.get(position);
                    Intent intent = new Intent(context, OnItemClick_Activity.class);
                    intent.putExtra("name",dataList.get(position).getName());
                    intent.putExtra("lat",dataList.get(position).getLocation().getLatitude());
                    intent.putExtra("lang",dataList.get(position).getLocation().getLongitude());
                    intent.putExtra("city",dataList.get(position).getLocation().getCity());
                    intent.putExtra("ccode",dataList.get(position).getLocation().getCountry());

                    context.startActivity(intent);

                }
            });


        }

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycle_view_liists, viewGroup, false);
        CustomViewHolder holder = new CustomViewHolder(view, mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        Network currentItem = dataList.get(i);
        customViewHolder.txtID.setText(dataList.get(i).getName());
        customViewHolder.cityCode.setText(dataList.get(i).getLocation().getCity());
        customViewHolder.countryCode.setText(dataList.get(i).getLocation().getCountry());
//        customViewHolder.txtID.setText(dataList.get(i).getName());
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }
    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Network> filter = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filter.addAll(dataList2);
            } else {
                String filterPatteren = constraint.toString().toLowerCase().trim();

                for (Network item : dataList2) {
                    if (item.getName().toString().toLowerCase().contains(filterPatteren)) {
                        filter.add(item);
                    }
                }
            }
//            Toast.makeText(context, "RESULTS", Toast.LENGTH_LONG).show();
            FilterResults results = new FilterResults();
            results.count = filter.size();
            results.values = filter;
            return results;
        }




        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataList.clear();

            dataList.addAll((ArrayList) results.values);
            notifyDataSetChanged();

        }
    };


}
