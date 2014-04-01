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
        String relative_dt_str = DateUtils.getRelativeTimeSpanString(created_at_time, now.getTime(),
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

        timeView.setText(relative_dt_str);

        TextView bodyView = (TextView) view.findViewById(R.id.tvBody);
        bodyView.setText(Html.fromHtml(tweet.getBody()));

        return view;
    }
}
