package jp.nk5.zipcodeapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements ZipcodeApiListener{

    private boolean executable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        executable = true;
    }

    public void onClickTextView(View view)
    {
        if (executable) new IntentIntegrator(MainActivity.this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                accessAPI(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void accessAPI(String result)
    {
        if (!isValidZipCode(result)) return;

        new ZipcodeApiAsyncTask(this).execute(Integer.parseInt(result));

    }

    private boolean isValidZipCode(String code)
    {
        if (code.length() != 7) return false;
        try {
            Integer.parseInt(code);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void lockUI()
    {
        executable = false;
    }

    public void unlockUI()
    {
        executable = true;
    }

    public void updateUI(String returnString)
    {
        TextView textView = findViewById(R.id.textView1);
        textView.setText("それは" + returnString + "の郵便番号！！");
    }


}
