package ohtu.beddit.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.SystemClock;
import android.provider.Settings;
import android.test.AndroidTestCase;
import android.util.Log;
import junit.framework.Assert;
import ohtu.beddit.alarm.AlarmServiceImpl;
import ohtu.beddit.alarm.NotificationFactory;
import ohtu.beddit.io.FileHandler;

import java.util.Calendar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: tahuomo
 * Date: 13.6.2012
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
public class AlarmServiceTest extends AndroidTestCase {
    FileHandler mockFileHandler;
    AlarmManager mockAlarmManager;
    NotificationFactory mockNotifications;
    AlarmServiceImpl alarmservice;

    @Override
    public void setUp() throws Exception {
        mockNotifications = mock(NotificationFactory.class);
        mockFileHandler= mock(FileHandler.class);
        mockAlarmManager = mock(AlarmManager.class);
        when(mockFileHandler.getAlarm()).thenReturn(new int[4]);
        alarmservice = new AlarmServiceImpl(getContext(), mockAlarmManager, mockFileHandler, mockNotifications);
    }

    public void testConstructorChecksFile(){
        //Constructor already called in setUp(), so getAlarm() should've been called already
        verify(mockFileHandler, times(1)).getAlarm();
    }


    public void testAlarmNotSetWhenCreated() throws Exception{
        Assert.assertFalse(alarmservice.isAlarmSet());
    }

    public void testWakeUpAttemptCallsAlarmManager(){
        Calendar c = Calendar.getInstance();
        long timeInMillis = c.getTimeInMillis();
        alarmservice.addWakeUpAttempt(c);

        verify(mockAlarmManager).set(anyInt(), eq(timeInMillis),any(PendingIntent.class));
    }

    public void testAddAlarmUpdatesFile() throws Exception {
        alarmservice.addAlarm(4, 45, 15);
        verify(mockFileHandler).saveAlarm(4, 45,15, true);
    }

    public void testAddAlarmAddsNotification() throws Exception {
        alarmservice.addAlarm(4, 45, 15);
        verify(mockNotifications).setNotification(anyInt(), anyInt(), eq(4), eq(45));
    }

    public void testAddAlarmCallsAlarmManager() throws Exception {
        alarmservice.addAlarm(4, 45, 15);
        verify(mockAlarmManager).set(anyInt(), anyLong(),any(PendingIntent.class));
    }

    public void testAlarmSetAfterAddAlarm() throws Exception{
        alarmservice.addAlarm(4,45,15);
        Assert.assertTrue(alarmservice.isAlarmSet());
    }

    public void testDeleteAlarmCallsCancel(){
        alarmservice.deleteAlarm();
        verify(mockAlarmManager).cancel(any(PendingIntent.class));
    }

    public void testDeleteAlarmUpdatesFile(){
        alarmservice.deleteAlarm();
        verify(mockFileHandler).disableAlarm();
    }

    public void testDeleteAlarmRemovesNotification(){
        alarmservice.deleteAlarm();
        verify(mockNotifications).resetNotification();
    }

    public void testAfterDeleteAlarmIsNotSet(){
        alarmservice.addAlarm(4,45,15);
        Assert.assertTrue(alarmservice.isAlarmSet());
        alarmservice.deleteAlarm();
        Assert.assertFalse(alarmservice.isAlarmSet());
    }


    public void testDoesNotChangeAlarmIfAlarmNotSet(){
        alarmservice.changeAlarm(4,5,15);
        Assert.assertFalse(alarmservice.isAlarmSet());
        verify(mockAlarmManager, times(0)).set(anyInt(), anyLong(),any(PendingIntent.class));
    }

    public void testAlarmChangedIfAlarmIsSet(){
        alarmservice.addAlarm(4,5,15);
        alarmservice.changeAlarm(4,30,15);
        Assert.assertTrue(alarmservice.isAlarmSet());
        verify(mockAlarmManager, times(2)).set(anyInt(), anyLong(),any(PendingIntent.class));
    }

    public void testAlarmServiceReturnsCorrectAlarmInfoFromFile(){
        when(mockFileHandler.getAlarm()).thenReturn(new int[]{1,2,30,15});

        assertEquals(alarmservice.getAlarmHours(), 2);
        assertEquals(alarmservice.getAlarmMinutes(), 30);
        assertEquals(alarmservice.getAlarmInterval(), 15);
    }


}
