package com.example.trawler;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.UiSettings;

public class MainActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;
    private ViewPager viewPager;
    public static String uName;
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference datRef = database.getReference();
    static DatabaseReference catches = datRef.child("Catches");
    public static ArrayList<Integer> specCode = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uName = getIntent().getExtras().get("User").toString();
        specCode.addAll(getIntent().getIntegerArrayListExtra("Catches"));
        Log.i("MainActivityFirebase", specCode.toString());
        // Create a viewpager, which holds our fragments in a page layout that can be swiped
        viewPager = findViewById(R.id.viewPager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        // Set the default page to 2, which is our camera fragment. Fragments are 0 = Profile,
        // 1 = Map, 2 = Camera, 3 = Encyclopedia, 4 = Settings
        viewPager.setCurrentItem(2);
        listener.onPageSelected(2);

        viewPager.addOnPageChangeListener(listener);


    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {

        private final int NUM_PAGES = 4;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) { // Display the proper fragment
            switch(position) {
                case 0:
                    return new ProfileFragment();
                case 1:
                    return new MapFragment();
                case 2:
                    return new CameraFragment();
                case 3:
                    return new EncyclopediaFragment();
                //case 4:
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


    //Handle the page swapping updating button states at the bottom of the main activity
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            resetButtons();
            ImageButton btn;
            switch(position) {
                case 0:
                    btn = findViewById(R.id.imageButton0);
                    getSupportActionBar().setTitle("Profile");
                    btn.setImageResource(R.drawable.profile_selected);
                    viewPager.setCurrentItem(0);
                    break;
                case 1:
                    btn = findViewById(R.id.imageButton1);
                    getSupportActionBar().setTitle("Map");
                    btn.setImageResource(R.drawable.map_selected);
                    viewPager.setCurrentItem(1);
                    break;
                case 2:
                    btn = findViewById(R.id.imageButton2);
                    getSupportActionBar().setTitle("Fish Classification");
                    btn.setImageResource(R.drawable.camera_selected);
                    viewPager.setCurrentItem(2);
                    break;
                case 3:
                    btn = findViewById(R.id.imageButton3);
                    getSupportActionBar().setTitle("Fishidex: Your Catches");
                    btn.setImageResource(R.drawable.encyclopedia_selected);
                    viewPager.setCurrentItem(3);
                    break;
                    //TODO Implement settings

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void resetButtons() {
        ImageButton btn;

        btn = findViewById(R.id.imageButton0);
        btn.setImageResource(R.drawable.profile_unselected);

        btn = findViewById(R.id.imageButton1);
        btn.setImageResource(R.drawable.map_unselected);

        btn = findViewById(R.id.imageButton2);
        btn.setImageResource(R.drawable.camera_unselected);

        btn = findViewById(R.id.imageButton3);
        btn.setImageResource(R.drawable.encyclopedia_unselected);

    }

    //Each of the below will set the button of the selected fragment to be selected and all others
    //cleared

    public void profileButton(View view) {
        listener.onPageSelected(0);
    }
    public void mapButton(View view) {
        listener.onPageSelected(1);
    }
    public void cameraButton(View view) {
        listener.onPageSelected(2);
    }
    public void encyclopediaButton(View view) {
        listener.onPageSelected(3);
    }
    public void settingsButton(View view) {
        listener.onPageSelected(4);
    }

    public static Catch_Metadata process(ImageProxy img){
        Catch_Metadata temp = new Catch_Metadata();
        temp.setLocation(MapFragment.getLocation());
        temp.setuID(uName);
        //TODO Update process function
        //By not providing the image, we set the image to the striped bass default image
        //For demo purposes, this is hardcoded
        temp.setFish_info(new Fish_Data("???????", "Striped Bass", 232));
        return temp;
    }

    public static void add_data(Catch_Metadata c){
        catches.child(uName).push().setValue(c);
    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}