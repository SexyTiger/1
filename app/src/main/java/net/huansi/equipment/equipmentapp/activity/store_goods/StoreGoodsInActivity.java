package net.huansi.equipment.equipmentapp.activity.store_goods;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Joiner;

import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.BaseActivity;
import net.huansi.equipment.equipmentapp.entity.HsWebInfo;
import net.huansi.equipment.equipmentapp.listener.WebListener;
import net.huansi.equipment.equipmentapp.util.NewRxjavaWebUtils;
import net.huansi.equipment.equipmentapp.util.OthersUtil;
import net.huansi.equipment.equipmentapp.util.SPHelper;
import net.huansi.equipment.equipmentapp.widget.LoadProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static net.huansi.equipment.equipmentapp.util.SPHelper.USER_NO_KEY;

public class StoreGoodsInActivity extends BaseActivity {
    private LoadProgressDialog dialog;
    private List<String> billList;
    private ArrayAdapter billAdapter;
    @BindView(R.id.tv_StoreNumber)
    TextView storeNumber;
    @BindView(R.id.lvBillNumberList)
    ListView lvBillNumber;
    @BindView(R.id.et_StoreNumber)
    EditText et_StoreNumber;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_store_goods_in;
    }

    @Override
    public void init() {
        setToolBarTitle("入库");
        dialog=new LoadProgressDialog(this);
        billList=new ArrayList<>();
    }

    @OnClick(R.id.store_clear)
    void clearStorage(){
        storeNumber.setText("");
    }
    @OnItemClick(R.id.lvBillNumberList)
    void delete(final int position){
        OthersUtil.showDoubleChooseDialog(StoreGoodsInActivity.this, "确认删除该单据号?", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                billList.remove(position);

                billAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.store_bill_bind)
    void bindStore2Bill(){
        OthersUtil.showDoubleChooseDialog(StoreGoodsInActivity.this, "确认绑定吗?", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (storeNumber.getText().toString().isEmpty()&&billList.isEmpty()){
                    OthersUtil.ToastMsg(StoreGoodsInActivity.this,"内容为空无法绑定!");
                    return;
                }
                final String join = Joiner.on(";").join(billList);
                Log.e("TAG","billList="+ join);
                final String user = SPHelper.getLocalData(getApplicationContext(), USER_NO_KEY, String.class.getName(), "").toString();
                OthersUtil.showLoadDialog(dialog);
                try {

                    NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(StoreGoodsInActivity.this,"")
                            .map(new Func1<String, HsWebInfo>() {
                                @Override
                                public HsWebInfo call(String s) {
                                    return NewRxjavaWebUtils.getJsonData(getApplicationContext(),"spAppProductStorageMove",
                                            "Type="+Integer.parseInt("0")+
                                                    ",OrderCode="+ join+//单号或箱号
                                                    ",Origin_Position="+""+
                                                    ",Current_Position="+storeNumber.getText().toString()+
                                                    ",UserID="+user,String.class.getName(),false,"组别获取成功");
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io()), getApplicationContext(), dialog, new WebListener() {
                        @Override
                        public void success(HsWebInfo hsWebInfo) {
                            String json = hsWebInfo.json;
                            Log.e("TAG","sizesJson="+json);
                            OthersUtil.ToastMsg(getApplicationContext(),"绑定成功");
                            billList.clear();
                            billAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void error(HsWebInfo hsWebInfo) {
                            Log.e("TAG","error="+hsWebInfo.json);
                        }
                    });
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        // 获取剪贴板的剪贴数据集
        ClipData clipData = clipboard.getPrimaryClip();
        if (storeNumber.getText().toString().isEmpty()){
           storeNumber.setText( et_StoreNumber.getText().toString());
           et_StoreNumber.getText().clear();
//            if (clipData != null && clipData.getItemCount() > 0) {
//                // 从数据集中获取（粘贴）第一条文本数据
//                CharSequence text = clipData.getItemAt(0).getText();
//                Log.e("TAG","text=" + text);
//                storeNumber.setText(text);
//            }
        }else {
            //if (clipData != null && clipData.getItemCount() > 0) {
                // 从数据集中获取（粘贴）第一条文本数据
                CharSequence text = clipData.getItemAt(0).getText();
                String s = et_StoreNumber.getText().toString();
                if (!billList.contains(s)&&!s.isEmpty()){
                    billList.add(s);
                }
                billAdapter=new ArrayAdapter(getApplicationContext(),R.layout.string_item,R.id.text,billList);
                lvBillNumber.setAdapter(billAdapter);
                et_StoreNumber.getText().clear();
            //}

        }
        return super.onKeyUp(keyCode, event);

    }
}
