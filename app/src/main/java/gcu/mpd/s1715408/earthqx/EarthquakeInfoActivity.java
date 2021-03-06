////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Activity for displaying all information from an earthquake object
 */
public class EarthquakeInfoActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView titleTextView;
    private TextView magnitudeTextView;
    private TextView depthTextView;
    private TextView publishDateTextView;
    private TextView originDateTextView;
    private TextView categoryTextView;
    private TextView linkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_info);

        Intent intent = getIntent();

        Earthquake earthquake = (Earthquake)intent.getSerializableExtra("earthquake");

        double magnitude = Double.parseDouble(earthquake.getMagnitude());
        ColourManager colourManager = new ColourManager(magnitude);
        String magColour = colourManager.GetMagColour();

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleTextView = findViewById(R.id.earthquakeTitleTxt);
        magnitudeTextView = findViewById(R.id.earthquakeMagnitudeTxt);
        depthTextView = findViewById(R.id.earthquakeDepthTxt);
        publishDateTextView = findViewById(R.id.earthquakePubDateTxt);
        originDateTextView = findViewById(R.id.earthquakeOriginDateTxt);
        categoryTextView = findViewById(R.id.earthquakeCategoryTxt);
        linkTextView = findViewById(R.id.earthquakeLinkTxt);

        if (earthquake != null) {
            titleTextView.setText(earthquake.getLocation());
            magnitudeTextView.setText(earthquake.getMagnitude());
            magnitudeTextView.setTextColor(Color.parseColor(magColour));
            depthTextView.setText(earthquake.getDepth());
            publishDateTextView.setText(earthquake.getPubDate());
            originDateTextView.setText(earthquake.getOriginDate());
            categoryTextView.setText(earthquake.getCategory());
            linkTextView.setText(earthquake.getLink());
        }

    }
}
