package ctnxa.DevWidget.Activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import devwidget.ctnxa.devwidget.R;

public class MainActivity extends AppWidgetProvider {
    // get IP address
    public String getIPAddress(){
        String ipAddress = "";
        List<NetworkInterface> networkInterfaces;
        List<InetAddress> addresses;

        try {
            networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            // parse through the network interfaces
            for(NetworkInterface networkInterface:networkInterfaces){
                addresses = Collections.list(networkInterface.getInetAddresses());

                // parse through the addresses and get the one that does not loop back
                for(InetAddress address:addresses)
                    if(!address.isLoopbackAddress()){
                        ipAddress = address.getHostAddress();
                        if(ipAddress.contains("192.168.1"))
                            return ipAddress;
                    }
            }
        } catch(SocketException e){
            System.out.println("SocketException");
            System.out.println(e);
        } catch(Exception e){
            System.out.println("Exception");
            System.out.println(e);
        }

        return ipAddress;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        PendingIntent pendingIntent;
        RemoteViews remoteViews;
        int widgetId;
        Intent newIntent;

        // parse through the widget id's
        for(int x=0; x<appWidgetIds.length; x++){
            widgetId = appWidgetIds[x];
            newIntent = new Intent(context, MainActivity.class);
            newIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            newIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            pendingIntent = PendingIntent.getBroadcast(context, 0, newIntent, 0);

            // update the widget textbox with IP address and set refresh button to call itself
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);
            remoteViews.setTextViewText(R.id.ipaddress, getIPAddress());
            remoteViews.setOnClickPendingIntent(R.id.refreshButton, pendingIntent);

            // create intent to pass on the data to the widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}