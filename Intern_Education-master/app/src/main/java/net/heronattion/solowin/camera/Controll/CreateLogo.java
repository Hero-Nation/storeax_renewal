package net.heronattion.solowin.camera.Controll;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import net.heronattion.solowin.R;
import net.heronattion.solowin.camera.Data.DotData;
import net.heronattion.solowin.camera.util.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import georegression.struct.point.Point2D_F32;
import georegression.struct.point.Vector2D_F32;

/**
 * Created by Hero on 2017-04-24.
 * 길이를 측정하면서 이미지  파일 저장
 * 이때, 개인정보가 있는 카드를 지우기위해 회사 로고를 박는다.
 */

public class CreateLogo {

    public Context mContext;

    private Bitmap baseBitmap; // 원본 이미지
    private ArrayList<DotData> cardDotList; // 카드 모서리 좌표 저장 클래스

    public Bitmap getBaseBitmap() {
        return baseBitmap;
    }

    public void setBaseBitmap(Bitmap baseBitmap) {
        this.baseBitmap = baseBitmap;
    }

    public ArrayList<DotData> getCardDotList() {
        return cardDotList;
    }

    public void setCardDotList(ArrayList<DotData> cardDotList) {
        this.cardDotList = cardDotList;
    }


    /* 프로세스 순서
     1. 카드좌표들의 중점을 구한다.
     2. 중점과 임의의 한점 사이의 거리를 구한다(대각선의 반)
     3. 2의 결과를 2배하여 로고 이미지를 그만큼 확대한다.(대각선으로 하면 확실하게 다 가려짐)
     4. 확대한 로고 이미지를 drawBitmap을 써서 원본 카드 좌표들의 중심에 박는다.
     5. 비트맵을 바이트 어레이로 전환한 다음, External storage에 저장한다.
    */
    // 전체 메소드 실행
    public void logoProcess() {
        // 1번 //
        Point2D_F32 center = makeCenter(cardDotList);
        // 2번 //
        Point2D_F32 a = new Point2D_F32(cardDotList.get(0).x * baseBitmap.getWidth(), cardDotList.get(0).y * baseBitmap.getHeight());
        float radius  = getLength(center, a);
        // 3~5번//
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        makeImage(center,radius).compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        SaveImageTask saveImageTask = new SaveImageTask();
        saveImageTask.execute(data);

    }


    // 카드의 좌표를 Point2D_F32로 바꾸는 메소드
    public Point2D_F32 makeCenter(ArrayList<DotData> arrayList) {
        Point2D_F32[] cardPoint = new Point2D_F32[4];
        Point2D_F32 centerPoint;
        // 퍼센트, 즉 상대 좌표로 되어있는 좌표들을 절대 좌표로 환산한다.
        for (int i = 0; i < arrayList.size(); i++) {
            cardPoint[i] = new Point2D_F32(baseBitmap.getWidth() * arrayList.get(i).x, baseBitmap.getHeight() * arrayList.get(i).y);
        }
        // 중점 구하기 -> 카드가 안기울어졌다는 가정하에, 걍 (중점, 중점) 사용
        // 나중에 회전된 카드의 경우는 대각선의 교점을 써야할 듯
        centerPoint = new Point2D_F32((cardPoint[0].x + cardPoint[1].x )/2, (cardPoint[0].y + cardPoint[3].y)/2);

        return centerPoint;
    }

    //중점과 한 점 사이의 거리
    public float getLength(Point2D_F32 a, Point2D_F32 b) {

        float length;
        // 벡터의 내적을 이용해서 두 점사이의 거리르 구했다.
        Vector2D_F32 vec = new Vector2D_F32(b.x - a.x, b.y - a.y);
        length = (float) Math.sqrt(vec.dot(vec));
        return length;
    }

    // 로고 이미지 붙이기
    public Bitmap makeImage(Point2D_F32 center, float length) {
        // 원본 이미지 카피
        Bitmap copyBitmap = baseBitmap.copy(Bitmap.Config.ARGB_8888, true);
        // 로고 이미지 불러오기 & 리사이즈
        Bitmap logo = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.herologo);
        logo = BitmapUtil.resizeBitmap(logo, 2*(int) length);

        // 캔버스의 drawbitmap 사용
        Canvas canvas = new Canvas(copyBitmap);
        canvas.drawBitmap(logo, center.x - logo.getWidth() / 2, center.y - logo.getHeight()/2, new Paint());

        return copyBitmap;

    }

    // 파일 저장 과정을 AsyncTask사용하자
    private class SaveImageTask extends AsyncTask<byte[], Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(mContext);
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("Loading...");
            loading.show();
        }

        @Override
        protected String doInBackground(byte[]... params) {
            byte[] currentData = params[0];

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Toast.makeText(mContext, "Error camera image saving", Toast.LENGTH_SHORT).show();
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(currentData);
                fos.close();
                //Thread.sleep(500);
//                mCamera.startPreview();
            } catch (FileNotFoundException e) {
                Log.d("LOGO", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("LOGO", "Error accessing file: " + e.getMessage());
            }
            return pictureFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            Toast.makeText(mContext,"사진이 저장되었습니다.",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 이미지를 저장할 파일 객체를 생성
     * 저장되면 Picture 폴더에 MyCameraApp 폴더안에 저장된다. (MyCameraApp 폴더명은 변경가능)
     */

    private static File getOutputMediaFile() {
        //SD 카드가 마운트 되어있는지 먼저 확인
        // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "StoreAx");
        // 없는 경로라면 따로 생성
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCamera", "failed to create directory");
                return null;
            }
        }

        // 파일명을 적당히 생성, 여기선 시간으로 파일명 중복을 피한다
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
        Log.i("MyCamera", "Saved at" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        System.out.println(mediaFile.getPath());
        return mediaFile;
    }


}
