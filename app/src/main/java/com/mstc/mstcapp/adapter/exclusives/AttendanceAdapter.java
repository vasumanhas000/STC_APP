package com.mstc.mstcapp.adapter.exclusives;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mstc.mstcapp.R;
import com.mstc.mstcapp.model.exclusives.AttendanceObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceView> {
    List<AttendanceObject> attendanceObjects_list=new ArrayList<>();
    public static int mExpandedPosition=-1;
    public static int previousExpandedPosition=-1;

    public AttendanceAdapter(List<AttendanceObject> attendanceObjects_list) {
        this.attendanceObjects_list=attendanceObjects_list;
    }

    @NonNull
    @Override
    public AttendanceView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance,parent,false);
        return new AttendanceView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AttendanceView holder, final int position) {
        //String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        holder.attendanceTitle.setText(attendanceObjects_list.get(position).getAttendanceTitle());
        List<String> contents=attendanceObjects_list.get(position).getAttendanceContent();
        int size=contents.size();
        holder.attendanceContent.setText(getData(size,contents));
        final boolean isExpanded = position==mExpandedPosition;
        holder.attendanceContent.setVisibility(isExpanded?View.VISIBLE:View.GONE);
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

    }
    private String getData(int n,List<String> arr) {
        String names="";
        for(int i=0;i<n;i++){
            if(i<n-1)
                names+=arr.get(i)+"\n";
            else
                names+=arr.get(i);
        }
        return names;
    }

    @Override
    public int getItemCount() {
        return attendanceObjects_list.size();
    }

    public class AttendanceView extends RecyclerView.ViewHolder{

        TextView attendanceTitle,attendanceTitleSmall,attendanceContent;

        public AttendanceView(@NonNull View itemView) {
            super(itemView);
            attendanceTitle=itemView.findViewById(R.id.attendance_title);
            attendanceContent=itemView.findViewById(R.id.attendance_content);

            attendanceContent.setVisibility(View.GONE);

        }
    }
}
