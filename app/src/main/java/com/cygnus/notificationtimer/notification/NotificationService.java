package com.cygnus.notificationtimer.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.cygnus.notificationtimer.BuildConfig;
import com.cygnus.notificationtimer.R;
import com.cygnus.notificationtimer.activity.MainActivity;
import com.cygnus.notificationtimer.constant.Constants;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NotificationService extends Service {

    private long TOTAL_TIMER_TIME = 300000;
    private long COUNT_DOWN_INTERVAL = 1000;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Start background service and timer
     *
     * @param intent  for getting data (default method variable)
     * @param flags   default method variable
     * @param startId default method variable
     * @return id  of START_STICKY event
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Objects.equals(intent.getAction(), Constants.ACTION.STARTFOREGROUND_ACTION)) {
            new CountDownTimer(TOTAL_TIMER_TIME, COUNT_DOWN_INTERVAL) {
                public void onTick(long millisUntilFinished) {
                    showNotification(millisUntilFinished);
                }

                public void onFinish() {

                }

            }.start();
        }
        return START_STICKY;
    }

    /**
     * show timer inside notification view
     * startForeground service when app is background
     *
     * @param millisUntilFinished show remaining notification time
     */
    private void showNotification(long millisUntilFinished) {
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);


        views.setTextViewText(R.id.tv_timer, "" + String.format(getResources().getString(R.string.time_format),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

        Notification notification = new Notification.Builder(this)
                .setOngoing(true)
                .build();
        notification.contentView = views;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.icon = R.mipmap.ic_launcher;
        notification.contentIntent = pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(millisUntilFinished);
        } else
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    /**
     * startForeground service for above Oreo version
     *
     * @param millisUntilFinished show remaining notification time
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForegroundService(long millisUntilFinished) {
        String NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID;
        String channelName = getResources().getString(R.string.chanel_id);
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.timer))
                .setContentText("" + String.format(getResources().getString(R.string.time_format),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }


}

