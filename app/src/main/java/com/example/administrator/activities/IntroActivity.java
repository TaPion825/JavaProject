package com.example.administrator.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.administrator.framework.R;
import com.example.administrator.networks.ChallengeStart;
import com.example.administrator.networks.HostStart;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IntroActivity extends Activity {

    public static final int bPort = 4516;
    public static final int cPort = 8258;

    private Button hostBtn;
    private Button joinBtn;
    private ListView hostList;
    private Button testBtn;
    //TODO 12/20일 edittext추가
    EditText editText;

    private String myIP;

    protected Intent intent;

    protected Handler handler;

    HostStart hostStart;
    ChallengeStart challengeStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        // 액티비티 테마 : 가로 전체화면
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_intro);

}
    @Override
    protected void onStart() {
        super.onStart();

        editText = (EditText)findViewById(R.id.editText2);

        intent = new Intent(IntroActivity.this, GameActivity.class);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 100) startActivity(intent);
            }
        };

        myIP = getLocalServerIp();

        hostList = (ListView) findViewById(R.id.hostList);


        hostBtn = (Button) findViewById(R.id.hostBtn);
        joinBtn = (Button) findViewById(R.id.joinBtn);

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostBtn.setEnabled(false); joinBtn.setEnabled(false);
                hostStart = new HostStart("host", myIP, bPort, cPort, intent, handler, hostList, IntroActivity.this);
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostBtn.setEnabled(false); joinBtn.setEnabled(false);
                //TODO 12/20일 edittext추가
                challengeStart = new ChallengeStart("challenger", myIP, editText.getText().toString(), bPort, cPort, intent, handler, hostList, IntroActivity.this);
            }
        });

    }

    private String getLocalServerIp()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex) {}
        return null;
    }
}
