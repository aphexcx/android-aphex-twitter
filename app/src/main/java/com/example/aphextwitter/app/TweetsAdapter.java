package com.example.aphextwitter.app;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aphextwitter.app.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TweetsAdapter extends ArrayAdapter<Tweet> {

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.tweet_item, null);
        }

        Tweet tweet = getItem(position);

        ImageView imageView = (ImageView) view.findViewById(R.id.ivProfile);
        ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), imageView);

        TextView nameView = (TextView) view.findViewById(R.id.tvName);
        String formattedName = "<b>" + tweet.getUser().getName() + "</b>" +
                " <small><font color='#777777'>@" + tweet.getUser().getScreenName() + "</font></small>";
        nameView.setText(Html.fromHtml(formattedName));

        TextView timeView = (TextView) view.findViewById(R.id.tvTime);
        //"created_at":"Wed May 23 06:01:13 +0000 2007",
        String created_at = tweet.getCreated_at();
        long created_at_time = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            Date mDate = sdf.parse(created_at);
            created_at_time = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String relative_dt_str = DateUtils.getRelativeDateTimeString(this.getContext(), created_at_time,
                DateUtils.SECOND_IN_MILLIS, // The resolution. This will display only
                // minutes (no "3 seconds ago")
                DateUtils.DAY_IN_MILLIS, // The maximum resolution at which the time will switch
                // to default date instead of spans. This will not
                // display "3 weeks ago" but a full date instead
                DateUtils.FORMAT_ABBREV_RELATIVE).toString(); // Eventual flags
        timeView.setText(relative_dt_str);

        TextView bodyView = (TextView) view.findViewById(R.id.tvBody);
        bodyView.setText(Html.fromHtml(tweet.getBody()));

        return view;
    }
}
