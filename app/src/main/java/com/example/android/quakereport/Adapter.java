package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by khali on 12/14/2017.
 */
public class Adapter extends ArrayAdapter<Earthquake> {

    public Adapter// كونستركتر علشان تاخد البيانات بتاعة كل ايتم
    (@NonNull Context context,//
     @NonNull List<Earthquake> objects) {
        super(context, 0, objects);

    }


    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;//دي الفيو اللي هترجع في الاخر بعد متتملي محتوى
        if (listItemView == null)// لو هيا فاضية ضخمها من list_item بـ inflater
        {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.card_item, parent, false);
        }


        final Earthquake currentcard = getItem(position);//دا الايتم اللي هنملي منه الفيو




        TextView mag = listItemView.findViewById
                (R.id.mag);// دا قوة الزلزال
        assert currentcard != null;

       double dmag=currentcard.getMag();
        DecimalFormat formatter = new DecimalFormat("0.0");
        String DecimalMag = formatter.format(dmag);

        mag.setText(DecimalMag);
        GradientDrawable magnitudeCircle = (GradientDrawable) mag.getBackground();
        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getContext().getColor(currentcard.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        String[] plase = currentcard.getPlace();

        TextView plase0 = listItemView.findViewById
                (R.id.place0);// دا المكان
        plase0.setText(plase[0]);

        TextView plaseview = listItemView.findViewById
                (R.id.place);// دا المكان

        plaseview.setText(plase[1]);


        String[] times = currentcard.getTime();
        TextView date = listItemView.findViewById
                (R.id.Date);//دا التاريخ
        date.setText(times[0]);
        TextView time = listItemView.findViewById
                (R.id.time);//دا الوقت
        time.setText(times[1]);



        return listItemView;//رجع الايتم دا لليست


    }


}
