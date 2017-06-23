package com.example.lenovo.at;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2017/6/5.
 */
public class ChooseAvatar extends AppCompatActivity {

    private Button btn_selectAvatar;
    private ImageView iv_avatar;
    private Menu mMenu;
    private Bitmap photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar);

        initial();
        initialToolBar();
    }

    private void initial() {
        btn_selectAvatar = (Button) findViewById(R.id.btn_selectAvatar);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);

        try (FileInputStream fis = openFileInput("avatar.png")) {
            byte[] contents = new byte[fis.available()];
            fis.read(contents);
            fis.close();
            iv_avatar.setImageBitmap(BitmapFactory.decodeByteArray(contents, 0, contents.length));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        btn_selectAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(pickIntent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:// 直接从相册获取
                    try {
                        startPhotoZoom(data.getData());
                    } catch (NullPointerException e) {
                        e.printStackTrace();// 用户点击取消操作
                    }
                    break;
                case 1:// 取得裁剪后的图片
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            iv_avatar.setImageDrawable(drawable);
        }
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
    }

    private void initialToolBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("更换头像");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.share_menu).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                saveAvatar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveAvatar() {
        if (photo == null) {
            finish();
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("avatar.png", MODE_PRIVATE);
            photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }
}
