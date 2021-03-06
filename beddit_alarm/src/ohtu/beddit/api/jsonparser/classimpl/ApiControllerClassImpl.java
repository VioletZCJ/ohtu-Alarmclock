package ohtu.beddit.api.jsonparser.classimpl;

import android.content.Context;
import android.util.Log;
import ohtu.beddit.api.ApiController;
import ohtu.beddit.api.jsonparser.BedditJsonParser;
import ohtu.beddit.io.PreferenceService;
import ohtu.beddit.utils.TimeUtils;
import ohtu.beddit.web.BedditConnectionException;
import ohtu.beddit.web.BedditConnector;
import ohtu.beddit.web.BedditException;
import ohtu.beddit.web.BedditConnectorImpl;
import java.util.Calendar;

public class ApiControllerClassImpl implements ApiController {
    private static final String TAG = "ApiController";

    private static final BedditJsonParser jsonParser = new BedditJsonParserImpl();
    private final BedditConnector bedditConnector;
    private static String userJson = null;
    private static String sleepJson = null;
    private static String queueJson = null;
    private static Calendar lastSleepUpdateTime = null;
    private static String lastUser = null;
    public static final int OUTDATED_THRESHOLD_MINUTES = 1;

    public ApiControllerClassImpl() {
        bedditConnector = new BedditConnectorImpl();
    }

    public ApiControllerClassImpl(BedditConnector bedditConnector) {
        this.bedditConnector = bedditConnector;
    }

    @Override
    public void updateUserData(Context context) throws BedditConnectionException {
        userJson = bedditConnector.getUserJson(context);
        Log.v(TAG, "update: " + userJson);
    }

    @Override
    public UserData getUserData(Context context) throws BedditException {
        String json = getUserJson(context);
        return jsonParser.parseJsonToObject(json, UserData.class);
    }

    @Override
    public void updateSleepData(Context context) throws BedditConnectionException {
        String date = TimeUtils.getTodayAsQueryDateString();
        sleepJson = bedditConnector.getWakeUpJson(context, date);
        lastSleepUpdateTime = Calendar.getInstance();
        lastUser = PreferenceService.getUsername(context);
        Log.v(TAG, "update: " + sleepJson);
    }

    @Override
    public SleepData getSleepData(Context context) throws BedditException {
        String json = getSleepJson(context);
        return jsonParser.parseJsonToObject(json, SleepData.class);
    }

    @Override
    public void updateQueueData(Context context) throws BedditConnectionException {
        String date = TimeUtils.getTodayAsQueryDateString();
        queueJson = bedditConnector.getQueueStateJson(context, date);
        Log.v(TAG, "update: " + queueJson);
    }

    @Override
    public QueueData getQueueData(Context context) throws BedditException {
        String json = getQueueJson(context);
        return jsonParser.parseJsonToObject(json, QueueData.class);
    }

    @Override
    public boolean isSleepInfoOutdated() {
        return lastSleepUpdateTime == null ||
               TimeUtils.differenceInMinutes(Calendar.getInstance(), lastSleepUpdateTime) > OUTDATED_THRESHOLD_MINUTES;
    }

    @Override
    public boolean hasUserChanged(Context context) {
        return !lastUser.equals(PreferenceService.getUsername(context));
    }

    @Override
    public void requestInfoUpdate(Context context) throws BedditConnectionException {
        Log.v(TAG, "posted: " + bedditConnector.requestDataAnalysis(context, TimeUtils.getTodayAsQueryDateString()));
    }

    @Override
    public String getAccessToken(String url) throws BedditException {
        Log.v(TAG, "Trying to get access token from " + url);
        String json = bedditConnector.getJsonFromServer(url, false);
        String token = jsonParser.parseJsonToObject(json, TokenData.class).getToken();
        Log.v(TAG, "AccessToken = \"" + token + "\"");
        return token;
    }

    private String getUserJson(Context context) throws BedditException {
        if (userJson == null) {
            updateUserData(context);
        }
        return userJson;
    }

    private String getSleepJson(Context context) throws BedditException {
        if (sleepJson == null) {
            updateSleepData(context);
        }
        return sleepJson;
    }

    private String getQueueJson(Context context) throws BedditException {
        if (queueJson == null) {
            updateQueueData(context);
        }
        return queueJson;
    }


}

