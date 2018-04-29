package com.hamadeh.danah.bucketlistproject;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.graphics.Color.*;
import static com.hamadeh.danah.bucketlistproject.LoginandSignUp.firebaseAuth;
import static com.hamadeh.danah.bucketlistproject.LoginandSignUp.databaseReference;

public class List extends AppCompatActivity {



    final ArrayList<BucketItem> bucketList = new ArrayList<>();
    final CustomAdapter Adapter = new CustomAdapter(bucketList);
    public static boolean editBucketItem = false;
    public static BucketItem bucketItemToEdit;
    ListView Items;
    public static String UID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        //change color of action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50E3A4")));
        Items = (ListView) findViewById(R.id.list);
        Items.setAdapter(Adapter);

        UID = firebaseAuth.getCurrentUser().getUid();

        //if floating button is clicked go to add a new task
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBucketItem = false;
                startActivity(new Intent(List.this, AddandEdit.class));
            }
        });

        //get from firebase and write to the list
        databaseReference.child(UID).child("task").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bucketList.clear();
                for (DataSnapshot imageSnapshot: dataSnapshot.getChildren()) {
                    BucketItem tempBucketItem = imageSnapshot.getValue(BucketItem.class);
                    tempBucketItem.itemRef = imageSnapshot.getKey();
                    bucketList.add(tempBucketItem);
                }
                orderItems();
               Adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


        //When a task is clicked
        Items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //go to edit mode
                editBucketItem = true;
                //get position and start edit activity (same as add)
                bucketItemToEdit = bucketList.get(position);
                startActivity(new Intent(List.this, AddandEdit.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(List.this, LoginandSignUp.class));
            finish();
        }
        else if(item.getItemId() == R.id.Help)
        {
            Toast.makeText(getBaseContext(), "Click on + to add, or on an item to edit.", Toast.LENGTH_LONG).show();
        }


        return true;
    }


    public void orderItems(){
        Collections.sort(bucketList, new Comparator<BucketItem>() {
            public int compare(BucketItem o1, BucketItem o2) {
                if (o1.date == null || o2.date == null) return 0;
                else return o1.date.compareTo(o2.date);
            }
        });

        for (int j = 0; j < bucketList.size(); j++) {
            for (int i = bucketList.size()-1; 0 <= i; i--) {
                if(bucketList.get(i).complete){
                    bucketList.add(bucketList.get(i));
                    bucketList.remove(i);
                }
            }
        }
    }

    final DateFormat txtDate = DateFormat.getDateInstance();;

    public class CustomAdapter extends BaseAdapter {

        private ArrayList<BucketItem> tempBucketList;

        public CustomAdapter(ArrayList<BucketItem> temp){
            tempBucketList = temp;
        }

        @Override
        public int getCount() {
            return tempBucketList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.list_custom,null);
            TextView name = (TextView) view.findViewById(R.id.mainName);
            TextView date = (TextView) view.findViewById(R.id.mainDate);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            final CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bucketList.get(i).complete = checkbox.isChecked();
                    databaseReference.child(UID)
                            .child("task")
                            .child(bucketList.get(i).itemRef)
                            .setValue(bucketList.get(i));
                }
            });

            if(tempBucketList.get(i).complete) checkBox.setChecked(true);
            else checkBox.setChecked(false);

            date.setText(txtDate.format(tempBucketList.get(i).date));
            name.setText(tempBucketList.get(i).name);

            return view;
        }
    }


    //Main Class used
    public static class BucketItem{
        public String itemRef;
        //for checkbox
        public boolean complete;
        public String name;
        public String description;
        public Date date;
        //For map
        public boolean userSelectedLocation;
        public double latitude;
        public double longitude;
    }

}
