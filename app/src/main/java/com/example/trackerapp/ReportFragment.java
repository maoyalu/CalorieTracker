package com.example.trackerapp;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.StrictMath.abs;

public class ReportFragment extends Fragment {
    View vReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vReport = inflater.inflate(R.layout.fragment_report, container, false);

        Button pieBtn = (Button) vReport.findViewById(R.id.pie_button);
        Button barBtn = (Button) vReport.findViewById(R.id.bar_button);
        final EditText endDate = (EditText) vReport.findViewById(R.id.end_date);
        final BarChart barChart = (BarChart) vReport.findViewById(R.id.bar_chart);
        final PieChart pieChart = (PieChart) vReport.findViewById(R.id.pie_chart);

        pieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDate.setVisibility(View.INVISIBLE);
                barChart.setVisibility(View.INVISIBLE);
                pieChart.setVisibility(View.VISIBLE);
            }
        });

        barBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDate.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.INVISIBLE);
            }
        });

        final EditText startDate = (EditText) vReport.findViewById(R.id.start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new StartDatePicker();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new EndDatePicker();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Button searchBtn = (Button) vReport.findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pieChart.getVisibility() == View.VISIBLE){
                    String start = startDate.getText().toString();
                    if(!start.isEmpty()){
                        GetPieTask getPie = new GetPieTask();
                        getPie.execute(start);
                    } else {
                        Toast.makeText(getContext(), R.string.report_error_empty_date, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String start = startDate.getText().toString();
                    String end = endDate.getText().toString();
                    if(!start.isEmpty() && !end.isEmpty()){
                        GetBarTask getBar = new GetBarTask();
                        getBar.execute(start, end);
                    } else {
                        Toast.makeText(getContext(), R.string.report_error_empty_date, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return vReport;
    }

    public static class StartDatePicker extends DatePickerFragment{
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String start = DateFormat.getDateInstance().format(c.getTime());
            EditText startText = (EditText) getActivity().findViewById(R.id.start_date);
            startText.setText(start);
        }
    }

    public static class EndDatePicker extends DatePickerFragment{
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String end = DateFormat.getDateInstance().format(c.getTime());
            EditText endText = (EditText) getActivity().findViewById(R.id.end_date);
            endText.setText(end);
        }
    }

    private class GetPieTask extends AsyncTask<String, Void, Report>{

        @Override
        protected Report doInBackground(String... params) {
            try {
                SessionManagement session = new SessionManagement(getContext());
                int id = session.getCurrentUserId();
                Date date = new SimpleDateFormat("MMM d, yyyy").parse(params[0]);
                Report report = RestClient.getReportByIdAndDate(id, date);
                return report;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Report report){
            List<PieEntry> entries = new ArrayList<>();
            if (report != null){
                double consumed = report.getTotalCaloriesConsumed();
                double burned = report.getTotalCaloriesBurned();
                double remain = abs(report.getCalorieGoal() - (consumed - burned));

                double consumedPct = consumed / (consumed + burned + remain);
                double burnedPct = burned / (consumed + burned + remain);
                double remainPct = remain / (consumed + burned + remain);

                entries.add(new PieEntry((float) consumedPct, "Consumed"));
                entries.add(new PieEntry((float) burnedPct, "Burned"));
                entries.add(new PieEntry((float) remainPct, "Remained"));

            }

            List<Integer> colors = new ArrayList<>();
            colors.add(Color.rgb(255,192,0));
            colors.add(Color.rgb(127,127,127));
            colors.add(Color.rgb(146,208,80));

            PieDataSet set = new PieDataSet(entries, "Calorie");
            set.setColors(colors);
            PieData data = new PieData(set);

            PieChart pie = (PieChart) vReport.findViewById(R.id.pie_chart);
            pie.setData(data);
            pie.setUsePercentValues(true);
            pie.invalidate();
        }
    }

    private class GetBarTask extends AsyncTask<String, Void, List<Report>>{

        @Override
        protected List<Report> doInBackground(String... params) {
            try {
                SessionManagement session = new SessionManagement(getContext());
                int id = session.getCurrentUserId();
                Date start = new SimpleDateFormat("MMM d, yyyy").parse(params[0]);
                Date end = ((new SimpleDateFormat("MMM d, yyyy").parse(params[1])));
                List<Report> reports = RestClient.getReportByIdAndPeriod(id, start, end);
                return reports;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Report> reports){
            if(reports.size() != 0){
                List<BarEntry> consumedGroup = new ArrayList<>();
                List<BarEntry> burnedGroup = new ArrayList<>();
                List<String> dates = new ArrayList<>();

                for(int i = 0; i < reports.size(); i++){
                    consumedGroup.add(new BarEntry((float)i, (float)reports.get(i).getTotalCaloriesConsumed()));
                    burnedGroup.add(new BarEntry((float)i, (float)reports.get(i).getTotalCaloriesBurned()));
                    dates.add(new SimpleDateFormat("MM-dd").format(reports.get(i).getReportDate()));
                }

                Log.i("CONSUMED: ", consumedGroup.toString());
                Log.i("BURNED: ", burnedGroup.toString());
                Log.i("DATES: ", dates.toString());

                BarDataSet consumedSet = new BarDataSet(consumedGroup, "Consumed");
                consumedSet.setColor(Color.rgb(255,192,0));
                BarDataSet burnedSet = new BarDataSet(burnedGroup, "Burned");
                burnedSet.setColor(Color.rgb(127,127,127));

                float groupSpace = 0.4f;
                float barSpace = 0f;
                float barWidth = 0.3f;

                BarData data = new BarData(consumedSet, burnedSet);
                data.setValueFormatter(new LargeValueFormatter());
                data.setBarWidth(barWidth);
                data.getGroupWidth(groupSpace, barSpace);

                BarChart bc = (BarChart) vReport.findViewById(R.id.bar_chart);
                bc.setData(data);
                bc.groupBars(0, groupSpace, barSpace);
                bc.invalidate();

                XAxis x = bc.getXAxis();
                x.setValueFormatter(new IndexAxisValueFormatter(dates));
                x.setAxisMinimum(0);
                x.setAxisMaximum(0 + (float) dates.size());
                x.setCenterAxisLabels(true);
                x.setGranularity(1f);
                x.setGranularityEnabled(true);
            }
        }
    }
}
