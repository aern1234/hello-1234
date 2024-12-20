package com.example.appmysql;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
String TAG="mysql_php";
ArrayList<String> items;
    ArrayList<String> itemsID;
    ArrayList <String> itemsPhone;
    ArrayList <String> itemspass;
    ArrayAdapter adapter;
    ListView Lv;
    Button btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Lv=findViewById(R.id.Lv);
        btnAdd=findViewById(R.id.btnadd);
        items=new ArrayList<String>();
        itemsID=new ArrayList<String>();
itemsPhone=new ArrayList();
itemspass=new ArrayList<>();
        showUserList();
        adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,items);

        Lv.setAdapter(adapter);
        btnAdd.setOnClickListener(this);
        Lv.setOnItemClickListener(this);

    }
    private void showUserList(){
        //Show progress
      //  showProgress();
        //Connect and get data
        Ion.with(this)
                .load("http://172.17.147.120/android/getusers.php") //URL to PHP script for getting data
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {

                      //  dialog.dismiss();

                        Log.w(TAG, "Error: "+e);
                        Log.w(TAG, "Result: "+result);

                        if (result!=null){
                            //clear previously result on item
                            items.clear();
                            itemsID.clear();
                            //Loop add item
                            for (int i=0; i<result.size(); i++){
                                JsonObject object = (JsonObject)result.get(i);
                                String id = object.get("id_user").getAsString();
                                String name = object.get("name_user").getAsString();
                                String pass = object.get("password_user").getAsString();
                                String phone = object.get("phonenumber_user").getAsString();

                                //Set detail on item
                                String r = "id = "+id+" name = "+name+" password = "+pass;
                                Log.w(TAG, r);
                                //Add item
                                items.add(name);
                                //Save id
                                itemsID.add(id);
                                itemsPhone.add(phone);
                                itemspass.add(pass);
                            }
                            //Update item on showing
                            adapter.notifyDataSetChanged();
                            //Check list
                            if (result.size()<1){
                                Toast.makeText(MainActivity.this,
                                        "Not found data!", Toast.LENGTH_LONG).show();
                            }
                        }else {
                            //Show error
                            Toast.makeText(MainActivity.this, "Error: "+e, Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(this, MainActivityAdd.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent(this, MainActivityManage.class);
        intent.putExtra("id",itemsID.get(i));
        intent.putExtra("name",items.get(i));
        intent.putExtra("phone",itemsPhone.get(i));
        intent.putExtra("pass",itemspass.get(i));
        startActivity(intent);
    }
}