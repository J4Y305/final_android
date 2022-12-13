package com.example.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    myPictureView myPicture;
    MyGraphicView graphicView;
    static float scaleX = 1, scaleY = 1, angle = 0, color = 1, satur = 1;
    ViewFlipper vFlipper;
    Button btnPrev, btnNext, btnPrevPic, btnNextPic, btnPhoto, btnSave;
    File[] imageFiles;
    String imageFname;
    int curNum = 0;
    final static int LINE = 1, CIRCLE = 2, RECT = 3, penStroke = 4;
    static int curShape = LINE;
    SeekBar SeekBar;
    String imgName = "Edit.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("미니 포토샵");

        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        registerForContextMenu(btnPhoto);

        LinearLayout pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);
        graphicView = (MyGraphicView) new MyGraphicView(this);
        pictureLayout.addView(graphicView);

        SeekBar = (SeekBar) findViewById(R.id.SeekBar);

        vFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnSave = (Button) findViewById(R.id.btnSave);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vFlipper.showPrevious();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vFlipper.showNext();
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        myPicture = (myPictureView) findViewById(R.id.myPictureView1);
        btnPrevPic = (Button) findViewById(R.id.btnPrevPic);
        btnNextPic = (Button) findViewById(R.id.btnNextPic);
        imageFiles = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures").listFiles();
        imageFname = imageFiles[curNum].toString();
        myPicture.imagePath = imageFname;

        btnPrevPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curNum <= 0)
                    Toast.makeText(getApplicationContext(), "첫번째 그림입니다.", Toast.LENGTH_SHORT).show();
                else curNum--;
                setMyPicture();
            }
        });

        btnNextPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curNum >= imageFiles.length - 1)
                    Toast.makeText(getApplicationContext(), "마지막 그림입니다.", Toast.LENGTH_SHORT).show();
                else curNum++;
                setMyPicture();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePicture();
            }
        });
    }

    private void savePicture() {
        graphicView.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(graphicView.getDrawingCache());
        graphicView.setDrawingCacheEnabled(false);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(!dir.exists())
            dir.mkdirs();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(dir, "EDIT.png"));
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Toast.makeText(this, "저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("photo ERR","그림저장오류",e);
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void setMyPicture() {
        imageFname = imageFiles[curNum].toString();
        myPicture.imagePath = imageFname;
        myPicture.invalidate();
    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v==btnPhoto){
            menu.add(0,1,0,"선");
            menu.add(0,2,0,"원");
            menu.add(0,3,0,"네모");
            menu.add(0,4,0,"펜 스트로크");
        }
    }


    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                curShape = LINE;
                return true;
            case 2:
                curShape = CIRCLE;
                return true;
            case 3:
                curShape = RECT;
                return true;
            case 4:
                curShape = penStroke;
                return true;
        }
        return false;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.zoomin:
                scaleX = scaleX + 0.2f;
                scaleY = scaleY + 0.2f;
                myPicture.invalidate();
                break;
            case R.id.zoomout:
                scaleX = scaleX - 0.2f;
                scaleY = scaleY - 0.2f;
                myPicture.invalidate();
                break;
            case R.id.rotation:
                angle = angle + 20;
                myPicture.invalidate();
                break;
            case R.id.bright:
                color = color + 0.2f;
                myPicture.invalidate();
                break;
            case R.id.dark:
                color = color - 0.2f;
                myPicture.invalidate();
                break;
            case R.id.gray:
                if (satur == 0) satur = 1;
                else satur = 0;
                myPicture.invalidate();
                break;
        }
        return true;
    }

    private class MyGraphicView extends View {
        int startX = -1, startY = -1, stopX = -1, stopY = -1;

        public MyGraphicView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    this.invalidate();
                    break;
            }

            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            float[] array = {color, 0, 0, 0, 0,
                    0, color, 0, 0, 0,
                    0, 0, color, 0, 0,
                    0, 0, 0, 1, 0};
            ColorMatrix cm = new ColorMatrix(array);
            if (satur == 0) cm.setSaturation(satur);
            paint.setColorFilter(new ColorMatrixColorFilter(cm));

            Bitmap picture = BitmapFactory.decodeFile(imageFname);
            int picX = (this.getWidth() - picture.getWidth()) / 2;
            int picY = (this.getHeight() - picture.getHeight()) / 2;
            int cenX = this.getWidth() / 2;
            int cenY = this.getHeight() / 2;
            canvas.scale(scaleX, scaleY, cenX, cenY);
            canvas.rotate(angle, cenX, cenY);
            canvas.drawBitmap(picture, picX, picY, paint);
            picture.recycle();

            paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.STROKE);

            switch (curShape) {
                case LINE:
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    break;
                case CIRCLE:
                    int radius = (int) Math.sqrt(Math.pow(stopX - startX, 2) + Math.pow(stopY - startY, 2));
                    canvas.drawCircle(startX, startY, radius, paint);
                    break;
                case RECT:
                    canvas.drawRect(new Rect(startX, startY, stopX, stopY), paint);
                    break;
                case penStroke:
                    paint.setStrokeWidth(SeekBar.getProgress());
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
            }
        }
    }
}