package net.huansi.equipment.equipmentapp.activity.check_simple;

import android.content.Intent;

import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.BaseActivity;
import net.huansi.equipment.equipmentapp.activity.move_cloth.ClothMoveMainActivity;
import net.huansi.equipment.equipmentapp.activity.move_cloth.ClothQueryRecordActivity;

import butterknife.OnClick;

public class CheckSimpleDepartmentActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_check_simple_department;
    }

    @Override
    public void init() {
        setToolBarTitle("选择部门");

    }
    @OnClick(R.id.CheckSample_designer)//
    void toDesigner(){
        Intent intent=new Intent(this,CheckSimplePendingActivity.class);
        intent.putExtra("UNITTYPE","0");
        startActivity(intent);
    }
    @OnClick(R.id.CheckSample_checker)
    void toChecker(){
        Intent intent=new Intent(this,CheckSimplePendingActivity.class);
        intent.putExtra("UNITTYPE","1");
        startActivity(intent);
    }
    @OnClick(R.id.CheckSample_monitor)
    void toMonitor(){
        Intent intent=new Intent(this,CheckSimplePendingActivity.class);
        intent.putExtra("UNITTYPE","2");
        startActivity(intent);
    }
    @OnClick(R.id.CheckSample_QC)
    void toQc(){
        Intent intent=new Intent(this,CheckSimplePendingActivity.class);
        intent.putExtra("UNITTYPE","3");
        startActivity(intent);
    }
}
