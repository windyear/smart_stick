package com.indoorlocate.smart_stick;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    //声明一个数据库用于存储信息
    private SQLiteDatabase sms_db;
    private TextView sms_body;
    //用于显示短信内容
    private Uri SMS_INBOX=Uri.parse("content://sms/");
    //这里的只能接收到新发过来的短信
    private BroadcastReceiver sms_Receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           //获取数据
            Bundle bundle = intent.getExtras();
            SmsMessage msg = null;
            if (null != bundle) {
                Object[] smsObj = (Object[]) bundle.get("pdus");
                for (Object object : smsObj) {
                    msg = SmsMessage.createFromPdu((byte[]) object);
                    System.out.println("number:" + msg.getOriginatingAddress()
                            + "   body:" + msg.getDisplayMessageBody() + "  time:"
                            + msg.getTimestampMillis());
                    //在这里写自己的逻辑
                    if (msg.getOriginatingAddress().equals("+8618819477449")) {
                        //TODO
                       sms_body.setText(msg.getDisplayMessageBody());
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sms_body=(TextView)findViewById(R.id.sms_body);
        //动态注册广播
        IntentFilter intent = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(sms_Receiver, intent);
        //利用contentprovider直接读取系统中存储的信息
        //获取处理的对象
        ContentResolver cr=getContentResolver();
        //查询的字段，这里只是查询两个
        String[] projection = new String[] {"address", "body"};
        //查询的条件
        String where = "address=18819477458";
        //查询的结果放在了一个Cusor中
        Cursor cusor = cr.query(SMS_INBOX, projection,where, null,null);
        //获取对应字段的标签值
        int phoneNumberColumn = cusor.getColumnIndex("address");
        int smsbodyColumn = cusor.getColumnIndex("body");
        //根据标签值一个个查询
        if (cusor != null) {
            while (cusor.moveToNext()) {
                Log.i("MainActivity",cusor.getString(phoneNumberColumn));
                Log.i("MainActivity",cusor.getString(smsbodyColumn));
            }
            cusor.close();
        }
        //下面进行对数据库的操作
        sms_db=SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/my_sms",null);
        //创建一个表
       // String sql="CREATE TABLE test1 if not exist(_id integer primary key AUTOINCREMENT,body text NOT NULL)";
        //sms_db.execSQL(sql);
        ContentValues sms1=new ContentValues();
        sms1.put("body","Hello world");
        sms_db.insert("test1",null,sms1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
