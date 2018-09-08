package shredder.com.groceryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import shredder.com.groceryapp.Data.DatabaseHelper;
import shredder.com.groceryapp.Model.GroceryStruct;
import shredder.com.groceryapp.UI.RecyclerViewAdapter;

public class ListActivity extends AppCompatActivity {

    ArrayList<GroceryStruct> dbObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Items On List");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        populateDatabase();
        setUpAdapter();
    }

    private void populateDatabase()
    {
        dbObjects = new ArrayList<>();
        DatabaseHelper db = new DatabaseHelper(this);
        dbObjects = db.readAllDatabase();
        }

    private void setUpAdapter()
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,dbObjects);
        recyclerView.setAdapter(adapter);

    }

    private void addItem() {
        final DatabaseHelper db = new DatabaseHelper(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        final EditText groceryItem = (EditText) view.findViewById(R.id.groceryItem);
        final EditText quantity = (EditText) view.findViewById(R.id.groceryQty);
        Button saveButton = (Button) view.findViewById(R.id.saveButton);

        dialogBuilder.setView(view);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroceryStruct temp = new GroceryStruct();

                String itemName = groceryItem.getText().toString().trim();
                String itemQty = quantity.getText().toString().trim();
                temp.setName(itemName);
                temp.setQuantity(itemQty);
                db.addToDatabase(temp);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        finish();
                        startActivity(new Intent(ListActivity.this, ListActivity.class));
                    }
                }, 1200);
            }
        });
    }
}
