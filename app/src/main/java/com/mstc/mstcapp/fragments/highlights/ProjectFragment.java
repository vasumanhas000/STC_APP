package com.mstc.mstcapp.fragments.highlights;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mstc.mstcapp.JsonPlaceholderApi;
import com.mstc.mstcapp.R;
import com.mstc.mstcapp.activity.NavActivity;
import com.mstc.mstcapp.adapter.highlights.ProjectAdapter;
import com.mstc.mstcapp.model.highlights.ProjectsObject;
import com.mstc.mstcapp.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProjectFragment extends Fragment {

    RecyclerView projectRecyclerView;
    ProgressBar projectProgressBar;
    Retrofit retrofit;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProjectAdapter projectAdapter;
    TextView internetCheck;
    SwipeRefreshLayout swipeRefreshLayout;

    public ProjectFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         retrofit=new Retrofit.Builder()
                .baseUrl(Utils.HIGHLIGHT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sharedPreferences= requireContext().getSharedPreferences("project", Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        projectRecyclerView = view.findViewById(R.id.projectRecyclerView);
        projectProgressBar=view.findViewById(R.id.progressbarProject);
        internetCheck=view.findViewById(R.id.internetcheckProject);

        projectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(NavActivity.projectList.size()==0){
            loadData(retrofit);
        }
        else{
            projectAdapter=new ProjectAdapter(getContext(),NavActivity.projectList);
            projectRecyclerView.setAdapter(projectAdapter);
            projectProgressBar.setVisibility(View.GONE);
        }

        //Swipe Refresh Layout
        swipeRefreshLayout = view.findViewById(R.id.projectSwipeRefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(projectProgressBar.getVisibility()==View.GONE)
                {
                    NavActivity.projectList.clear();
                    if(projectRecyclerView.getAdapter()!=null)
                    {
                        Objects.requireNonNull(projectRecyclerView.getAdapter()).notifyDataSetChanged();
                    }
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            loadData(retrofit);
                        }
                    });
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void loadData(Retrofit retrofit) {
        JsonPlaceholderApi jsonPlaceholderApi=retrofit.create(JsonPlaceholderApi.class);
        Call<List<ProjectsObject>> call=jsonPlaceholderApi.getProjects();
        call.enqueue(new Callback<List<ProjectsObject>>() {
            @Override
            public void onResponse(@NotNull Call<List<ProjectsObject>> call, @NotNull Response<List<ProjectsObject>> response) {
                if(!response.isSuccessful()) {
                    swipeRefreshLayout.setRefreshing(false);
                    projectProgressBar.setVisibility(View.GONE);
                    internetCheck.setVisibility(View.VISIBLE);
                    Snackbar.make(projectRecyclerView,"ErrorCode " + response.code(),Snackbar.LENGTH_SHORT).setAnchorView(R.id.nav_view).setBackgroundTint(requireContext().getColor(R.color.colorPrimary)).setTextColor(requireContext().getColor(R.color.permWhite)).show();
                    Log.i("CODE", String.valueOf(response.code()));
                }
                else
                {
                    List<ProjectsObject> projects=response.body();
                    if(projects!=null)
                    {
                        NavActivity.projectList.clear();
                        for(ProjectsObject projectsObject1 :projects){

                            String title = projectsObject1.getTitle();
                            String desc = projectsObject1.getDesc();
                            String link= projectsObject1.getLink();
                            List <String> contri = projectsObject1.getContributors();
                            NavActivity.projectList.add(new ProjectsObject(title,contri,link,desc));

                        }
                        Gson gson=new Gson();
                        String json=gson.toJson(NavActivity.projectList);
                        editor= sharedPreferences.edit();
                        Log.i("JSON",json);
                        editor.putString("data",json);
                        editor.apply();

                        swipeRefreshLayout.setRefreshing(false);
                        projectProgressBar.setVisibility(View.GONE);
                        internetCheck.setVisibility(View.GONE);
                        projectAdapter=new ProjectAdapter(getContext(),NavActivity.projectList);
                        projectRecyclerView.setAdapter(projectAdapter);
                    }
                    else
                    {
                        if(sharedPreferences.contains("data")){
                            Log.i("SHARED","Yes Data");
                            loadShared();
                        }
                        else {
                            swipeRefreshLayout.setRefreshing(false);
                            projectProgressBar.setVisibility(View.GONE);
                            internetCheck.setVisibility(View.VISIBLE);
                        }
                    }
                }


            }

            @Override
            public void onFailure(@NotNull Call<List<ProjectsObject>> call, @NotNull Throwable t) {
                Log.i("FAILED : ", Objects.requireNonNull(t.getMessage()));
                if(sharedPreferences.contains("data")){
                    //sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
                    Log.i("SHARED","Yes Data");
                    loadShared();
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                    projectProgressBar.setVisibility(View.GONE);
                    internetCheck.setVisibility(View.VISIBLE);
                }
            }
        });

    }

   private void loadShared(){
        NavActivity.projectList.clear();
        if(getContext()!=null)
        {
            //SharedPreferences sharedPreferences= requireContext().getSharedPreferences("project", Context.MODE_PRIVATE);
            Gson gson=new Gson();
            String json=sharedPreferences.getString("data",null);
            if(json!=null)
            {
                Log.i("GETDATA ",json);
                Type type=new TypeToken<List<ProjectsObject>>(){}.getType();
                NavActivity.projectList=gson.fromJson(json,type);
                projectAdapter=new ProjectAdapter(getContext(),NavActivity.projectList);
                projectRecyclerView.setAdapter(projectAdapter);
                internetCheck.setVisibility(View.GONE);
            }
            else
            {
                internetCheck.setVisibility(View.VISIBLE);
            }
        }
        else {
            internetCheck.setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
        projectProgressBar.setVisibility(View.GONE);
    }
}