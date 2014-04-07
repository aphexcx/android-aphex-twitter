package com.example.aphextwitter.app;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aphextwitter.app.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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
        Date mDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            mDate = sdf.parse(created_at);
            created_at_time = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date now = new Date();

        // "1 min ago, 3:46 AM"
//        String relative_dt_str1 = DateUtils.getRelativeDateTimeString(this.getContext(), created_at_time,
//                DateUtils.SECOND_IN_MILLIS, // The resolution. This will display only
//                // minutes (no "3 seconds ago")
//                DateUtils.DAY_IN_MILLIS, // The maximum resolution at which the time will switch
//                // to default date instead of spans. This will not
//                // display "3 weeks ago" but a full date instead
//                DateUtils.FORMAT_ABBREV_RELATIVE).toString(); // Eventual flags

        // "1 min ago"
        String relative_dt_str2 = DateUtils.getRelativeTimeSpanString(created_at_time, now.getTime(),
                DateUtils.SECOND_IN_MILLIS, // The resolution. This will display only
                // minutes (no "3 seconds ago")
                DateUtils.FORMAT_ABBREV_RELATIVE).toString(); // Eventual flags

        // this was an attempt at getting nicer times, i.e. '13m' instead of '13 minutes'
        // it requires creating a custom locale in the form of a ResourceBundle that you are
        // then supposed to add to the prettytime jar.
        // i made one, TWITTER_Resources.java, compiled it with javac, added it to the jar,
        // and it didn't fucking work.
        // http://ocpsoft.org/support/topic/removing-the-ago-portion/
        PrettyTime p = new PrettyTime(new Locale("TWITTER"));
        // "moments ago"
        String relative_dt_strp = p.format(mDate);

//        DateTime myBirthDate = new DateTime(mDate); //(1978, 3, 26, 12, 35, 0, 0);
//        DateTime nowdt = new DateTime();
//        Period period = new Period(myBirthDate, nowdt);
//
//        PeriodFormatter formatter = new PeriodFormatterBuilder()
//                .appendSeconds().appendSuffix(" seconds ago\n")
//                .appendMinutes().appendSuffix(" minutes ago\n")
//                .appendHours().appendSuffix(" hours ago\n")
//                .appendDays().appendSuffix(" days ago\n")
//                .appendWeeks().appendSuffix(" weeks ago\n")
//                .appendMonths().appendSuffix(" months ago\n")
//                .appendYears().appendSuffix(" years ago\n")
//                .printZeroNever()
//                .toFormatter();

//        String relative_dt_str = formatter.print(period);

        String relative_dt_str = getRelativeTimeAgo(tweet.getCreated_at());

        timeView.setText(relative_dt_str);

        // Try to display image media in this tweet
        String tweet_body = tweet.getBody();
        ImageView ivMedia = (ImageView) view.findViewById(R.id.ivMedia);
        // reset the media imageView's bitmap in case this view is recycled
        ivMedia.setImageDrawable(null);
        if (tweet.getMedia().length() > 0) {
            String media_url = "";
            JSONArray indices = null;
            try {
                media_url = tweet.getMedia().getJSONObject(0).getString("media_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!media_url.isEmpty()) {
                ImageLoader.getInstance().displayImage(media_url, ivMedia);
            }

            // try to trim out the media url from the tweet
            try {
                indices = tweet.getMedia().getJSONObject(0).getJSONArray("indices");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (indices != null) {
                StringBuilder tweet_body_buffer = new StringBuilder(tweet_body);
                try {
                    tweet_body_buffer.replace(indices.getInt(0), indices.getInt(1), "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tweet_body = tweet_body_buffer.toString();
            }
        }

        TextView bodyView = (TextView) view.findViewById(R.id.tvBody);
        bodyView.setText(Html.fromHtml(tweet_body));
        Linkify.addLinks(bodyView, Linkify.ALL);

        return view;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // i'm sorry
        relativeDate = relativeDate.replaceAll(" ago", "");
        relativeDate = relativeDate.replaceAll(" secs", "s");
        relativeDate = relativeDate.replaceAll(" sec", "s");
        relativeDate = relativeDate.replaceAll(" mins", "m");
        relativeDate = relativeDate.replaceAll(" min", "m");
        relativeDate = relativeDate.replaceAll(" hours", "h");
        relativeDate = relativeDate.replaceAll(" hour", "h");
        relativeDate = relativeDate.replaceAll(" days", "d");
        relativeDate = relativeDate.replaceAll(" day", "d");

        return relativeDate;
    }
}
