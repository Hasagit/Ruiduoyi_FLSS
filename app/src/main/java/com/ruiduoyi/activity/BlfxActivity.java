package com.ruiduoyi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ruiduoyi.R;
import com.ruiduoyi.adapter.FailureAnalysisAdapter;
import com.ruiduoyi.adapter.SigleSelectAdapter;
import com.ruiduoyi.adapter.SigleSelectAdapter2;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;
import com.ruiduoyi.view.PopupWindowSpinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlfxActivity extends BaseActivity implements View.OnClickListener{
    private Button cancle_btn;
    private Button spinner;//不良产品下拉框
    private ListView listView,listView_register;
    private String jtbh;
    private TextView sjsx_text,zzdh_text,gddh_text,scph_text,mjbh_text,cpbh_text,
            pmgg_text,jhsl_text,lpsl_text,blpsl_text,bldm_text,blms_text,jzzl_text;
    private Button btn_dian,btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,btn_0,btn_clear,btn_submit,btn_del;
    private TextView sub_text;
    private Animation anim;
    private String sub_num;
    private String wkno;
    private String sjsx_str,zzdh_str,gddh_str,scph_str,mjbh_str,cpbh_str,pmgg_str,jhsl_str,lpsl_str,
            blpsl_str,mjmc_str,jzzl_str;
    private SigleSelectAdapter adapter2;
    private List<Map<String,String>>data1;
    private int select_position;
    private PopupWindowSpinner popupWindowSpinner;
    private List<String>zzdh_list=new ArrayList<>();
    private int zzdh_position;
    private PopupDialog dialog;
    private RadioGroup radioGroup;
    private RadioButton radio_sl,radio_zl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blfx);
        initData();
        initView();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x100://初始化
                    JSONArray list_spinner= (JSONArray) msg.obj;
                    try {
                        initSpinner(list_spinner);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x101:
                    try {
                        JSONArray list1= (JSONArray) msg.obj;
                        data1=new ArrayList<>();
                        for (int i=0;i<list1.length();i++){
                            Map<String,String>map=new HashMap<>();
                            map.put("lab_1",list1.getJSONObject(i).getString("bll_bldm"));
                            map.put("lab_2",list1.getJSONObject(i).getString("bll_blmc"));
                            map.put("lab_3","0");
                            data1.add(map);
                        }
                        adapter2= new SigleSelectAdapter(BlfxActivity.this, data1) {
                            @Override
                            public void onRadioSelectListener(int position,Map<String, String> map) {
                                select_position=position;
                                bldm_text.setText(map.get("lab_1"));
                                blms_text.setText(map.get("lab_2"));
                            }

                        };
                        listView.setAdapter(adapter2);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    break;
                case 0x102:
                    try {
                        sub_text.setText("0");
                        JSONArray list2= (JSONArray) msg.obj;
                        List<Map<String,String>>data=new ArrayList<>();
                        for (int i=0;i<list2.length();i++){
                            Map<String,String>map=new HashMap<>();
                            map.put("lab_1",list2.getJSONObject(i).getString("v_bldm"));
                            map.put("lab_2",list2.getJSONObject(i).getString("v_blms"));
                            map.put("lab_3",list2.getJSONObject(i).getString("v_blsl"));
                            map.put("lab_4",list2.getJSONObject(i).getString("v_jlrq"));
                            data.add(map);
                        }
                        SimpleAdapter adapter=new SimpleAdapter(BlfxActivity.this,data,R.layout.list_item_b8_2,
                                new String[]{"lab_1","lab_2","lab_3","lab_4"},new int[]{R.id.lab_1,R.id.lab_2,
                                R.id.lab_3,R.id.lab_4});
                        listView_register.setAdapter(adapter);
                        listView_register.startAnimation(anim);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    break;
                case 0x103:
                    btn_submit.setEnabled(true);
                    btn_submit.setText("提交");
                default:
                    break;
            }
        }
    };


    private void initData(){
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        sjsx_str=sharedPreferences.getString("sjsx","");
        zzdh_str=sharedPreferences.getString("zzdh","");
        gddh_str=sharedPreferences.getString("gddh","");
        scph_str=sharedPreferences.getString("scph","");
        mjbh_str=sharedPreferences.getString("mjbh","");
        cpbh_str=sharedPreferences.getString("cpbh","");
        pmgg_str=sharedPreferences.getString("pmgg","");
        mjmc_str=sharedPreferences.getString("mjmc","");
        jhsl_str=sharedPreferences.getString("jhsl","");
        lpsl_str=sharedPreferences.getString("lpsl","");
        blpsl_str=sharedPreferences.getString("blsl","");
        jzzl_str=sharedPreferences.getString("jzzl","");
        jtbh=sharedPreferences.getString("jtbh","");
        anim= AnimationUtils.loadAnimation(this,R.anim.sub_num_anim);
        Intent intent_from=getIntent();
        wkno=intent_from.getStringExtra("wkno");
        getNetData();
    }

    private void initView(){
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        radio_sl=(RadioButton)findViewById(R.id.radio_sl);
        radio_zl=(RadioButton)findViewById(R.id.radio_zl);
        cancle_btn=(Button)findViewById(R.id.cancle_btn);
        spinner=(Button) findViewById(R.id.blcp_spinner);
        listView=(ListView)findViewById(R.id.list_b8);
        sub_text=(TextView)findViewById(R.id.sub_text);
        sjsx_text=(TextView)findViewById(R.id.dq_1);
        zzdh_text=(TextView)findViewById(R.id.dq_2);
        gddh_text=(TextView)findViewById(R.id.dq_3);
        scph_text=(TextView)findViewById(R.id.dq_4);
        mjbh_text=(TextView)findViewById(R.id.dq_5);
        //mjmc_text=(TextView)findViewById(R.id.dq_6);
        cpbh_text=(TextView)findViewById(R.id.dq_7);
        pmgg_text=(TextView)findViewById(R.id.dq_8);
        jhsl_text=(TextView)findViewById(R.id.dq_9);
        lpsl_text=(TextView)findViewById(R.id.dq_10);
        blpsl_text=(TextView)findViewById(R.id.dq_11);
        bldm_text=(TextView)findViewById(R.id.bldm_text);
        blms_text=(TextView)findViewById(R.id.blms_text);
        jzzl_text=(TextView)findViewById(R.id.dq_12);
        sjsx_text.setText(sjsx_str);
        zzdh_text.setText(zzdh_str);
        gddh_text.setText(gddh_str);
        scph_text.setText(scph_str);
        mjbh_text.setText(mjbh_str);
        cpbh_text.setText(cpbh_str);
        pmgg_text.setText(pmgg_str);
        //mjmc_text.setText(mjmc_str);
        jhsl_text.setText(jhsl_str);
        lpsl_text.setText(lpsl_str);
        blpsl_text.setText(blpsl_str);
        jzzl_text.setText(jzzl_str);

        btn_0=(Button)findViewById(R.id.btn_0);
        btn_1=(Button)findViewById(R.id.btn_1);
        btn_2=(Button)findViewById(R.id.btn_2);
        btn_3=(Button)findViewById(R.id.btn_3);
        btn_4=(Button)findViewById(R.id.btn_4);
        btn_5=(Button)findViewById(R.id.btn_5);
        btn_6=(Button)findViewById(R.id.btn_6);
        btn_7=(Button)findViewById(R.id.btn_7);
        btn_8=(Button)findViewById(R.id.btn_8);
        btn_9=(Button)findViewById(R.id.btn_9);
        btn_del=(Button)findViewById(R.id.btn_del);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        btn_clear=(Button)findViewById(R.id.btn_clear);
        btn_dian=(Button)findViewById(R.id.dian);
        listView_register=(ListView)findViewById(R.id.list_b8_2);
        cancle_btn.setOnClickListener(this);
        btn_dian.setOnClickListener(this);
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_clear.setOnClickListener(this);


        dialog=new PopupDialog(this,400,350);
        dialog.setTitle("提示");
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_dian.setEnabled(false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radio_sl:
                        btn_dian.setEnabled(false);
                        break;
                    case R.id.radio_zl:
                        btn_dian.setEnabled(true);
                        if (jzzl_str.equals("")){
                            dialog.setMessageTextColor(Color.RED);
                            dialog.setMessage("没有维护净重重量的数据，只能按个数输入");
                            dialog.show();
                            radio_sl.setChecked(true);
                        }
                        break;
                }
            }
        });

    }

    private void initListView(List<List<String>>lists){
        List<Map<String,String>>data=new ArrayList<>();
        for (int i=0;i<lists.size();i++){
            Map<String,String>map=new HashMap<>();
            List<String>item=lists.get(i);
            map.put("lab_1",item.get(0));
            map.put("lab_2",item.get(1));
            map.put("isFocus",1+"");
            data.add(map);
        }
        SigleSelectAdapter adapter1=new SigleSelectAdapter(BlfxActivity.this,data) {
            @Override
            public void onRadioSelectListener(int position,Map<String, String> map) {
                bldm_text.setText(map.get("lab_1"));
                blms_text.setText(map.get("lab_2"));
            }
        };
        FailureAnalysisAdapter adapter=new FailureAnalysisAdapter(BlfxActivity.this,R.layout.list_item_b8,data,bldm_text,blms_text);
        listView.setAdapter(adapter1);
    }


    private void initSpinner(JSONArray lists) throws JSONException {
        final List<String>data=new ArrayList<>();
        for (int i=0;i<lists.length();i++){
            data.add(lists.getJSONObject(i).getString("v_wldm")+"\t\t"+lists.getJSONObject(i).get("v_pmgg"));
            zzdh_list.add(lists.getJSONObject(i).getString("v_zzdh"));
        }
        if (data.size()>0){
            spinner.setText(data.get(0));
            zzdh_position=0;
        }
        popupWindowSpinner=new PopupWindowSpinner(BlfxActivity.this,data,R.layout.spinner_list_b7,R.id.lab_1,305);
        popupWindowSpinner.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                spinner.setText(data.get(position));
                zzdh_position=position;
                popupWindowSpinner.dismiss();
            }
        });
        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (popupWindowSpinner!=null){
                   popupWindowSpinner.showDownOn(spinner);
               }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancle_btn:
                finish();
                break;
            case R.id.dian:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(sub_num.indexOf(".")<1){
                    sub_text.setText(sub_num+".");
                }
                break;
            case R.id.btn_0:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if((!sub_num.equals("0"))&&(!sub_num.equals("-"))){
                   sub_text.setText(sub_num+"0");
                }
                break;
            case R.id.btn_1:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"1");
                }else {
                    sub_text.setText("1");
                }
                break;
            case R.id.btn_2:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"2");
                }else {
                    sub_text.setText("2");
                }
                break;
            case R.id.btn_3:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"3");
                }else {
                    sub_text.setText("3");
                }
                break;
            case R.id.btn_4:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"4");
                }else {
                    sub_text.setText("4");
                }
                break;
            case R.id.btn_5:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"5");
                }else {
                    sub_text.setText("5");
                }
                break;
            case R.id.btn_6:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"6");
                }else {
                    sub_text.setText("6");
                }
                break;
            case R.id.btn_7:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"7");
                }else {
                    sub_text.setText("7");
                }
                break;
            case R.id.btn_8:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"8");
                }else {
                    sub_text.setText("8");
                }
                break;
            case R.id.btn_9:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"9");
                }else {
                    sub_text.setText("9");
                }
                break;
            case R.id.btn_submit:
                if (isReady()){
                    btn_submit.setEnabled(false);
                    btn_submit.setText("提交中");
                    upLoadData(wkno);
                }
                break;
            case R.id.btn_del:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(sub_num.equals("0")){
                    sub_text.setText("-");
                }
                break;
            case R.id.btn_clear:
                sub_text.startAnimation(anim);
                sub_text.setText("0");
                break;
            default:
                break;
        }
    }

    private void getNetData(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                JSONArray array=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_ZlmYywh 'D','"+jtbh+"','"+zzdh_str+"'");
                if (array!=null){
                    if (array.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x100;
                        msg.obj=array;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_ZlmYywh 'D'",sharedPreferences.getString("jtnh",""),
                            sharedPreferences.getString("mac",""));
                }



                JSONArray array1=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_Blllist");
                if (array1!=null){
                    if (array1.length()>0){
                            Message msg=handler.obtainMessage();
                            msg.what=0x101;
                            msg.obj=array1;
                            handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_Blllist",sharedPreferences.getString("jtbh",""),
                            sharedPreferences.getString("mac",""));
                }




                JSONArray list2=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_BlmInfo '"+jtbh+"'");
                if (list2!=null){
                    if (list2.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x102;
                        msg.obj=list2;
                        handler.sendMessage(msg);
                    }
                }


            }
        }).start();
    }



    public void upLoadData(final String wkno){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    upLoadOneData(wkno);
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    handler.sendEmptyMessage(0x103);
                }
            }
        }).start();
    }


    private boolean isReady(){
        if (spinner.getText().toString().equals("")){
            dialog.setMessage("请先选取产品");
            dialog.setMessageTextColor(Color.RED);
            dialog.show();
            //Toast.makeText(this,"请先选取产品",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (bldm_text.getText().toString().equals("")){
            dialog.setMessage("请先选取不良描述");
            dialog.setMessageTextColor(Color.RED);
            dialog.show();
            //Toast.makeText(this,"请先选取不良描述",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sub_text.getText().toString().equals("0")){
            dialog.setMessage("请先输入不良数");
            dialog.setMessageTextColor(Color.RED);
            dialog.show();
            //Toast.makeText(this,"请先输入不良数",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Double.parseDouble(lpsl_str)<Double.parseDouble(sub_text.getText().toString().trim())){
            dialog.setMessage("不良品数量不能大于良品数量");
            dialog.setMessageTextColor(Color.RED);
            dialog.show();
            //Toast.makeText(this,"不良品总数量不能大于良品数量",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }




    private void upLoadOneData(String wkno) throws JSONException {
        String num;
        if (radio_sl.isChecked()){
            num=sub_text.getText().toString();
        }else {
            num=String.valueOf((int) (Double.parseDouble(sub_text.getText().toString())/Double.parseDouble(jzzl_str)));
        }
        JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Add_BlmInfo " +
                "'A','"+zzdh_list.get(zzdh_position)+"','','','"+jtbh+"','','"+bldm_text.getText().toString()+"'," +
                "'"+num+"','"+wkno+"'");
        if (list!=null){
            if (list.length()>0){
                if (list.getJSONObject(0).getString("Column1").equals("OK")){
                    JSONArray list1=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_BlmInfo '"+jtbh+"'");
                    if (list1!=null){
                        if (list1.length()>0){
                            Message msg=handler.obtainMessage();
                            msg.what=0x102;
                            msg.obj=list1;
                            handler.sendMessage(msg);
                        }
                    }
                    return ;
                }
            }
        }else {
            //upLoadOneData(wkno);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.sendUpdateInfoFragmentReceiver(this);
    }
}
