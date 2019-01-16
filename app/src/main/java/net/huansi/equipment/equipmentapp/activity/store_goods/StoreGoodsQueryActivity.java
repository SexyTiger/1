package net.huansi.equipment.equipmentapp.activity.store_goods;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.BaseActivity;
import net.huansi.equipment.equipmentapp.entity.BusStoreEvent;
import net.huansi.equipment.equipmentapp.entity.HsWebInfo;
import net.huansi.equipment.equipmentapp.entity.StoreGoodsDetail;
import net.huansi.equipment.equipmentapp.entity.StoreGoodsSummary;
import net.huansi.equipment.equipmentapp.entity.WsEntity;
import net.huansi.equipment.equipmentapp.event.MessageEvent;
import net.huansi.equipment.equipmentapp.listener.WebListener;
import net.huansi.equipment.equipmentapp.util.NewRxjavaWebUtils;
import net.huansi.equipment.equipmentapp.widget.LoadProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static net.huansi.equipment.equipmentapp.entity.BusStoreEvent.Type.DETAIL_SUCCESS;
import static net.huansi.equipment.equipmentapp.entity.BusStoreEvent.Type.SUM_SUCCESS;

public  class StoreGoodsQueryActivity extends BaseActivity {
    @BindView(R.id.vp_container)
    ViewPager mcContainer;
    @BindView(R.id.goodsSummary)
    TextView goodsSummary;
    @BindView(R.id.goodsDetail)
    TextView goodsDetail;
    @BindView(R.id.et_ClientPo)
    EditText et_ClientPo;
    @BindView(R.id.store_query)
    Button store_query;
    private List<Fragment> fragmentList;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private List<TextView> textViewList;
    private LoadProgressDialog dialog;
    private List<StoreGoodsDetail> lvGoodsDetail=new ArrayList<>();
    private List<StoreGoodsSummary> lvGoodsSummary=new ArrayList<>();
    private int mDefaultColor= Color.BLACK;

    private int mActiveColor=Color.RED;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_store_goods_query;
    }
    @Override
    public void init() {
        setToolBarTitle("查询页面");
        dialog=new LoadProgressDialog(this);
        ButterKnife.bind(this);
        fragmentList=new ArrayList<Fragment>();
        fragmentList.add(new FragmentSummary());
        fragmentList.add(new FragmentDetail());

        textViewList=new ArrayList<TextView>();
        textViewList.add(goodsSummary);
        textViewList.add(goodsDetail);
        textViewList.get(0).setTextColor(mActiveColor);
        fragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList != null ? fragmentList.get(i) : null;
            }

            @Override
            public int getCount() {
                return fragmentList != null ? fragmentList.size() : 0;
            }
        };
        mcContainer.setAdapter(fragmentPagerAdapter);
        mcContainer.setAlpha(1);
        mcContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for (TextView viewer :
                        textViewList) {
                    viewer.setTextColor(mDefaultColor);
                }
                textViewList.get(position).setTextColor(mActiveColor);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.store_query)
    void storeQuery(){
        //et_ClientPo.getText().clear();
        String PO = et_ClientPo.getText().toString();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(StoreGoodsQueryActivity.this,"")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getApplicationContext(), "spAppProductStorageMove_Search",
                                "CustomerPO=" + "3502264764" +
                                        ",Type=" + "Detail", String.class.getName(), false, "组别获取成功");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()), getApplicationContext(), dialog, new WebListener() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                String json = hsWebInfo.json;
                Log.e("TAG","查询结果det="+json);
                List<WsEntity>  entities=hsWebInfo.wsData.LISTWSDATA;
                for(WsEntity wsEntity:entities){
                    StoreGoodsDetail storeDetail = (StoreGoodsDetail) wsEntity;
                    lvGoodsDetail.add(storeDetail);
                }
                Log.e("TAG",lvGoodsDetail.toString());
                EventBus.getDefault().post(new BusStoreEvent(DETAIL_SUCCESS,lvGoodsDetail));
            }
            @Override
            public void error(HsWebInfo hsWebInfo) {
                Log.e("TAG","error="+hsWebInfo.json);
            }
        });


        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(StoreGoodsQueryActivity.this,"")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getApplicationContext(), "spAppProductStorageMove_Search",
                                "CustomerPO=" + "3502264764" +
                                        ",Type=" + "Sum", String.class.getName(), false, "组别获取成功");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()), getApplicationContext(), dialog, new WebListener() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                String json = hsWebInfo.json;
                Log.e("TAG","查询结果sum="+json);
                List<WsEntity>  entities=hsWebInfo.wsData.LISTWSDATA;
                for(WsEntity wsEntity:entities){
                    StoreGoodsSummary storeSummary = (StoreGoodsSummary) wsEntity;
                    lvGoodsSummary.add(storeSummary);
                }
                Log.e("TAG",lvGoodsSummary.toString());
                EventBus.getDefault().post(new BusStoreEvent(SUM_SUCCESS,lvGoodsSummary));
            }
            @Override
            public void error(HsWebInfo hsWebInfo) {
                Log.e("TAG","error="+hsWebInfo.json);
            }
        });

    }
}
