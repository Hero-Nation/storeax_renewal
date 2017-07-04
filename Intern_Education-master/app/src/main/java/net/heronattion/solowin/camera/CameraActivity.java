package net.heronattion.solowin.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.heronattion.solowin.R;
import net.heronattion.solowin.activity.BaseActivity;
import net.heronattion.solowin.camera.Controll.ConvertGray;
import net.heronattion.solowin.camera.Data.PhotoByteData;
import net.heronattion.solowin.camera.Data.SensorData;
import net.heronattion.solowin.camera.util.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hero on 2017-04-11.
 */
//implements SensorEventListener : 기울기 센서 적용할 경우, 인터페이스를 적용해야한다.
public class CameraActivity extends BaseActivity {
    //카메라 기능 위한 멤버 변수 선언
    private static String TAG = "CAMERA";
    private Context mContext = this;
    private Camera mCamera;
    private CameraPreview mPreview;
    private ProgressDialog loading;
    private FrameLayout preview;
    ImageButton captureBtn;

    // 이미지 프로세싱을 위한 전역변수들
    public static int r; // 1/r만큼 crop하고 확대
    public static PhotoByteData photoByteData; // 사진의 data[]를 show2Activity에서 접근하기 위한 클래스
    public static Bitmap bitmap; // SortLineAndPaint에서 라인 그리는 메소드를 위한 전역 bitmap(절반으로 줄인 사진)

    // 센서 변수 선언
    private SensorManager mSensorManager; // 센서 메니저
    private Sensor accSensor; // 가속도 센서(중력 가속도)
    float accelXValue; // x축으로 받는 중력 가속도
    float accelYValue; // y축으로 받는 중력 가속도
    float accelZValue; // z축으로 받는 중력 가속도
    public static SensorData mSensor; // 센서 데이터 저장 클래스 선언
    LinearLayout linearLayout; // 인플레이터로 기울기 가이드 나타냄
    ImageView xOver, xUnder, yOver, yUnder; // 화살표로 이미지 가이드
    //boolean[] sensorFlag; 센서값에 만족 되는 경우만 촬영하게 하는 플래그
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 카메라 해상도 설정을 위해 디스플레이 해상도를 구한다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 강제 가로 모드
        mContext = this;
        // 카메라 사용여부 체크
        if (!checkCameraHardware(getApplicationContext())) {
            finish();
        }
        bindViews();
        setValues();
        setupEvents();


        // 디스플레이 체크
//        Display display = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE)).getDefaultDisplay() ;
//        int width = display.getWidth();
//        int height = display.getHeight();

        // 카메라가 지원하는 사진의 크기와
        // 안드로이드가 제공하는 프리뷰의 사이즈를 출력
        // 추후 이를 사용해 최적화된 해상도를 찾아야한다.(지금은 폰 디스플레이 해상도를 대입하는방식)
//        Camera.Parameters parameters = mCamera.getParameters();
//        if (parameters != null) {
//            List<Camera.ImageViewSizeData> pictureSizeList = parameters.getSupportedPictureSizes();
//            for (Camera.ImageViewSizeData imageViewSizeData : pictureSizeList) {
//                Log.d("##PictureSize##", "width: " + imageViewSizeData.width + "(" + width + ")" +
//                        "height :" + imageViewSizeData.height + "(" + height + ")");
//            } //지원하는 사진의 크기
//
//            List<Camera.ImageViewSizeData> previewSizeList = parameters.getSupportedPreviewSizes();
//            for (Camera.ImageViewSizeData imageViewSizeData : previewSizeList) {
//                Log.d("##previewSize##", "width: " + imageViewSizeData.width + "(" + width + ")" +
//                        "height :" + imageViewSizeData.height + "(" + height + ")");
//            } //지원하는 프리뷰 크기
//        }
    }

    @Override
    public void setValues() {
        super.setValues();

        // 카메라 인스턴스 생성
        mCamera = getCameraInstance();
        // 프리뷰창을 생성하고 액티비티의 레아이웃으로 지정
        mPreview = new CameraPreview(this, mCamera);

        // 가이드 이미지 삽입 대비용 반투명 이미지 삽입 테스트
        ImageView testImage = new ImageView(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(300, 300);
        layoutParams.gravity = Gravity.CENTER;
        testImage.setLayoutParams(layoutParams);
        testImage.setImageResource(R.drawable.icon_background_logo);
        testImage.setAlpha(40);

        // 촬영버튼 삽입
        captureBtn = new ImageButton(mContext);
        FrameLayout.LayoutParams btnparams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnparams.gravity = Gravity.RIGHT | Gravity.CENTER;
        captureBtn.setLayoutParams(btnparams);
        captureBtn.setImageResource(R.mipmap.camera_icon);

        // 레이아웃에 동적으로 프리뷰, 가이드 이미지, 촬영버튼 삽이
        preview.addView(mPreview);
        preview.addView(testImage);
        preview.addView(captureBtn);

        // 이미지 프로세싱 위한 멤버 변수들 선언언
        photoByteData = new PhotoByteData();
        r = 6;

        // 센서 변수 초기화
        mSensorManager = (SensorManager) getSystemService(mContext.SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = new SensorData();
//        textView = new TextView(mContext);
//        FrameLayout.LayoutParams txtparams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        txtparams.gravity = Gravity.BOTTOM | Gravity.CENTER;
//        textView.setLayoutParams(txtparams);
//        textView.setBackgroundColor(Color.WHITE);
//        preview.addView(textView);

        // 가속도 센서 값에 따라 촬영 가능 불가능 결정 플래그 (1111이면 촬영 가능)
//            sensorFlag = new boolean[4];
//        for(int i = 0 ; i <4 ; i++){
//            sensorFlag[i] = false;
//        }



    }

    @Override
    public void setupEvents() {
        super.setupEvents();

        // 촬영버튼 등록
        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {

                    }
                });
                return true;
            }
        });

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.autoFocus(new Camera.AutoFocusCallback() {

                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
//                        boolean x = true;
//                        // 1111인지 검사
//                        for(int i = 0 ; i < 4 ; i++){
//                            x &= sensorFlag[i];
//                        }
//                        System.out.println(x);
//                        if(x) // x가 true일때만 촬영
                        mCamera.takePicture(shutterCallback, null, mPicture);

                    }
                });

            }
        });


    }





    /**
     * 카메라 사용 및 파일 전송 클래스, 메소드
     */

    /**
     * 카메라 사용여부 가능 체크
     *
     * @param context
     * @return
     */

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.i(TAG, "Number of available camera : " + Camera.getNumberOfCameras());
            return true;
        } else {
            Toast.makeText(context, "No camera found!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 카메라 인스턴스 호출
     *
     * @return
     */

    public Camera getCameraInstance() {
        try {
            // open() 의 매개변수로 int 값을 받을 수 도 있는데, 일반적으로 0이 후면 카메라, 1이 전면 카메라를 의미합니다.
            mCamera = Camera.open();
        } catch (Exception e) {
            Log.i(TAG, "Error : Using Camera");
            e.printStackTrace();
        }
        return mCamera;
    }

    // 찰칵 소리 -> shutterCallback
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
//            Toast.makeText(mContext.getApplicationContext(),"Loading....", Toast.LENGTH_LONG).show();
            loading = new ProgressDialog(mContext);
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("Loading...");
            loading.show();
            onPause();
            Log.d(TAG, "onShutter'd");
        }
    };


    /* jpeg callback 클래스
     이미지 프로세싱을 실행시킨다.
     여기서 이미지는 data[] 형태. 비트맵으로 변환시킨다음, ConvertGray 클래스로 넘겨주자
     이미지 프로세싱 다 되면, 카드의 모서리(교점)들의 정보가 float이중 배열 형태로 저장
     이중 배열을 Show2Activity에서 받아, dotView를 생성한다.
    */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 무음용....
//            loading = new ProgressDialog(mContext);
//            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            loading.setMessage("Loading...");
//            loading.show();

            //회전 시도
            //이미지의 너비와 높이 결정
//            int w = camera.getParameters().getPictureSize().width;
//            int h = camera.getParameters().getPictureSize().height;
//
//            int orientation = setCameraDisplayOrientation(CameraActivity.this,
//                    CAMERA_FACING, camera);
//
//            //byte array를 bitmap으로 변환
            // data[]를 bitmap으로 변환
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            // 그냥 집어넣으면 이미지 용량으로 인해 OOM 발생, 1/2배로 줄이자
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // 이미지 프로세싱 시작
            ConvertGray convertGray = new ConvertGray();
            convertGray.mOrgImage = bitmap;
            convertGray.cropAndResize();

            // 프로세싱된 결과를 바이트 어레이로 다시 변환 -> 변경됨
            // Show2Activity에서 원본 이미지를 보여주고 그 위에 float이중배열로 받은 좌표로 점을 찍기때문에
            // 절반 크기로 줄인 원본 이미지를 넘겨주자.
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] currentData = stream.toByteArray();
            // 다음 액티비티는 원본이미지 보낸다.
            photoByteData.setPhotoInfo(currentData);

            Intent intent = new Intent(CameraActivity.this, Show2Activity.class);
            intent.putExtra("caseFlag","1");
            startActivity(intent);
            loading.dismiss();
            finish();
//            new SaveImageTask().execute(currentData);

//            //int w = bitmap.getWidth();
//            //int h = bitmap.getHeight();
//
//            //이미지를 디바이스 방향으로 회전
//            Matrix matrix = new Matrix();
//            matrix.postRotate(orientation);
//            bitmap =  Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
//
//            //bitmap을 byte array로 변환
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            byte[] currentData = stream.toByteArray();
//            new SaveImageTask().execute(currentData);
            // 회전안하고 데이터 넘기기
//            new SaveImageTask().execute(data);

            // JPEG 이미지가 byte[] 형태로 들어옵니다.
        }
    };


    // 파일 저장 과정을 AsyncTask사용하자
    // 이 어플은 이미지 저장을 안하기 때문에 사용X
    private class SaveImageTask extends AsyncTask<byte[], Void, String> {

        @Override
        protected void onPreExecute() {
            mCamera.stopPreview();

            super.onPreExecute();
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
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            return pictureFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String s) {
            loading.dismiss();
            super.onPostExecute(s);

            System.out.println(s);
//            Intent intent = new Intent(CameraActivity.this, ShowActivity.class);
//            intent.putExtra("FILE_PATH",s);
//            startActivity(intent);
            Intent intent = new Intent(CameraActivity.this, Show2Activity.class);
            intent.putExtra("FILE_PATH", s);
            startActivity(intent);
            finish();

        }
    }

    /**
     * 이미지를 저장할 파일 객체를 생성
     * 저장되면 Picture 폴더에 MyCameraApp 폴더안에 저장된다. (MyCameraApp 폴더명은 변경가능)
     */

    private static File getOutputMediaFile() {
        //SD 카드가 마운트 되어있는지 먼저 확인
        // Environment.getExternalStorageState() 로 마운트 상태 확인 가능합니다
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
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

    /**
     * 화면 회전 계산해주는 함수
     * 카메라 프리뷰에 가이드 이미지 띄우기 때문에 당장 사용할 일없다.
     * 혹시 몰라서 지우지는 않음
     *
     * @param activity
     * @param cameraId Camera.CameraInfo.CAMERA_FACING_FRONT,
     *                 Camera.CameraInfo.CAMERA_FACING_BACK
     * @param camera   Camera Orientation
     *                 reference by https://developer.android.com/reference/android/hardware/Camera.html
     */
    public static int setCameraDisplayOrientation(Activity activity,
                                                  int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }


    // 센서 오버라이드 메소드
    // 센서값이 변할때 이벤트
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        // 센서 감지 이벤트 발생할때마다 뷰를 업데이트한다.
//        // 멤버 변수로 선언시, 처음에 센서 조건만족하면 그 이후로 레이아웃이 추가가 안되는 버그 발생
//        preview.removeView(linearLayout);
//        linearLayout = (LinearLayout)View.inflate(mContext,R.layout.accel_icon_item,null);
//        xOver = (ImageView)linearLayout.findViewById(R.id.XOver); xOver.setVisibility(View.VISIBLE);
//        xUnder = (ImageView)linearLayout.findViewById(R.id.XUnder);xUnder.setVisibility(View.VISIBLE);
//        yOver = (ImageView)linearLayout.findViewById(R.id.YOver);yOver.setVisibility(View.VISIBLE);
//        yUnder = (ImageView)linearLayout.findViewById(R.id.YUnder);yUnder.setVisibility(View.VISIBLE);
//        preview.addView(linearLayout);
//
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            accelXValue = event.values[0];
//            accelYValue = event.values[1];
//            accelZValue = event.values[2];
//
//            // 다른 액티비티에서 접근이 가능하도록 전역 클래스로 저장
//            mSensor.setAccelXValue(accelXValue);
//            mSensor.setAccelYValue(accelYValue);
//            mSensor.setAccelZValue(accelZValue);
////            textView.setText(mSensor.getAccelXValue() + " / " + mSensor.getAccelYValue() + " / " + mSensor.getAccelZValue());
//        Log.i("SensorValue",accelXValue + " / " + accelYValue + " / " + accelZValue);
//            if(accelXValue>0.3f){
//                xOver.setVisibility(View.VISIBLE);
//                sensorFlag[0] = false;
//            }else{
//                xOver.setVisibility(View.INVISIBLE);
//                sensorFlag[0] = true;
//            }
//            if(accelXValue<-0.3f){
//                xUnder.setVisibility(View.VISIBLE);
//                sensorFlag[1] = false;
//            }else{
//                xUnder.setVisibility(View.INVISIBLE);
//                sensorFlag[1] = true;
//            }
//            if(accelYValue>0.3f){
//                yOver.setVisibility(View.VISIBLE);
//                sensorFlag[2] = false;
//            }else{
//                yOver.setVisibility(View.INVISIBLE);
//                sensorFlag[2] = true;
//            }
//            if(accelYValue<-0.3f){
//                yUnder.setVisibility(View.VISIBLE);
//                sensorFlag[3] = false;
//            }else{
//                yUnder.setVisibility(View.INVISIBLE);
//                sensorFlag[3] = true;
//            }
//        }
//    }
//
//    // 센서의 정확도에 대한 메소드
//    // 센서 인터페이스에 딸려있는 추상 메소드
//    // 쓸일 없으니 노타치
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//
//    // 센서 리스너 등록
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
//
//    }
//    // 센서 리스너 반환
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mSensorManager.unregisterListener(this);
//    }


    @Override
    public void bindViews() {
        super.bindViews();
        preview = (FrameLayout) findViewById(R.id.camera_preview);

    }


}
