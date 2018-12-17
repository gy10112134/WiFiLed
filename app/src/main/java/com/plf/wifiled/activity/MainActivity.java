package com.plf.wifiled.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnConnect;
    private EditText mEtIP;
    private EditText mEtPort;
    private Socket mSocket;
    private ConnectThread mConnectThread;
    private PrintStream out;
    private Button mBtnRedOn;
    private Button mBtnRedOff;
    private Button mBtnYellowOn;
    private Button mBtnYellowOff;
    private Button mBtnBlueOn;
    private Button mBtnBlueOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnConnect = (Button) findViewById(R.id.btn_connect);
        mEtIP = (EditText) findViewById(R.id.et_ip);
        mEtPort = (EditText) findViewById(R.id.et_port);
        mBtnConnect.setOnClickListener(this);
        mBtnRedOn = (Button) findViewById(R.id.btn_red_on);
        mBtnRedOff = (Button) findViewById(R.id.btn_red_off);
        mBtnYellowOn = (Button) findViewById(R.id.btn_yellow_on);
        mBtnYellowOff = (Button) findViewById(R.id.btn_yellow_off);
        mBtnBlueOn = (Button) findViewById(R.id.btn_blue_on);
        mBtnBlueOff = (Button) findViewById(R.id.btn_blue_off);
        mBtnConnect.setOnClickListener(this);
        mBtnRedOn.setOnClickListener(this);
        mBtnRedOff.setOnClickListener(this);
        mBtnYellowOn.setOnClickListener(this);
        mBtnYellowOff.setOnClickListener(this);
        mBtnBlueOn.setOnClickListener(this);
        mBtnBlueOff.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                //连接
                if (mSocket == null || !mSocket.isConnected()) {
                    String ip = mEtIP.getText().toString();
                    int port = Integer.valueOf(mEtPort.getText().toString());
                    mConnectThread = new ConnectThread(ip, port);
                    mConnectThread.start();
                }

                if(mSocket != null && mSocket.isConnected()){
                    try{
                        mSocket.close();
                        mSocket=null;   //  清空mSocket
                        mBtnConnect.setText("连接");
                        Toast.makeText(MainActivity.this,"连接已关闭",Toast.LENGTH_LONG).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_red_on: //开红灯
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (out != null) {
                                out.print("0101");
                                out.flush();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "message:"+e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
                break;
            case R.id.btn_red_off: //关红灯
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (out != null) {
                                out.print("0100");
                                out.flush();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "message:"+e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
                break;
            case R.id.btn_yellow_on: //开黄灯
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (out != null) {
                                out.print("0201");
                                out.flush();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "message:"+e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();

                break;
            case R.id.btn_yellow_off: //开黄灯
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (out != null) {
                                out.print("0200");
                                out.flush();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "message:"+e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();

                break;
            case R.id.btn_blue_on: //开蓝灯
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (out != null) {
                                out.print("0301");
                                out.flush();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "message:"+e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();

                break;
            case R.id.btn_blue_off: //开蓝灯
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (out != null) {
                                out.print("0300");
                                out.flush();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "message:"+e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
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
        }
    }
}
