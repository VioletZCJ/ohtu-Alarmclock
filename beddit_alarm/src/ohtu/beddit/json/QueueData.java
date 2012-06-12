package ohtu.beddit.json;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: joosakur
 * Date: 12.6.2012
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
public class QueueData extends JsonObject {
    private String results_available_up_to;
    private String sleep_analysis_status;
    private String results_available_up_to_when_sleep_analyzed;
    private String results_available_up_to_when_queued_for_sleep_analysis;

    public String getSleepAnalysisStatus() {
        return sleep_analysis_status;
    }

    public Calendar getResults_available_up_to() {
        return BedditApiController.bedditTimeStringToCalendar(results_available_up_to);
    }
}