package shredder.com.groceryapp.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import shredder.com.groceryapp.Data.DatabaseHelper;
import shredder.com.groceryapp.MainActivity;
import shredder.com.groceryapp.Model.GroceryStruct;
import shredder.com.groceryapp.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    final Context context;
    ArrayList<GroceryStruct> objects;

    public RecyclerViewAdapter(Context context, ArrayList<GroceryStruct> objects) {

        this.context = context;
        this.objects= objects;
    }


    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        GroceryStruct currentObject = objects.get(position);
        holder.groceryItemName.setText(currentObject.getName());
        holder.quantity.setText(currentObject.getQuantity());
        holder.dateAdded.setText(currentObject.getDateItemAdded());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView groceryItemName;
        public TextView quantity;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;
        public int id;

        public ViewHolder(View view) {
            super(view);

            groceryItemName = (TextView) view.findViewById(R.id.name);
            quantity = (TextView) view.findViewById(R.id.quantity);
            dateAdded = (TextView) view.findViewById(R.id.dateAdded);
            editButton = (Button) view.findViewById(R.id.editButton);
            deleteButton = (Button) view.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int positionOfClick = getAdapterPosition();
                    GroceryStruct tempGrocery = objects.get(positionOfClick);
                    edit(tempGrocery,positionOfClick);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positionOfClick = getAdapterPosition();
                    GroceryStruct tempGrocery = objects.get(positionOfClick);

                    delete(tempGrocery,positionOfClick);
                }
            });
        }
    }

    /*****
     * When we update database,we should also update the current arrayList of objects,so that the screen is updated in realtime,else we will have to refresh the Activity.
     * Instead we use notifyItemChanged() ,which actually takes POSITION AND  OBJECT that was passed as parameter which eventually gets updated with setters then passed as a whole object.So that it can show the proper info at realtime!
     *
     * We can also pass  notifyItemChanged(posiOfItemChanged, null); null means we asking to refresh everything of the current arrayList's objects.
     */

    private void edit(final GroceryStruct  tempGrocery,final int posi) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.popup, null);
        final EditText groceryItem = (EditText) view.findViewById(R.id.groceryItem);
        final EditText quantity = (EditText) view.findViewById(R.id.groceryQty);
        final TextView tv = (TextView) view.findViewById(R.id.title);
        final Button saveButton = (Button) view.findViewById(R.id.saveButton);
        tv.setText("Edit the Entry!");
        groceryItem.setText(objects.get(posi).getName());
        quantity.setText(objects.get(posi).getQuantity());
        dialogBuilder.setView(view);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tempGrocery.setName(groceryItem.getText().toString().trim());
                tempGrocery.setQuantity(quantity.getText().toString().trim());

                if (!groceryItem.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty()) {
                    DatabaseHelper db = new DatabaseHelper(context);
                    db.updateDatabase(tempGrocery);
                    notifyItemChanged(posi, tempGrocery);
                    dialog.dismiss();
                }else
                    Snackbar.make(v, "Add Grocery and Quantity", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /*****************************************************
     *  This will delete the item from Database by calling appropriate function declared in DatabaseHelper.
     *  notifyItemRemoved() actually goes to arrayList of objects and check for updated info,
     *  Therefore if we don't delete arrayList's object along with it,it will continue to show,and will be present untill the activity is refreshed!
     ********************************************************/

    private void delete(final GroceryStruct  tempGrocery,final int positionOfClick)
    {

        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.stat_sys_warning)
                .setTitle("Dialogue Box").setMessage("Do you want to delete ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHelper db1 = new DatabaseHelper(context);
                        db1.deleteFromDatabase(tempGrocery.getId());
                        objects.remove(positionOfClick);
                        notifyItemRemoved(positionOfClick);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }
}
