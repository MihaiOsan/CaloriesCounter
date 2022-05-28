package com.example.calorie.ui.gallery;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.calorie.MainActivity;
import com.example.calorie.R;
import com.example.calorie.databinding.FragmentGalleryBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    SQLiteDatabase db;
    List<String> list;
    String selectedItem;
    String idFood="";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        MainActivity activity = (MainActivity) getActivity();
        db = activity.getDatabase();
        ListView listView = root.findViewById(R.id.listViewMeals);
        TextInputEditText inputMeal = (TextInputEditText) root.findViewById(R.id.textInputMealName);
        TextView textFoodName = (TextView) root.findViewById(R.id.textViewFoodName);
        TextView textFoodCalories = (TextView) root.findViewById(R.id.textViewFoodCalories);
        TextView textFoodProteins = (TextView) root.findViewById(R.id.textViewFoodProteins);
        TextView textFoodFats = (TextView) root.findViewById(R.id.textViewFats);
        Button buttonSearch = (Button) root.findViewById(R.id.buttonSearchFood);
        Button buttonCount = (Button) root.findViewById(R.id.buttonCountCalories);

        list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM FOOD ORDER BY name", null);
        cursor.moveToFirst();
        idFood = cursor.getString(0);
        textFoodName.setText(cursor.getString(1));
        textFoodCalories.setText(cursor.getString(2) + "kcla");
        textFoodProteins.setText(cursor.getString(3) + "g");
        textFoodFats.setText(cursor.getString(4) + "g");
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                String searchWord = inputMeal.getText().toString();
                Cursor cursor = db.rawQuery("SELECT * FROM FOOD WHERE name LIKE '%"+ searchWord +"%' ORDER BY name", null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    list.add(cursor.getString(1));
                    cursor.moveToNext();
                }
                cursor.close();
                arrayAdapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = db.rawQuery("SELECT * FROM FOOD WHERE name='" +list.get(i)+"';", null);
                cursor.moveToFirst();
                idFood = cursor.getString(0);
                textFoodName.setText(cursor.getString(1));
                textFoodCalories.setText(cursor.getString(2) + "kcla");
                textFoodProteins.setText(cursor.getString(3) + "g");
                textFoodFats.setText(cursor.getString(4) + "g");
                cursor.close();
            }
        });

        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foodName = textFoodName.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(new Date());
                ContentValues cv = new ContentValues();
                cv.put("idFood", idFood);
                cv.put("date", strDate);
                long result = db.insert("calorisCounted",null, cv);
                if (result == -1){
                    Toast toast = Toast.makeText(getActivity(),
                            "faild",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Toast toast = Toast.makeText(getActivity(),
                            textFoodCalories.getText().toString() + " counted",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}