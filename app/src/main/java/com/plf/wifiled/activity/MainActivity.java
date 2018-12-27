package com.plf.wifiled.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String RED_LED_OPEN = "0101";
    public static final String RED_LED_CLOSE = "0100";
    public static final String YELLOW_LED_OPEN = "0201";
    public static final String YELLOW_LED_CLOSE = "0200";
    public static final String BLUE_LED_OPEN = "0301";
    public static final String BLUE_LED_CLOSE = "0300";

    private Button mBtnConnect;
    private EditText mEtIP;
    private EditText mEtPort;
    private Socket mSocket;
    private ConnectThread mConnectThread;
    private PrintStream out;
    private ImageView mRedImageView;
    private ImageView mYellowImageView;
    private ImageView mBlueImageView;

    private boolean redFlag = true;
    private boolean yellowFlag = true;
    private boolean blueFlag = true;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnConnect =  findViewById(R.id.btn_connect);
        mEtIP =  findViewById(R.id.et_ip);
        mEtPort =  findViewById(R.id.et_port);
        mRedImageView = findViewById(R.id.btn_red);
        mYellowImageView =  findViewById(R.id.btn_yellow);
        mBlueImageView =  findViewById(R.id.btn_blue);

        mBtnConnect.setOnClickListener(this);
        mRedImageView.setOnClickListener(this);
        mYellowImageView.setOnClickListener(this);
        mBlueImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                //连接
                if (mSocket == null || !mSocket.isConnected()) {
                    loadingProgressDialog();
                    String ip = mEtIP.getText().toString();
                    int port = Integer.valueOf(mEtPort.getText().toString());
                    mConnectThread = new ConnectThread(ip, port);
                    mConnectThread.start();
                }

                if (mSocket != null && mSocket.isConnected()) {
                    try {
                        mSocket.close();
                        mSocket = null;   //  清空mSocket
                        mBtnConnect.setText("连接");
                        Toast.makeText(MainActivity.this, "连接已关闭", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_red: //红灯
                if (mSocket != null && mSocket.isConnected()) {
                    if (redFlag) {
                        openLed(RED_LED_OPEN);
                        switchLed(mRedImageView, R.drawable.on_not_receive_ok);
                        redFlag = false;
                    } else {
                        closeLed(RED_LED_CLOSE);
                        switchLed(mRedImageView, R.drawable.off_not_receive_ok);
                        redFlag = true;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "服务器未连接", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_yellow: //黄灯
                if (mSocket != null && mSocket.isConnected()) {
                    if (yellowFlag) {
                        openLed(YELLOW_LED_OPEN);
                        switchLed(mYellowImageView, R.drawable.on_not_receive_ok);
                        yellowFlag = false;
                    } else {
                        closeLed(YELLOW_LED_CLOSE);
                        switchLed(mYellowImageView, R.drawable.off_not_receive_ok);
                        yellowFlag = true;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "服务器未连接", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_blue: //蓝灯
                if (mSocket != null && mSocket.isConnected()) {
                    if (blueFlag) {
                        openLed(BLUE_LED_OPEN);
                        switchLed(mBlueImageView, R.drawable.on_not_receive_ok);
                        blueFlag = false;
                    } else {
                        closeLed(BLUE_LED_CLOSE);
                        switchLed(mBlueImageView, R.drawable.off_not_receive_ok);
                        blueFlag = true;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "服务器未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void loadingProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setMessage("连接中,请稍后...");
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.i("tag", "onDismiss");
                if (mSocket != null && mSocket.isConnected()) {
                    try {
                        mSocket.close();
                        mSocket = null;   //  清空mSocket
                        mBtnConnect.setText("连接");
                        Toast.makeText(MainActivity.this, "连接已关闭", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void switchLed(ImageView ledImageView, int imgResId) {
        ledImageView.setImageResource(imgResId);
    }

    private void openLed(final String ledOpen) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (out != null) {
                        out.print(ledOpen);
                        out.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "message:" + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private void closeLed(final String ledClose) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (out != null) {
                        out.print(ledClose);
                        out.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "message:" + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    private class ConnectThread extends Thread {
        private String ip;
        private int port;

        public ConnectThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                mSocket = new Socket(ip, port);
                out = new PrintStream(mSocket.getOutputStream());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBtnConnect.setText("断开");
                        Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            dialog.dismiss();
        }
    }
}
