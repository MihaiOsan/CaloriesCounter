package com.example.calorie.ui.home;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.calorie.MainActivity;
import com.example.calorie.R;
import com.example.calorie.databinding.FragmentHomeBinding;
import com.google.android.material.textfield.TextInputEditText;

public class HomeFragment extends Fragment {
    SQLiteDatabase db;
    private FragmentHomeBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button button = (Button) root.findViewById(R.id.buttonSearchFood);
        TextInputEditText textFoodName = (TextInputEditText) root.findViewById(R.id.textInputFoodName);
        TextInputEditText textFoodCalories = (TextInputEditText) root.findViewById(R.id.textInputCalories);
        TextInputEditText textFoodProteins = (TextInputEditText) root.findViewById(R.id.textInputProteins);
        TextInputEditText textFoodFats = (TextInputEditText) root.findViewById(R.id.textInputFats);
        MainActivity activity = (MainActivity) getActivity();
        db = activity.getDatabase();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,cal,pro, fa;
                int calories, proteins, fats;
                name = textFoodName.getText().toString();
                cal = textFoodCalories.getText().toString();
                pro = textFoodProteins.getText().toString();
                fa = textFoodFats.getText().toString();
                //toate campurile trebuie completate
                if(name.equals("") || cal.equals("") || pro.equals("") || fa.equals("")){
                    Toast toast = Toast.makeText(getActivity(),
                            "All fields must be completed!",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    Cursor cursor = db.rawQuery("SELECT * FROM food WHERE name='" + name + "';",null);
                    if (cursor.moveToFirst()){
                        Toast toast = Toast.makeText(getActivity(),
                                name + " already added",
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                    //daca nu il adaug
                    else {
                        ContentValues cv = new ContentValues();
                        cv.put("name", name);
                        cv.put("calories",cal);
                        cv.put("proteins", pro);
                        cv.put("fats", fa);
                        long result = db.insert("food",null, cv);
                        if (result == -1){
                            Toast toast = Toast.makeText(getActivity(),
                                    "faild",
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else {
                            textFoodName.setText("");
                            textFoodCalories.setText("");
                            textFoodProteins.setText("");
                            textFoodFats.setText("");
                            Toast toast = Toast.makeText(getActivity(),
                                    name + " was added to database",
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
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