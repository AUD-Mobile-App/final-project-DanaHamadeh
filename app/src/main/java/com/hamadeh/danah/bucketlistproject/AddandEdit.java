package com.hamadeh.danah.bucketlistproject;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import static com.hamadeh.danah.bucketlistproject.List.UID;
import static com.hamadeh.danah.bucketlistproject.List.editBucketItem;
import static com.hamadeh.danah.bucketlistproject.List.bucketItemToEdit;
import static com.hamadeh.danah.bucketlistproject.LoginandSignUp.databaseReference;

public class AddandEdit extends AppCompatActivity implements OnMapReadyCallback {
    private EditText name, description;
    private Date date = new Date();
    private GoogleMap mMap;
    private Marker markerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addedit);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        name = (EditText)findViewById(R.id.txtName);
        description = (EditText)findViewById(R.id.txtDesc);
        final Button datePickBtn = (Button)findViewById(R.id.btnDate);

        //If in edit mode
       if (editBucketItem){
           myToolbar.setTitle("CANCEL");}

           else myToolbar.setTitle("CANCEL");

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        //toolbar to go back/cancel
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();}});


        //for map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get date
        final Calendar calendar = Calendar.getInstance();
        final DateFormat txtDate = DateFormat.getDateInstance();
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                date = calendar.getTime();
                datePickBtn.setText(txtDate.format(calendar.getTime()));
            }
        };
        datePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editBucketItem){
                    calendar.setTime(bucketItemToEdit.date); }
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(AddandEdit.this, datePicker, mYear, mMonth, mDay).show();
            }
        });



        //if on edit mode, show the title name,description already set by user
        if(editBucketItem){
            date = bucketItemToEdit.date;
            datePickBtn.setText(txtDate.format(bucketItemToEdit.date));
            name.setText(bucketItemToEdit.name);
            description.setText(bucketItemToEdit.description);}


        else{datePickBtn.setText(txtDate.format(new Date()));}
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(editBucketItem && bucketItemToEdit.userSelectedLocation){

            LatLng pos = new LatLng(bucketItemToEdit.latitude, bucketItemToEdit.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
            markerPosition = mMap.addMarker(new MarkerOptions().position(pos).title("Location"));

        }else{
            LatLng dubaiLoc = new LatLng(25.206052, 55.269554);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dubaiLoc, 10));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(markerPosition != null){
                    markerPosition.remove();
                }
                markerPosition = mMap.addMarker(new MarkerOptions().position(point).title("Location"));
            }
        });
    }

    private boolean validateForm() {
        String email = name.getText().toString();
        if (TextUtils.isEmpty(email)) {
            name.setError("This field is Required");
            return false;
        }
        else {
            name.setError(null);
            return true;
        }
    }


    //in add mode, get name description, date and save in firebase
    public void addBucketItem(View view){

        if (!validateForm()) {
            return; }

        List.BucketItem task = new List.BucketItem();
        task.name = name.getText().toString();
        task.description = description.getText().toString();
        task.date = date;
        //always put checkbox unchecked in the beginning
        task.complete = false;

        if(markerPosition != null){
            task.userSelectedLocation = true;
            task.longitude = markerPosition.getPosition().longitude;
            task.latitude = markerPosition.getPosition().latitude;}
        else
            {task.userSelectedLocation = false;}

        //to update
        if(editBucketItem)
        {databaseReference.child(UID).child("task").child(bucketItemToEdit.itemRef).setValue(task);}
        else
        {databaseReference.child(UID).child("task").push().setValue(task);}
        finish();
    }
}
