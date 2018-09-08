package shredder.com.groceryapp.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import shredder.com.groceryapp.Model.GroceryStruct;
import shredder.com.groceryapp.Util.GroceryContract;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    /*********************
     *  The constructor  *
     *********************/

    public DatabaseHelper(Context context) {

        super(context, GroceryContract.GroceryEntry.DATABASE_NAME, null, 1);
        this.context = context;
    }

    /****************************
     * SQLiteOpenHelper methods *
     ****************************/

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_STATEMENT =  "CREATE TABLE "
                + GroceryContract.GroceryEntry.TABLE_NAME
                + " ( " + GroceryContract.GroceryEntry._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + GroceryContract.GroceryEntry.COLUMN_ITEM_NAME
                + " TEXT , " + GroceryContract.GroceryEntry.COLUMN_ITEM_QUANTITY
                + " TEXT ," + GroceryContract.GroceryEntry.COLUMN_TIME_STAMP + " LONG );";

        db.execSQL(CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + GroceryContract.GroceryEntry.TABLE_NAME);

        onCreate(db);
    }


    /*********************
     *        ADD        *
     *********************/
    public void addToDatabase(GroceryStruct groceryStruct)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        
        values.put(GroceryContract.GroceryEntry.COLUMN_ITEM_NAME,groceryStruct.getName());
        values.put(GroceryContract.GroceryEntry.COLUMN_ITEM_QUANTITY,groceryStruct.getQuantity());
        values.put(GroceryContract.GroceryEntry.COLUMN_TIME_STAMP,java.lang.System.currentTimeMillis());

        db.insert(GroceryContract.GroceryEntry.TABLE_NAME,null,values);

        //Toast.makeText(context, "Items Added!", Toast.LENGTH_SHORT).show();
    }

    /*********************
     *        DELETE     *
     *********************/

    public void deleteFromDatabase (int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GroceryContract.GroceryEntry.TABLE_NAME, GroceryContract.GroceryEntry._ID + " =? ", new String[]{String.valueOf(id)});
        db.close();
    }

    /*********************
     *        UPDATE     *
     *********************/

    public int updateDatabase(GroceryStruct groceryStruct)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(GroceryContract.GroceryEntry.COLUMN_ITEM_NAME,groceryStruct.getName());
        values.put(GroceryContract.GroceryEntry.COLUMN_ITEM_QUANTITY,groceryStruct.getQuantity());
        values.put(GroceryContract.GroceryEntry.COLUMN_TIME_STAMP,java.lang.System.currentTimeMillis());

        int id = db.update(GroceryContract.GroceryEntry.TABLE_NAME,values, GroceryContract.GroceryEntry._ID + "= ?", new String[]{String.valueOf(groceryStruct.getId())});

        return id;

    }

    /*********************
     *       READ ALL    *
     *********************/

    public ArrayList<GroceryStruct> readAllDatabase()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                                 GroceryContract.GroceryEntry._ID,
                                 GroceryContract.GroceryEntry.COLUMN_ITEM_NAME,
                                 GroceryContract.GroceryEntry.COLUMN_ITEM_QUANTITY,
                                 GroceryContract.GroceryEntry.COLUMN_TIME_STAMP
                              };



        Cursor cursor = db.query(GroceryContract.GroceryEntry.TABLE_NAME,projection,null,null,null,null, GroceryContract.GroceryEntry.COLUMN_TIME_STAMP +" DESC");

        int idIndex = cursor.getColumnIndex(GroceryContract.GroceryEntry._ID);
        int itemIndex = cursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_ITEM_NAME);
        int qtyIndex = cursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_ITEM_QUANTITY);
        int timeIndex = cursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_TIME_STAMP);

        ArrayList <GroceryStruct> arrayList = new ArrayList<>();

       // cursor.moveToFirst();

            while(cursor.moveToNext())
            {
                int id = cursor.getInt(idIndex);
                String groceryItem = cursor.getString(itemIndex);
                String groceryQty = cursor.getString(qtyIndex);

                //convert timestamp to something readable
                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_TIME_STAMP))).getTime());
                arrayList.add(new GroceryStruct(groceryItem,groceryQty,formattedDate,id));
            }
        cursor.close();
        return arrayList;
    }

    /*********************
     * READ SINGLE ENTRY *
     *********************/

    public  GroceryStruct readEntryFromDatabase(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = { GroceryContract.GroceryEntry._ID, GroceryContract.GroceryEntry.COLUMN_ITEM_NAME, GroceryContract.GroceryEntry.COLUMN_ITEM_QUANTITY, GroceryContract.GroceryEntry.COLUMN_TIME_STAMP};

        Cursor cursor = db.query(GroceryContract.GroceryEntry.TABLE_NAME,projection, GroceryContract.GroceryEntry._ID + " = ? ",new String[] { String.valueOf(id)},null,null, GroceryContract.GroceryEntry.COLUMN_TIME_STAMP +" DESC");

        if(cursor!=null)
        cursor.moveToFirst();

            GroceryStruct grocery = new GroceryStruct();

            grocery.setName(cursor.getString(cursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_ITEM_NAME)));
            grocery.setQuantity(cursor.getString(cursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_ITEM_QUANTITY)));

            java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
            String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(GroceryContract.GroceryEntry.COLUMN_TIME_STAMP))).getTime());

            grocery.setDateItemAdded(formatedDate);

            return  grocery;

    }

    /*********************
     *    TOTAL ENTRY    *
     *********************/

    public int getCount ()
    {
        String countQuery = "SELECT * FROM " + GroceryContract.GroceryEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}
