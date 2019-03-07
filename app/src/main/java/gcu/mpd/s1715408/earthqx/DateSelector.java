package gcu.mpd.s1715408.earthqx;

import android.app.DatePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateSelector {

    View 
    private Date currentDateSelection;
    private int mDay, mMonth, mYear;

    public void setDateSelection(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        MainActivity.dateTextView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        String stringDate = dateTextView.getText().toString();

                        try{
                            currentDateSelection = new SimpleDateFormat("dd/MM/yyy").parse(stringDate);
                        }
                        catch(ParseException e){
                            Log.e("Date Converting Error", "Can't convert string date to currentDateSelection");
                        }

                        Log.d("currentDateSelection: ",""+currentDateSelection);
                        applyDateFilter();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
