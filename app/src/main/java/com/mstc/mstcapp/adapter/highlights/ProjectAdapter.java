package com.mstc.mstcapp.adapter.highlights;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mstc.mstcapp.R;
import com.mstc.mstcapp.model.highlights.ProjectsObject;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.myViewHolder> {

    private Context mContext1;
    private List<ProjectsObject> mData1;
    public static int mExpandedPosition=-1;
    public static int previousExpandedPosition=-1;

    public ProjectAdapter(Context mContext1, List<ProjectsObject> mData) {
        this.mContext1 = mContext1;
        this.mData1 = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(mContext1).inflate(R.layout.item_projects, parent, false);
        myViewHolder vHolder = new myViewHolder(v);
        return vHolder;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, final int position) {

        holder.proj_name.setText(mData1.get(position).getTitle());
        holder.proj_title_secondary.setText(mData1.get(position).getTitle());
        holder.proj_descrip.setText(mData1.get(position).getDesc());
        holder.proj_mediumLink.setText(mData1.get(position).getLink());
        String boldText = "CONTRIBUTORS: ";
        String normalText = String.format("%s", mData1.get(position).getContributors()).replace("[","").replace("]","");
        SpannableString contributor = new SpannableString(boldText + normalText);
        contributor.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.proj_contributors.setText(contributor);
        final boolean isExpanded = position==mExpandedPosition;
        holder.proj_title_secondary.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.proj_name.setVisibility(!isExpanded?View.VISIBLE:View.GONE);
        holder.proj_descrip.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.proj_mediumLink.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.proj_contributors.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        if (isExpanded)
            previousExpandedPosition = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            }
        });
        holder.proj_mediumLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link=mData1.get(position).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData((Uri.parse(link)));
                mContext1.startActivity(intent);
            }
        });
        holder.proj_mediumLink.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                SpannableString ss = new SpannableString(holder.proj_mediumLink.getText().toString());
                ss.setSpan(new UnderlineSpan(),0,ss.length(),0);
                ss.setSpan(new StyleSpan(Typeface.BOLD), 0, ss.length(), 0);
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    holder.proj_mediumLink.setText(ss);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {

                    holder.proj_mediumLink.setText(ss.toString());

                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData1.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {

        private TextView proj_name,proj_descrip,proj_contributors,proj_mediumLink,proj_title_secondary;
        CardView cardView_project;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            proj_name = (TextView) itemView.findViewById(R.id.tv_name_proj);
            proj_descrip = (TextView) itemView.findViewById(R.id.tv_descrip_proj);
            proj_contributors=(TextView)itemView.findViewById(R.id.tv_contributors_proj);
            proj_mediumLink=(TextView)itemView.findViewById(R.id.tv_mediumLink_proj);
            proj_title_secondary=(TextView)itemView.findViewById(R.id.tv_name_proj_full);

            proj_title_secondary.setVisibility(View.GONE);
            proj_descrip.setVisibility(View.GONE);
            proj_contributors.setVisibility(View.GONE);
            proj_mediumLink.setVisibility(View.GONE);
            cardView_project=itemView.findViewById(R.id.project_cardview);
        }
    }
}
