package com.mstc.mstcapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mstc.mstcapp.R;
import com.mstc.mstcapp.activity.ResourcesActivity;
import com.mstc.mstcapp.model.ResourceModel;

import java.util.List;


public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ResourcesView> {
   private static List<ResourceModel> domains;
   private static Context context;

    public ResourceAdapter(Context mContext, List<ResourceModel> domain) {
        domains = domain;
        context = mContext;//constructor used for initialising the list in the the view
    }

    @NonNull
    @Override
    public ResourcesView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //fills the view with card view layout made (item_resources_domain)
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resource,parent,false);
        return new ResourcesView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ResourcesView holder, final int position) {
        //domain title and card
        holder.texttitle.setText(domains.get(position).getResourceTitle());
        new Thread(new Runnable() {
            @Override
            public void run() {
                holder.domain_bgImage.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(context).load(domains.get(position).getResourcePicture()).into(holder.domain_bgImage);
                    }
                });
            }
        }).start();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity =(Activity) holder.itemView.getContext();
                Intent i =new Intent(v.getContext(), ResourcesActivity.class);
                i.putExtra("domain", String.valueOf(domains.get(position).getResourceTitle()));
                Log.i("Domain",String.valueOf(domains.get(position).getResourceTitle()));
                holder.itemView.getContext().startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return domains.size(); //size of the list
    }

    public static class ResourcesView extends RecyclerView.ViewHolder{

        TextView texttitle;
        ImageView domain_bgImage;
        public ResourcesView(@NonNull View itemView) {
            super(itemView);
            texttitle=(TextView)itemView.findViewById(R.id.domaintitle);
            domain_bgImage=(ImageView)itemView.findViewById(R.id.res_domainImage);
        }
    }
}
