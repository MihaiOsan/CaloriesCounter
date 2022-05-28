package com.example.calorie.ui.slideshow;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.calorie.MainActivity;
import com.example.calorie.R;
import com.example.calorie.databinding.FragmentSlideshowBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {
    SQLiteDatabase db;
    ArrayList<String> nutritionalDates;
    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        MainActivity activity = (MainActivity) getActivity();
        db = activity.getDatabase();
        TextView textDataAll = (TextView) root.findViewById(R.id.textViewCalAll);
        BarChart barChart = (BarChart) root.findViewById(R.id.barChartCalories);
        ArrayList<BarEntry> barChartDdata = new ArrayList<>();
        nutritionalDates = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT date(calorisCounted.date), SUM(food.calories), SUM(food.proteins), SUM(food.fats) " + "FROM calorisCounted INNER JOIN food ON food.ID=calorisCounted.idFood group by date(calorisCounted.date);",null);
        cursor.moveToFirst();
        float i= (float) -0.5;
        while (!cursor.isAfterLast()) {
            i++;
            nutritionalDates.add("On "+ cursor.getString(0) +" you consumed:\n" +
                    cursor.getString(1)+"kcal, "+cursor.getString(2)+"g of proteins and "+cursor.getString(3)+"g of fats");
            barChartDdata.add(new BarEntry(i,Integer.parseInt(cursor.getString(1))));
            cursor.moveToNext();
        }
        cursor.close();
        BarDataSet barDataSet = new BarDataSet (barChartDdata,  "Consumed");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10f);
        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));

            }
        });

        barChart.animateY(2000);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x= barChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry) e);
                textDataAll.setText(nutritionalDates.get(x));
            }

            @Override
            public void onNothingSelected() {
                textDataAll.setText("");
            }
        });


        BarChart barChartCalToday = (BarChart) root.findViewById(R.id.barChartCaloriesToday);
        ArrayList<BarEntry> barChartDataCalToday = new ArrayList<>();
        Cursor cursor2 = db.rawQuery("SELECT food.calories " + "FROM calorisCounted INNER JOIN food ON food.ID=calorisCounted.idFood where date(calorisCounted.date) = date(DATE()) order by calorisCounted.date;",null);
        cursor2.moveToFirst();
        float j= (float) -0.5;
        while (!cursor2.isAfterLast()) {
            j++;
            barChartDataCalToday.add(new BarEntry(j,Integer.parseInt(cursor2.getString(0))));
            cursor2.moveToNext();
        }
        cursor2.close();
        BarDataSet barDataSet2 = new BarDataSet (barChartDataCalToday,  "Consumed Today");
        barDataSet2.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet2.setValueTextColor(Color.BLACK);
        barDataSet2.setValueTextSize(16f);
        BarData barData2 = new BarData(barDataSet2);
        barChartCalToday.setFitBars(true);
        barChartCalToday.setData(barData2);

        XAxis xAxis2 = barChartCalToday.getXAxis();
        xAxis2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));

            }
        });
        barChartCalToday.animateY(2000);
        barChartCalToday.setEnabled(false);
        barChartCalToday.setClickable(false);
        barChartCalToday.getDescription().setText("");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}