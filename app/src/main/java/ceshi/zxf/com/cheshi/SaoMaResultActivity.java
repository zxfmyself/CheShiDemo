package ceshi.zxf.com.cheshi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by admin on 2016/3/7.
 */
public class SaoMaResultActivity extends AppCompatActivity {

    Button btn_sys;
    TextView tv_result;

    private String page_result;
    private String codetxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saomaresult);

        tv_result = (TextView) this.findViewById(R.id.saomaresult_tv_result);
        btn_sys = (Button) this.findViewById(R.id.saomaresult_rb_back);
        btn_sys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseOn();
            }
        });


        Intent intent = getIntent();
        page_result = intent.getStringExtra("typetitle");
        if(page_result.equals("扫码结果")){
            codetxt =  intent.getStringExtra("codetxt");
        }
        tv_result.setText(codetxt);


    }
    protected void  CloseOn(){
        this.finish();
    }

}
