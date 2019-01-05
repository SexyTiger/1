package net.huansi.equipment.equipmentapp.activity.call_repair;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.BaseActivity;
import net.huansi.equipment.equipmentapp.adapter.HsBaseAdapter;
import net.huansi.equipment.equipmentapp.entity.EvaluateEntities;
import net.huansi.equipment.equipmentapp.entity.HsWebInfo;
import net.huansi.equipment.equipmentapp.entity.RepairEvaluate;
import net.huansi.equipment.equipmentapp.listener.WebListener;
import net.huansi.equipment.equipmentapp.util.NewRxjavaWebUtils;
import net.huansi.equipment.equipmentapp.util.OthersUtil;
import net.huansi.equipment.equipmentapp.util.SPHelper;
import net.huansi.equipment.equipmentapp.util.ViewHolder;
import net.huansi.equipment.equipmentapp.widget.LoadProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rx.functions.Func1;

import static net.huansi.equipment.equipmentapp.util.SPHelper.USER_NO_KEY;

public class CallRepairEvaluateActivity extends BaseActivity {
    @BindView(R.id.lv_EvaluateList) ListView lv_EvaluateList;
    private EvaluateAdapter evaluateAdapter;
    private RepairEvaluate repairEvaluate;
    private List<RepairEvaluate> evaluates=new ArrayList<>();
    private LoadProgressDialog dialog;
    private HsWebInfo hsWebInfo=new HsWebInfo();
    private final String[] items = new String[]{"非常满意", "比较满意", "需要改进", "差评"};
    private List<String> itemList=new ArrayList<>();
    private int index=0;//对应评价等级
    @Override
    protected int getLayoutId() {
        return R.layout.activity_call_repair_evaluate;
    }

    @Override
    public void init() {
        setToolBarTitle("叫修评价");
        dialog=new LoadProgressDialog(this);
        itemList.add("Excellent");
        itemList.add("Good");
        itemList.add("Common");
        itemList.add("Bad");
        initInfo();

    }
    private void initInfo() {

        final String caller = SPHelper.getLocalData(getApplicationContext(), USER_NO_KEY, String.class.getName(), "").toString();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(CallRepairEvaluateActivity.this, hsWebInfo)
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),
                                        "spAppGET_EPCallRepairRecord",
                                        "ActionType=" + "Mine" +
                                                ",CallRepairEmployeeID=" + caller,
                                        String.class.getName(),
                                        false,
                                        "helloWorld");
                            }
                        })
                ,CallRepairEvaluateActivity.this, dialog, new WebListener() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "success1="+hsWebInfo.json);
                        String json = hsWebInfo.json;
                        EvaluateEntities evaluateEntities = JSON.parseObject(json, EvaluateEntities.class);
                        List<EvaluateEntities.DATABean> data = evaluateEntities.getDATA();
                        for (int i=0;i<data.size();i++){
                            repairEvaluate=new RepairEvaluate();
                            repairEvaluate.ID=data.get(i).getID();
                            repairEvaluate.EQUIPMENTDETAILID=data.get(i).getEQUIPMENTDETAILID();
                            repairEvaluate.CALLREPAIRITEMID=data.get(i).getCALLREPAIRITEMID();
                            repairEvaluate.CALLREPAIRDATE=data.get(i).getCALLREPAIRDATE();
                            repairEvaluate.ASSETSCODE=data.get(i).getASSETSCODE();
                            repairEvaluate.SEWLINE=data.get(i).getSEWLINE();
                            repairEvaluate.CALLREPAIR=data.get(i).getCALLREPAIR();
                            repairEvaluate.CALLREPAIREMPLOYEEID=data.get(i).getCALLREPAIREMPLOYEEID();
                            repairEvaluate.STATUS=data.get(i).getSTATUS();
                            repairEvaluate.COSTCENTER=data.get(i).getCOSTCENTER();
                            repairEvaluate.OUTFACTORYCODE=data.get(i).getOUTFACTORYCODE();
                            repairEvaluate.EPCCODE=data.get(i).getEPCCODE();
                            repairEvaluate.EQUIPMENTNAME=data.get(i).getEQUIPMENTNAME();
                            repairEvaluate.MODEL=data.get(i).getMODEL();
                            repairEvaluate.REPAIRUSER=data.get(i).getREPAIRUSER();
                            repairEvaluate.REPAIRSTARTDATE=data.get(i).getREPAIRSTARTDATE();
                            evaluates.add(repairEvaluate);
                        }
                        evaluateAdapter=new EvaluateAdapter(evaluates,getApplicationContext());
                        lv_EvaluateList.setAdapter(evaluateAdapter);

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "error1="+hsWebInfo.json);
                    }
                });
    }

    private class EvaluateAdapter extends HsBaseAdapter<RepairEvaluate>{


        public EvaluateAdapter(List<RepairEvaluate> list, Context context) {
            super(list, context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            if (convertView==null) convertView=mInflater.inflate(R.layout.activity_call_repair_evaluate_item,viewGroup,false);
            CardView cardView = ViewHolder.get(convertView, R.id.cardView);
            TextView tvCallRepairEquipmentName = ViewHolder.get(convertView, R.id.tvCallRepairEquipmentName);//设备名称
            TextView tvCallRepairCostCenter = ViewHolder.get(convertView, R.id.tvCallRepairCostCenter);//成本中心
            TextView tvCallRepairEPCode = ViewHolder.get(convertView, R.id.tvCallRepairEPCode);//设备编号
            TextView tvCallRepairOutOfFactoryCode = ViewHolder.get(convertView, R.id.tvCallRepairOutOfFactoryCode);//出厂编号
            TextView tvCallRepairEquipmentModel = ViewHolder.get(convertView, R.id.tvCallRepairEquipmentModel);//设备型号
            TextView tvCallRepairAssetsCode = ViewHolder.get(convertView, R.id.tvCallRepairAssetsCode);//资产编号
            TextView tvCallRepairRepairUser = ViewHolder.get(convertView, R.id.tvCallRepairRepairUser);//维修人
            TextView tvCallRepairRepairStartDate = ViewHolder.get(convertView, R.id.tvCallRepairRepairStartDate);//维修开始时间
            TextView tvCallRepairTime = ViewHolder.get(convertView, R.id.tvCallRepairTime);//叫修时间
            TextView tvCallRepairEquipmentState = ViewHolder.get(convertView, R.id.tvCallRepairEquipmentState);//维修状态
            TextView btCancelCallRepair = ViewHolder.get(convertView, R.id.cancelCallRepair);//取消叫修
            RepairEvaluate repairEvaluate = mList.get(position);
            tvCallRepairEquipmentName.setText(repairEvaluate.EQUIPMENTNAME);
            tvCallRepairCostCenter.setText(repairEvaluate.COSTCENTER);
            tvCallRepairEPCode.setText(repairEvaluate.EPCCODE);
            tvCallRepairOutOfFactoryCode.setText(repairEvaluate.OUTFACTORYCODE);
            tvCallRepairEquipmentModel.setText(repairEvaluate.MODEL);
            tvCallRepairAssetsCode.setText(repairEvaluate.ASSETSCODE);
            tvCallRepairRepairUser.setText(repairEvaluate.REPAIRUSER);
            tvCallRepairRepairStartDate.setText(repairEvaluate.REPAIRSTARTDATE);
            tvCallRepairTime.setText(repairEvaluate.CALLREPAIR);
            tvCallRepairEquipmentState.setText(repairEvaluate.STATUS);
            if (evaluates.get(position).STATUS.equalsIgnoreCase("等待维修")){
                btCancelCallRepair.setVisibility(View.VISIBLE);
                btCancelCallRepair.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OthersUtil.showDoubleChooseDialog(CallRepairEvaluateActivity.this, "确认取消该笔叫修吗?", null, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelRepair(position);
                            }
                        });
                    }
                });
            }
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final String id = evaluates.get(position).CALLREPAIRITEMID;
                    if (evaluates.get(position).STATUS.equalsIgnoreCase("维修完成")){
                        AlertDialog alertDialog = new AlertDialog.Builder(CallRepairEvaluateActivity.this)
                                .setTitle("为了方便改进工作，请务必给出您宝贵的评价")
                                .setIcon(R.drawable.app_icon)
                                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {//添加单选框
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        index=i;
                                        Log.e("TAG","评价第"+i);
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        submitEvaluate(id);
                                    }
                                })

                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .create();
                        alertDialog.show();
                    }else {
                        OthersUtil.showTipsDialog(CallRepairEvaluateActivity.this,"当前状态为"+evaluates.get(position).STATUS+"无法评价");
                    }
                    return true;
                }
            });

            return convertView;
        }
    }

    private void cancelRepair(int Position) {
        final String itemID = evaluates.get(Position).CALLREPAIRITEMID;
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(CallRepairEvaluateActivity.this, hsWebInfo)
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),
                                        "spAppSbumitCallRepairRecord",
                                        "ActionType=" + "Cancel" +
                                                ",ItemID=" + itemID +
                                                ",AssetsCode=" + "" +
                                                ",POSTID=" + "" +
                                                ",CallRepairEmployeeID=" +"" +
                                                ",IssueDesc=" +""+
                                                ",Comments="+"",
                                        String.class.getName(),
                                        false,
                                        "helloWorld");
                            }
                        })
                ,CallRepairEvaluateActivity.this, dialog, new WebListener() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "success="+hsWebInfo.json);
                        OthersUtil.showTipsDialog(CallRepairEvaluateActivity.this,"取消成功");
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "error="+hsWebInfo.json);
                        evaluates.clear();
                        evaluateAdapter.notifyDataSetChanged();
                        initInfo();
                        OthersUtil.showTipsDialog(CallRepairEvaluateActivity.this,"取消成功");
                    }
                });

    }

    private void submitEvaluate(final String itemID) {
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(CallRepairEvaluateActivity.this, hsWebInfo)
                        .map(new Func1<HsWebInfo, HsWebInfo>() {
                            @Override
                            public HsWebInfo call(HsWebInfo hsWebInfo) {
                                return NewRxjavaWebUtils.getJsonData(getApplicationContext(),
                                        "spAppSbumitCallRepairRecord",
                                        "ActionType=" + "Complete" +
                                                ",ItemID=" + itemID +
                                                ",AssetsCode=" + "" +
                                                ",POSTID=" + "" +
                                                ",CallRepairEmployeeID=" +"" +
                                                ",IssueDesc=" +""+
                                                ",Comments="+"",
                                        String.class.getName(),
                                        false,
                                        "helloWorld");
                            }
                        })
                ,CallRepairEvaluateActivity.this, dialog, new WebListener() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "success="+hsWebInfo.json);
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo) {
                        Log.e("TAG", "error="+hsWebInfo.json);
                    }
                });
    }
}
