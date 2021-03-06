////////////////////////////////////////
// Name                 Gavin Macleod //
// Student ID           S1715408      //
// Programme of Study   BSc Computing //
////////////////////////////////////////

package gcu.mpd.s1715408.earthqx;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Custom list adapter for the earthquake ListView
 */
public class EarthquakeListAdapter extends ArrayAdapter<Earthquake> {

    private static final String TAG = "EarthquakeListAdapter";

    private Context mContext;
    private int mResource;

    static class ViewHolder{
        TextView location;
        TextView depth;
        TextView magnitude;
    }

    public EarthquakeListAdapter(Context context, int resource, List<Earthquake>objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String location = getItem(position).getLocation();
        String depth = getItem(position).getDepth();
        String magnitude = getItem(position).getMagnitude();

        Earthquake minimalEarthquake = new Earthquake();
        minimalEarthquake.setLocation(location);
        minimalEarthquake.setDepth(depth);
        minimalEarthquake.setMagnitude(magnitude);

        ViewHolder holder = new ViewHolder();

        if(convertView ==null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            holder.location = convertView.findViewById(R.id.titleTextView);
            holder.depth = convertView.findViewById(R.id.depthTextView);
            holder.magnitude = convertView.findViewById(R.id.magnitudeTextView);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.location.setText(location);
        holder.depth.setText(depth);
        holder.magnitude.setText(magnitude);


        ColourManager colourManager = new ColourManager(Double.parseDouble(magnitude));

        holder.magnitude.setTextColor(Color.parseColor(colourManager.GetMagColour()));

        return convertView;

    }
    

}
