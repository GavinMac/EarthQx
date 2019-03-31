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

        Earthquake earthquake = new Earthquake();
        earthquake.setLocation(location);
        earthquake.setDepth(depth);
        earthquake.setMagnitude(magnitude);

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


        float mag = Float.parseFloat(magnitude);
        if(mag >=3){
            holder.magnitude.setTextColor(Color.parseColor("#FF0000"));
        }
        else if(mag >=2){
            holder.magnitude.setTextColor(Color.parseColor("#FF9800"));
        }
        else if (mag >=1){
            holder.magnitude.setTextColor(Color.parseColor("#FFEB3B"));
        }
        else if(mag <0){
            holder.magnitude.setTextColor(Color.GRAY);
        }
        else{
            holder.magnitude.setTextColor(Color.BLACK);
        }

        return convertView;

    }
}
