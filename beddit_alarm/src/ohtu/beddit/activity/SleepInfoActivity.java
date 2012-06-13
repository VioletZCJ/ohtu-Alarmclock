package ohtu.beddit.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.location.GpsStatus;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonParser;
import ohtu.beddit.R;
import ohtu.beddit.alarm.AlarmCheckerRealImpl;
import ohtu.beddit.web.BedditWebConnector;
import ohtu.beddit.web.MalformedBedditJsonException;
import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: juho
 * Date: 6/11/12
 * Time: 11:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class SleepInfoActivity extends Activity {

    private Button feelGoodMan;
    private Button feelBatMan;
    private String nightInfo;
    private final String TAG = "SleepInfoActivity";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleep_info);

        getNightInfo();
        setButtons();
        updateText();

    }

    private void getNightInfo() {
        BedditWebConnector connectori = new BedditWebConnector();
        nightInfo = "";
        String date = AlarmCheckerRealImpl.getQueryDateString();
        try {
            nightInfo = connectori.getWakeUpJson(this, date);
        } catch (MalformedBedditJsonException e) {
            Log.v(TAG, "failed to get wake up info");
            e.printStackTrace();
        }
    }

    private void updateText() {
        ((TextView)findViewById(R.id.sleep_info_overall_text)).setText(getHoursAndMinutesFromSeconds(getValueOfKeyFromJson(nightInfo, "time_sleeping")));
        ((TextView)findViewById(R.id.sleep_info_deep_text)).setText(getHoursAndMinutesFromSeconds(getValueOfKeyFromJson(nightInfo, "time_deep_sleep")));

        String dataDate = getValueOfKeyFromJson(nightInfo, "local_analyzed_up_to_time");

        if (getValueOfKeyFromJson(nightInfo, "is_analysis_up_to_date").equalsIgnoreCase("true"))
            ((TextView)findViewById(R.id.sleep_info_delay)).setText("Data is up to date");
        else
            ((TextView)findViewById(R.id.sleep_info_delay)).setText("Data is " + getTimeDifference(dataDate) + " old.");
    }

    private void setButtons() {
        feelGoodMan = (Button)findViewById(R.id.SleptWellButton);
        feelGoodMan.setOnClickListener(new SleepInfoActivity.FeelsGoodButtonClickListener());
        feelBatMan = (Button) findViewById(R.id.SleptBadlyButton);
        feelBatMan.setOnClickListener(new SleepInfoActivity.FeelsBadManButtonClickListener());
    }



    public class FeelsBadManButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    public class FeelsGoodButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }


    private String getValueOfKeyFromJson(String json, String key)
    {
        return new JsonParser().parse(json).getAsJsonObject().get(key).getAsString();
    }

    private String getHoursAndMinutesFromSeconds(String rawdata) {
        int lol = Integer.parseInt(rawdata);
        return lol/3600 + "h " + (lol/60)%60 + "min";
    }

    //expects format like this 2012-06-13T08:38:11 Please don't break it :)
    private String getTimeDifference(String data)
    {
        String parsed = data.substring(11);
        int hours = Integer.parseInt(parsed.substring(0, 2));
        int minutes = Integer.parseInt(parsed.substring(3,5));
        int diffhours = Calendar.getInstance().getTime().getHours() - hours;
        int diffminutes = Calendar.getInstance().getTime().getMinutes() - minutes;
        return diffhours + "h " + diffminutes + "min";
    }
}
