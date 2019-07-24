package net.huansi.equipment.equipmentapp.activity.aux_material;

import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.BaseActivity;

public class AuxMaterialInActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_aux_material_in;
    }

    @Override
    public void init() {
        setToolBarTitle("辅料入库");
    }
}
