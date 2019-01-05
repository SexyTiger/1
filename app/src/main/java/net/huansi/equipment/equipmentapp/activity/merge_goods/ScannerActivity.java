package net.huansi.equipment.equipmentapp.activity.merge_goods;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;

import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.BaseActivity;
import net.huansi.equipment.equipmentapp.entity.HsWebInfo;
import net.huansi.equipment.equipmentapp.entity.MoveMergeInfo;
import net.huansi.equipment.equipmentapp.entity.MoveMergeShowInfo;
import net.huansi.equipment.equipmentapp.event.MessageEvent;
import net.huansi.equipment.equipmentapp.listener.WebListener;
import net.huansi.equipment.equipmentapp.util.NewRxjavaWebUtils;
import net.huansi.equipment.equipmentapp.util.OthersUtil;
import net.huansi.equipment.equipmentapp.util.SPHelper;
import net.huansi.equipment.equipmentapp.widget.LoadProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static net.huansi.equipment.equipmentapp.util.SPHelper.USER_NO_KEY;
import static net.huansi.equipment.equipmentapp.util.SPHelper.getLocalData;

/**
 * Created by zhou.mi on 2018/1/31.
 */

public class ScannerActivity extends BaseActivity {
    @BindView(R.id.etSearchBarcode) EditText etSearchBarcode;
    @BindView(R.id.etSearchFEPO) EditText etSearchFEPO;
    @BindView(R.id.etSearchLocation) EditText etSearchLocation;
    @BindView(R.id.etSearchShelf) EditText etSearchShelf;
    @BindView(R.id.etSearchDate) EditText etSearchDate;
    @BindView(R.id.lvMoveList) ListView lvMoveList;
    @BindView(R.id.spArea) Spinner spArea;
    @BindView(R.id.spGallery) Spinner spGallery;
    @BindView(R.id.etShelf) EditText etShelf;
    @BindView(R.id.etRemark) EditText etRemark;
    @BindView(R.id.lv_select_cancel)
    LinearLayout lv_select_cancel;
    private List<String> scannerInfoList;
    private List<MoveMergeShowInfo> moveMergeShowList;
    private MoveMergeShowInfo moveMergeShowInfo;
    private LoadProgressDialog dialog;
    private HsWebInfo hsWebInfo=new HsWebInfo();
    private List<String> areaList=new ArrayList<>();
    private List<String> galleryList=new ArrayList<>();
    private ArrayAdapter<String> mAreaAdapter;
    private ArrayAdapter<String> mGalleryAdapter;
    private MoveMergeShowAdapter moveMergeShowAdapter;
    private List<Boolean> selectList = new ArrayList(); // 判断listview单选位置
    private List<String> mergeBarcode =new ArrayList<>();//需要并架的条码号集合（CheckBox选中的）
    @Override
    public void init() {
        OthersUtil.hideInputFirst(this);
        setToolBarTitle("挪料并架");
        dialog=new LoadProgressDialog(this);
        scannerInfoList=new ArrayList<>();
        EventBus.getDefault().register(this);
        areaList.add("A");
        areaList.add("B");
        areaList.add("C");
        areaList.add("D");
        areaList.add("E");
        areaList.add("F");
        mAreaAdapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.string_item,R.id.text,areaList);
        spArea.setAdapter(mAreaAdapter);
        for (int i=1;i<=11;i++){
            galleryList.add(Integer.toString(i));
        }
        mGalleryAdapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.string_item,R.id.text,galleryList);
        spGallery.setAdapter(mGalleryAdapter);
        moveMergeShowAdapter=new MoveMergeShowAdapter();
        //SharedPreferences保存上传架号信息
        SharedPreferences message = getSharedPreferences("message", MODE_PRIVATE);
        String location = message.getString("Location", "");
        String shelf = message.getString("Shelf", "");
        String date = message.getString("Date", "");
        etSearchLocation.setText(location);
        etSearchShelf.setText(shelf);
        etSearchDate.setText(date);
    }



    @Override
    protected int getLayoutId() {
        return R.layout.scanner_activity;
    }

    @OnClick(R.id.btUpDate)
    void scanCodeUpDate(){
       Log.e("TAG","MergeCode="+mergeBarcode);
        String s = "";
        for (int i = 0; i < mergeBarcode.size(); i++) {
            s = s + mergeBarcode.get(i) + "/";
        }
        if (s.length()==0){
            return;
        }
        final String needUp = s.substring(0, s.length() - 1);
        Log.e("TAG","needUp="+needUp);
            NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(ScannerActivity.this, hsWebInfo)
                            .map(new Func1<HsWebInfo, HsWebInfo>() {
                                @Override
                                public HsWebInfo call(HsWebInfo hsWebInfo) {
                                    Log.e("TAG","upupDate");
                                    return NewRxjavaWebUtils.getJsonData(getApplicationContext(),
                                            "spSubmitOther_MaterialStockInByBarcode_ForMergeShelf",
                                            "Barcode="+needUp+
                                                    ",Area="+spArea.getSelectedItem().toString() +
                                                    ",Location="+spGallery.getSelectedItem().toString()+
                                                    ",Shelf="+etShelf.getText().toString()+
                                                    ",GoodsRemarks="+"并架",
                                            String.class.getName(),
                                            false,
                                            "helloWorld");
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                    ,ScannerActivity.this, dialog, new WebListener() {
                        @Override
                        public void success(HsWebInfo hsWebInfo) {
                            Log.e("TAG", "success1");
                            moveMergeShowList.clear();
                            moveMergeShowAdapter.notifyDataSetChanged();
                            OthersUtil.showTipsDialog(ScannerActivity.this,"并架成功!");
                        }

                        @Override
                        public void error(HsWebInfo hsWebInfo) {
                            Log.e("TAG", "error1="+hsWebInfo.json);
                        }
                    });

    }
    @OnClick(R.id.btSearchData)
    void searchData(){

        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(ScannerActivity.this, hsWebInfo)
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Log.e("TAG","getDate");
                                return NewRxjavaWebUtils.getJsonDataExt(getApplicationContext(),
                                        "SqlConnStrAGP","Proc_MES_StrategyBoard_History", "",
                                        String.class.getName(),
                                        false,
                                        "helloWorld");
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                ,ScannerActivity.this, dialog, new WebListener() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "successmmmmm="+hsWebInfo.json);

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "errormmmmm="+hsWebInfo.json);
                    }
                });


        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(ScannerActivity.this, hsWebInfo)
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                Log.e("TAG","upupDate");
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),
                                        "spGetOther_MaterialStockInByShelf",
                                        "Barcode="+etSearchBarcode.getText().toString()+
                                                ",FEPO="+etSearchFEPO.getText().toString() +
                                                ",Location="+etSearchLocation.getText().toString()+
                                                ",Shelf="+etSearchShelf.getText().toString()+",CheckDate="+etSearchDate.getText().toString(),
                                        String.class.getName(),
                                        true,
                                        "helloWorld");
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                ,ScannerActivity.this, dialog, new WebListener() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "success1o");

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "error1o="+hsWebInfo.json);
                        String json = hsWebInfo.json;
                       if (json==null){
                           return;
                       }
                        MoveMergeInfo moveMergeInfo = JSON.parseObject(json, MoveMergeInfo.class);
                        if (moveMergeInfo==null){
                            return;
                        }
                        List<MoveMergeInfo.DATABean> data = moveMergeInfo.getDATA();
                        moveMergeShowList=new ArrayList<>();
                        for (MoveMergeInfo.DATABean item:data){
                            moveMergeShowInfo=new MoveMergeShowInfo();
                            moveMergeShowInfo.BARCODE=item.getBARCODE();
                            mergeBarcode.add(item.getBARCODE());
                            moveMergeShowInfo.FEPO=item.getFEPOCODES();
                            moveMergeShowInfo.MATERIALCODE=item.getMATERIALCODE();
                            moveMergeShowInfo.COLORNAME=item.getCOLORNAME();
                            moveMergeShowInfo.QUANTITYPS=item.getQUANTITYPS();
                            moveMergeShowList.add(moveMergeShowInfo);
                        }
                        initCheck(false);
                        lvMoveList.setAdapter(moveMergeShowAdapter);
                        if (moveMergeShowList.isEmpty()){
                            lv_select_cancel.setVisibility(View.GONE);
                        }else {
                            lv_select_cancel.setVisibility(View.VISIBLE);
                        }
                    }
                });


    }
    @OnClick(R.id.selectAll)
    void selectAll(){
        initCheck(true);
        moveMergeShowAdapter.notifyDataSetChanged();
    }
    @OnClick(R.id.cancelAll)
    void cancelAll(){
        initCheck(false);
        moveMergeShowAdapter.notifyDataSetChanged();
    }
    private void initCheck( Boolean flag) {
        for (int i=0;i<moveMergeShowList.size();i++){
            selectList.add(i,flag);
        }
    }

    @OnClick(R.id.tvSearchDate)
    void timePicker(){
        Calendar calendar=Calendar.getInstance();
        DatePickerDialog dialog=new DatePickerDialog(ScannerActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.e("TAG","month="+month);
                String sMonth = null;
                if (month < 9) {
                    month=month+1;
                    sMonth = "0" + month;
                }else {
                    month=month+1;
                    sMonth=""+month;
                }
                String sDay = null;
                if (dayOfMonth < 10) {
                    sDay = "0" + dayOfMonth;
                }else {
                    sDay=""+dayOfMonth;
                }
                etSearchDate.setText(year + "-" + sMonth + "-" + sDay);

            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    //自动接收的数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event){
        Log.e("TAG", "onEvent...."+event.getMessage());
        etSearchBarcode.getText().clear();
        etSearchBarcode.setText(event.getMessage());
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        etSearchBarcode.getText().clear();
        String barcode = etSearchBarcode.getText().toString();
        Log.e("TAG","barcode="+barcode);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerInfoList.clear();
        EventBus.getDefault().unregister(this);
        SharedPreferences sharedPreferences = getSharedPreferences("message", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Location",etSearchLocation.getText().toString());
        editor.putString("Shelf",etSearchShelf.getText().toString());
        editor.putString("Date",etSearchDate.getText().toString());
        editor.commit();
    }

    public class MoveMergeShowAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return moveMergeShowList.size();
        }

        @Override
        public Object getItem(int position) {
            return moveMergeShowList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
                Log.e("TAG","getView了");
                convertView= LayoutInflater.from(ScannerActivity.this).inflate(R.layout.listview_move_merge_item,null);
                CheckBox cbMerge= (CheckBox) convertView.findViewById(R.id.cbMoveMergeShowData);
                cbMerge.setText(
                    moveMergeShowList.get(position).BARCODE+"/"+moveMergeShowList.get(position).FEPO
                            +"/"+moveMergeShowList.get(position).MATERIALCODE
                            +"/"+moveMergeShowList.get(position).COLORNAME
                            +"/"+moveMergeShowList.get(position).QUANTITYPS);
                cbMerge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            selectList.set(position,true);
                            if (!mergeBarcode.contains(moveMergeShowList.get(position).BARCODE)){
                                mergeBarcode.add(moveMergeShowList.get(position).BARCODE);
                            }
                        }else {
                            selectList.set(position,false);
                            mergeBarcode.remove(moveMergeShowList.get(position).BARCODE);
                        }
                    }
                });
            cbMerge.setChecked(selectList.get(position));
            return convertView;
        }
    }


}
