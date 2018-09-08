package shredder.com.groceryapp.Util;

import android.provider.BaseColumns;

public final class GroceryContract {

    public GroceryContract() {}

    public static final class GroceryEntry implements BaseColumns
    {
        public static final String DATABASE_NAME = "grocery.db";
        public final static String TABLE_NAME = "groceryTable";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "itemName";
        public final static String COLUMN_ITEM_QUANTITY = "itemQty";
        public final static String COLUMN_TIME_STAMP = "timeStamp";

    }
}
