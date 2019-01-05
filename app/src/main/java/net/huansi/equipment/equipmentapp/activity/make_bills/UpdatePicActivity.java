package net.huansi.equipment.equipmentapp.activity.make_bills;

import android.content.Intent;
import android.widget.ImageView;

import net.huansi.equipment.equipmentapp.R;
import net.huansi.equipment.equipmentapp.activity.BaseActivity;
import net.huansi.equipment.equipmentapp.activity.LargerImageSHowActivity;
import net.huansi.equipment.equipmentapp.adapter.PictureAddCommonAdapter;
import net.huansi.equipment.equipmentapp.util.OthersUtil;
import net.huansi.equipment.equipmentapp.util.PictureAddDialogUtils;
import net.huansi.equipment.equipmentapp.widget.HorizontalListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;

import static net.huansi.equipment.equipmentapp.constant.Constant.LargerImageSHowActivityConstants.URL_PATH_PARAM;
import static net.huansi.equipment.equipmentapp.constant.Constant.PICTURE_ALL_NUMBER_PER;
import static net.huansi.equipment.equipmentapp.constant.Constant.PICTURE_UP_NUMBER_PER;
import static net.huansi.equipment.equipmentapp.util.PictureAddDialogUtils.ALBUM_PICTURE_REQUEST_CODE;
import static net.huansi.equipment.equipmentapp.util.PictureAddDialogUtils.CAMERA_PICTURE_REQUEST_CODE;

public class UpdatePicActivity extends BaseActivity{
    private PictureAddCommonAdapter mPictureAdapter;//图片的适配
    private List<String> mPathList;//图片地址的数组
    private PictureAddDialogUtils pictureAddDialogUtils;
    @BindView(R.id.hlvUpdatePicture)
    HorizontalListView hlvUpdatePicture;
    @BindView(R.id.imvUpdatePictureAdd)
    ImageView imvUpdatePictureAdd;
    @Override
    protected int getLayoutId() {
        return R.layout.update_pic_activity;
    }

    @Override
    public void init() {

        mPathList=new ArrayList<>();
        pictureAddDialogUtils=new PictureAddDialogUtils();
        mPictureAdapter=new PictureAddCommonAdapter(mPathList,getApplicationContext());
        hlvUpdatePicture.setAdapter(mPictureAdapter);
    }
    @OnClick(R.id.imvUpdatePictureAdd)
    void updatePic(){
        if(mPathList.size()>=PICTURE_UP_NUMBER_PER){
            OthersUtil.showTipsDialog(this,"每个流程只能上传四张，请检查！！");
            return;
        }
        pictureAddDialogUtils.showPictureAddDialog(this);
    }

    @OnItemClick(R.id.hlvUpdatePicture)
    void showLargerPicture(int position){
        Intent intent=new Intent(this, LargerImageSHowActivity.class);
        intent.putExtra(URL_PATH_PARAM,mPathList.get(position));
        startActivity(intent);
    }
    @OnItemLongClick(R.id.hlvUpdatePicture)
    boolean delPicture(int position){
        mPathList.remove(position);
        mPictureAdapter.notifyDataSetChanged();
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;
        switch (requestCode){
            //相册
            case ALBUM_PICTURE_REQUEST_CODE:
                pictureAddDialogUtils.initPictureAfterChooseByAlbum(data, this, mPathList);
                mPictureAdapter.notifyDataSetChanged();
                break;
            //拍照
            case CAMERA_PICTURE_REQUEST_CODE:
                pictureAddDialogUtils.initPictureAfterChooseByCamera(data, pictureAddDialogUtils.getPicturePath(), mPathList);
                mPictureAdapter.notifyDataSetChanged();
                break;
        }
    }
}
