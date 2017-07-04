package net.heronattion.solowin.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;

import net.heronattion.solowin.R;
import net.heronattion.solowin.activity.BaseActivity;
import net.heronattion.solowin.activity.CameraMeasureActivity;
import net.heronattion.solowin.activity.SignupInformation3Activity;
import net.heronattion.solowin.camera.Controll.CreateLogo;

import net.heronattion.solowin.camera.Data.DotData;
import net.heronattion.solowin.camera.Data.PhotoByteData;

import java.text.DecimalFormat;
import java.util.ArrayList;

import georegression.struct.point.Point2D_F32;
import georegression.struct.point.Vector2D_F32;

import static net.heronattion.solowin.activity.SignupInformation3Activity.savedCardDotList;

public class Show2Activity extends BaseActivity {

    private ImageView resultImage;
    private PhotoView photoImg2;
    private FrameLayout showFrameLayout2;
    private Button eraseDotBtn2;
    private Button decideSizeBtn2;
    private TextView beforeImageSizeTxt2;
    private TextView processingImageSizeTxt2;
    private TextView processingPhotoViewSizeTxt2;
    private TextView FrameSizeTxt;
    private TextView rectSizeTxt2;
    private TextView cardLengthTxt2;

    Bitmap resultBitmap;
    //이미지 프로세싱 결과물과 포토뷰에 점 찍기에 대한 멤버 변수
    public static float[][] ipPercent; // SortLineAndPaint결과로 나온 교점들 저장하는 float이중 배열
    RectF mRectF; // 포토뷰에 들어갈 이미지의 RectF정보 저장
    ImageView imageView; // 가이드라인 위한 이미지뷰
    ArrayList<DotData> cardDotList; // 카드에 대해 찍힌 점 저장하는 리스트
    ArrayList<DotData> outDotList; // 카드 외에 비교 대상 찍힌 점 저장하는 리스트

    float scaleValue; // 점의 확대를 위한 상수
    Bitmap cardDotbit; // 카드에 대해 찍힌 점 이미지
    Bitmap outDotbit; // 비교대상 점 이미지
    boolean windowfoucsflag = false;
//    private Button logoBtn;


    String caseFlag;

    @Override
    public void setCustomActionBar() {
        super.setCustomActionBar();
        titleTxt.setText("측정결과");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show2);

        setCustomActionBar();
        bindViews();
        setValues();
        setupEvents();

        windowfoucsflag = true;
    }


    // onCreate로 넣으면 PhotoView의 사이즈를 0으로 인식한다.
    // onWindowFocusChanged -> 현재 Activity의 focus여부를 확인
    // 즉 onCreate 다음 실행되는 것과 같은 상태
    // 주의 : Activity의 focus를 확인하기 때문에 액티비티가 이동되거나 화면이 꺼졌다 켜질때 마다 실행
    // 점을 지우는 반복문을 집어넣어서 여러개가 안찍히도록 한다.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i("mRectF...", mRectF + "");

        if (windowfoucsflag) {
            createDotView();
            windowfoucsflag = false;
        }

    }

    @Override
    public void setValues() {
        super.setValues();
        // show2Activity로 넘어오는 경우 총 3가지
        // 1. 회원가입 최초 측정
        // 2. 회원가입 추가 입력
        // 3. 마이페이지 추가 입력
        // getExtra로 구별하자.
        Intent intent = getIntent();
        caseFlag = intent.getStringExtra("caseFlag");


//        사진 찍자마자 저장된 사진을 불러와서 작업할 경우, 파일의 저장된 경로를 인텐트로 가져왔다.
//        지금은 사진의 바이트 어레이를 가져와서 비트맵 전환후 작업 실행
//        Intent intent = getIntent();
//        path = intent.getStringExtra("FILE_PATH");

        // 사진의 바이트 어레이 접근해서 가져온다.
        PhotoByteData result = CameraActivity.photoByteData;
        byte[] resultImageByte = result.getPhotoInfo();
        // 가져온 바이트 어레이를 비트맵으로 변환 -> 여기서의 바이트 어레이는 이미지 프로세싱이 끝난 상태
        resultBitmap = BitmapFactory.decodeByteArray(resultImageByte, 0, resultImageByte.length);
        photoImg2.setImageBitmap(resultBitmap);

        // 카메라 액티비티의 r을 가져온다 -> 최대 r배만큼 줌인 가능
        photoImg2.setMaximumScale(CameraActivity.r);
        photoImg2.setMinimumScale(1.0f);

        // 카드 모서리 점 찍은 정보 저장 리스트
        cardDotList = new ArrayList<>();
        // 측정대상 점 찍은 정보 저장 리스트
        outDotList = new ArrayList<>();

        // 줌인 했을때, dotView들도 따라서 이동하게 해주는 값
        scaleValue = 1;

        cardDotbit = BitmapFactory.decodeResource(getResources(), R.drawable.dotimage3);
        outDotbit = BitmapFactory.decodeResource(getResources(), R.drawable.dotimage4);
//        photoImg2.setScale(r,resultBitmap.getWidth()/2,resultBitmap.getHeight()/2,true);

        // 가이드 라인 그려주는 이미지뷰
        imageView = new ImageView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        mRectF = photoImg2.getDisplayRect();

        Log.i("mRectF...", mRectF + "");

        //카메라 액티비티의 마지막 센서 값을 뿌려준다.
        rectSizeTxt2.setText("x : " + CameraActivity.mSensor.getAccelXValue() +
                "y : " + CameraActivity.mSensor.getAccelYValue() +
                "z : " + CameraActivity.mSensor.getAccelZValue());

    }

    @Override
    public void setupEvents() {
        super.setupEvents();

        // 터치한 곳에 점찍기
        photoImg2.setOnPhotoTapListener(myPhotoTapListener);

        // 카드 길이 계산 버튼
        decideSizeBtn2.setOnClickListener(decideBtnOnClickListener);

        // 이미지 확대 및 드래그 이벤트
        photoImg2.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(final float scaleFactor, final float focusX, final float focusY) {
                //얼마나 확대 되었는지 스케일 저장
                scaleValue = photoImg2.getScale();
                // 드래그 이동으로 인한 rect 변환에 대한 이벤트
                photoImg2.setOnMatrixChangeListener(myMatrixChangedListener);
                processingPhotoViewSizeTxt2.setText("포토뷰 사이즈 : " + photoImg2.getWidth() + " / " + photoImg2.getHeight());
                FrameSizeTxt.setText("프레임 레이아웃 크기 : " + showFrameLayout2.getWidth() + " / " + showFrameLayout2.getHeight());
            }
        });

        // 점 지워주는 버튼
        eraseDotBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outDotList.size() == 0) {
                    Toast.makeText(mContext, "찍은 부분이 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    outDotList.get(outDotList.size() - 1).imageView.setVisibility(View.GONE);
                    outDotList.remove(outDotList.size() - 1);
                    showFrameLayout2.invalidate();
                    for (int i = 0; i < outDotList.size(); i++) {
                        Log.i(i + "번째 리스트 원소...", outDotList.get(i) + "");
                    }
                }
            }
        });


    }


    /// 리스너 모음//////////
    OnPhotoTapListener myPhotoTapListener = new OnPhotoTapListener() {
        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            if (outDotList.size() > 1) {
                Toast.makeText(mContext, "더이상 마킹을 할 수 없습니다.", Toast.LENGTH_SHORT).show();

            } else {
                // 터치한 화면에 점 이미지뷰를 생성해준다.
                final ImageView dotView = new ImageView(mContext);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dotView.setLayoutParams(layoutParams);
                dotView.setImageBitmap(outDotbit);
                dotView.setAlpha(95);

                // 점이미지뷰의 x,y 좌표를 정의
                // 줌이 되있던 안되있던 rect의 정보를 가져와서 x,y비율로 계산해준다. -> 화면 리사이클 방지 가능
                dotView.setX((mRectF.right - mRectF.left) * x + mRectF.left - cardDotbit.getWidth() / 2);
                dotView.setY((mRectF.bottom - mRectF.top) * y + mRectF.top - cardDotbit.getHeight() / 2);
                // 스케일도 비례식으로 정의해준다.
                dotView.setScaleX((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r);
                dotView.setScaleY((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r);

                Log.i("photoImg.getScale", photoImg2.getScale() + "");
                Log.i("dotView.getScale", dotView.getScaleX() + "");

                // dotView 자체와 위치(상대좌표)를 저장한다.
                final DotData dot = new DotData();
                dot.imageView = dotView;
                dot.x = x;
                dot.y = y;
                dot.scale = dotView.getScaleX();

                //dotView 드래그 메소드
                dragDot(dot);

                // DotClass를 리스트에 저장
                // -> 퍼센트(x,y)와 이미지뷰가 주
                // 혹시 몰라서 scale과 사용 여부를 결정하는 flag도 저장되어있긴함
                outDotList.add(dot);

                // 프레임 레이아웃에 점 추가
                showFrameLayout2.addView(dot.imageView);
//            Log.i("PhotoImg X,Y좌표....", photoImg.getX() + " / " + photoImg.getY());


            }
        }
    };

    OnMatrixChangedListener myMatrixChangedListener = new OnMatrixChangedListener() {
        @Override
        public void onMatrixChanged(final RectF rect) {
            // 멤버 변수 mRect에 변화된 rect 정보 저장
            mRectF = rect;
            // 점들의 이동 + 크기 증가 반복문
            // 점들의 이동의 경우, tap리스너의 x,y비율을 가져와서 확대된 rect에 맞춰서 다시 이동시켜준다.
            // 점들의 크기 증가, 감소는 getScale을 가져와서 비례식으로 재정의해준다.
            for (DotData d : cardDotList) {
                d.imageView.setX((rect.right - rect.left) * d.x + rect.left - d.imageView.getWidth() / 2);
                d.imageView.setScaleX((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r); // 비례식으로 나온 결과 -> 이미지는 최고 4배 확대 되고, 점들은 2배만 확대하게 함

                d.imageView.setY((rect.bottom - rect.top) * d.y + rect.top - d.imageView.getHeight() / 2);
                d.imageView.setScaleY((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r); // 비례식으로 나온 결과 -> 이미지는 최고 4배 확대 되고, 점들은 2배만 확대하게 함

                // 현재 scale과 전단계 scale을 비교하는 용도로 있었으나 안쓰는거 같다. -> 나중에 확인하고 지우자
                d.scale = scaleValue;
                Log.i("점 좌표.....", (d.imageView.getX() + d.imageView.getWidth() / 2) + " / " + (d.imageView.getY() + d.imageView.getHeight() / 2));
            }
            for (DotData d : outDotList) {
                d.imageView.setX((rect.right - rect.left) * d.x + rect.left - d.imageView.getWidth() / 2);
                d.imageView.setScaleX((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r); // 비례식으로 나온 결과 -> 이미지는 최고 4배 확대 되고, 점들은 2배만 확대하게 함

                d.imageView.setY((rect.bottom - rect.top) * d.y + rect.top - d.imageView.getHeight() / 2);
                d.imageView.setScaleY((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r); // 비례식으로 나온 결과 -> 이미지는 최고 4배 확대 되고, 점들은 2배만 확대하게 함

                // 현재 scale과 전단계 scale을 비교하는 용도로 있었으나 안쓰는거 같다. -> 나중에 확인하고 지우자
                d.scale = scaleValue;
                Log.i("점 좌표.....", (d.imageView.getX() + d.imageView.getWidth() / 2) + " / " + (d.imageView.getY() + d.imageView.getHeight() / 2));
            }


        }
    };

    // decideBtn 온클릭 리스너
    View.OnClickListener decideBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float outLine = 0;
            // 외부 두점 길이 구하는 과정
            if (outDotList.size() < 2) {
                Toast.makeText(mContext, "점을 모두 찍어주십시오.", Toast.LENGTH_SHORT).show();
            } else {
                Vector2D_F32 outVec = new Vector2D_F32();
                float outX0 = outDotList.get(0).imageView.getX();
                float outY0 = outDotList.get(0).imageView.getY();
                Log.i("outDotList ", outDotList.size() + "");
                float outX1 = outDotList.get(1).imageView.getX();
                float outY1 = outDotList.get(1).imageView.getY();

                outVec.set(outX1 - outX0, outY1 - outY0);

                outLine = (float) Math.sqrt(outVec.dot(outVec));


                // 카드 길이 구하는 과정(긴면이 비교 대상)
                float x0 = cardDotList.get(0).imageView.getX();
                float y0 = cardDotList.get(0).imageView.getY();

                float x1 = cardDotList.get(1).imageView.getX();
                float y1 = cardDotList.get(1).imageView.getY();

                float x2 = cardDotList.get(2).imageView.getX();
                float y2 = cardDotList.get(2).imageView.getY();

                float x3 = cardDotList.get(3).imageView.getX();
                float y3 = cardDotList.get(3).imageView.getY();


                Point2D_F32[] points = new Point2D_F32[4];
                points[0] = new Point2D_F32(x0, y0);
                points[1] = new Point2D_F32(x1, y1);
                points[2] = new Point2D_F32(x2, y2);
                points[3] = new Point2D_F32(x3, y3);
                Vector2D_F32 vec = new Vector2D_F32(points[1].x - points[0].x, points[1].y - points[0].y);

                float compareLine =
                        Math.round(((float) Math.sqrt(vec.dot(vec)) / photoImg2.getScale()) * 100f) / 100f;
//
//                SortLineAndPaint sortLineAndPaint = new SortLineAndPaint();
//                sortLineAndPaint.calAngle(points);
////                    Log.i("leng x/y", SortLineAndPaint.length_x + " / " + SortLineAndPaint.length_y);
//                Log.i("totalSquareFlag", sortLineAndPaint.totalSquareFlag + "");
//                if (sortLineAndPaint.totalSquareFlag == 0) {
//                    float cardLineHori = Math.round((sortLineAndPaint.length_x / photoImg2.getScale()) * 100f) / 100f; // 가로선
//                    float cardLineVer = Math.round((sortLineAndPaint.length_y / photoImg2.getScale()) * 100f) / 100f;  // 세로선
////                cardLengthTxt2.setText("긴면 : " + Math.round((sortLineAndPaint.length_x / photoImg2.getScale()) * 100f) / 100f
////                        + " / 짧은 면 : " + Math.round((sortLineAndPaint.length_y / photoImg2.getScale()) * 100f) / 100f);
//                    float compareLine = 0;
//                    if (cardLineHori >= cardLineVer) { // 긴면을 기준으로 삼는다.
//                        compareLine = cardLineHori;
//                    } else {
//                        compareLine = cardLineVer;
//                    }
//
//
//
//
//                }
                // 비례식을 이용한 길이 비교
                float realLine = ((outLine) / photoImg2.getScale()
                        * 8.56f) / compareLine;

                DecimalFormat form = new DecimalFormat("#.##");

                savedCardDotList.clear();

                if (CameraInit.flag.equals("2")) { // 옷종류 구별 (허리)

//                    CameraInit.sCameraWaistSize = Float.parseFloat(form.format(realLine));


                    if (caseFlag.contains("1")) { // 최초측정
                        // 전역 DotList에 마지막 카드점의 두 위치 저장
                        savedCardDotList.add(cardDotList.get(0));
                        savedCardDotList.add(cardDotList.get(1));
                        savedCardDotList.add(cardDotList.get(2));
                        savedCardDotList.add(cardDotList.get(3));
                        SignupInformation3Activity.sizeEdit.setText(Float.parseFloat(form.format(realLine)) + "");

                    } else if (caseFlag.contains("2")) { // 추가 입력
                        // 전역 DotList에 마지막 카드점의 두 위치 저장
                        // 추가 과정 도중 수정될 가능성도 고려
                        savedCardDotList.add(cardDotList.get(0));
                        savedCardDotList.add(cardDotList.get(1));
                        savedCardDotList.add(cardDotList.get(2));
                        savedCardDotList.add(cardDotList.get(3));
                        CameraMeasureActivity.detailSizeET.setText(Float.parseFloat(form.format(realLine)) + "");

                    } else { // 마이페이지 입력

                    }
                    finish();

                } else {
                    //옷종류 구별(어깨)

//                    CameraInit.sCameraShoulderSize = Float.parseFloat(form.format(realLine));
//                    SignupInformation3Activity.sizeEdit.setText(Float.parseFloat(form.format(realLine)) + "");

                    if (caseFlag.contains("1")) { // 최초측정
                        // 전역 DotList에 마지막 카드점의 두 위치 저장
                        savedCardDotList.add(cardDotList.get(0));
                        savedCardDotList.add(cardDotList.get(1));
                        savedCardDotList.add(cardDotList.get(2));
                        savedCardDotList.add(cardDotList.get(3));

                        SignupInformation3Activity.sizeEdit.setText(Float.parseFloat(form.format(realLine)) + "");

                    } else if (caseFlag.contains("2")) { // 추가 입력
                        // 전역 DotList에 마지막 카드점의 두 위치 저장
                        // 추가 과정 도중 수정될 가능성도 고려
                        savedCardDotList.add(cardDotList.get(0));
                        savedCardDotList.add(cardDotList.get(1));
                        savedCardDotList.add(cardDotList.get(2));
                        savedCardDotList.add(cardDotList.get(3));


                        CameraMeasureActivity.detailSizeET.setText(Float.parseFloat(form.format(realLine)) + "");

                    } else { // 마이페이지 입력

                    }
                    finish();
                }

                cardLengthTxt2.setText(Math.round(realLine * 100f) / 100f + "cm입니다.");
                // 카드 로고로 가리고 저장
                CreateLogo createLogo = new CreateLogo();
                createLogo.mContext = mContext;
                createLogo.setBaseBitmap(resultBitmap);
                createLogo.setCardDotList(cardDotList);
                createLogo.logoProcess();


            }


        }


    };

    ///// 그 외 함수 모음집//////

    // 이미지 프로세싱 결과로 DotView를 생성하는 과정
    public void createDotView() {
        if (caseFlag.contains("1")) {
            // 최초로 사이즈 측정
            for (int i = 0; i < 4; i++) {
                final ImageView dotView = new ImageView(mContext);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dotView.setLayoutParams(layoutParams);
                cardDotbit = BitmapFactory.decodeResource(getResources(), R.drawable.dotimage3);
                dotView.setAlpha(95);
                dotView.setImageBitmap(cardDotbit);

                dotView.setX((mRectF.right - mRectF.left) * ipPercent[i][0] + mRectF.left - cardDotbit.getWidth() / 2);
                dotView.setY((mRectF.bottom - mRectF.top) * ipPercent[i][1] + mRectF.top - cardDotbit.getHeight() / 2);
                dotView.setScaleX((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r);
                dotView.setScaleY((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r);
                if (i == 2 || i == 3) {
                    dotView.setVisibility(View.GONE);
                }
                final DotData dot = new DotData();
                dot.imageView = dotView;
                dot.x = ipPercent[i][0];
                dot.y = ipPercent[i][1];
                dot.scale = dotView.getScaleX();

                cardDotList.add(dot);
                dragDot(dot);
                showFrameLayout2.addView(dot.imageView);
                Log.i("cardDotList 원소 갯수 : ", cardDotList.size() + "");
            }
        } else if (caseFlag.contains("2")) {
            //추가입력으로 들어온 경우
            Log.d(">>>>>>>>>", showFrameLayout2.getChildCount() + "");
            Log.d("<<<<<<<<<<", savedCardDotList.size() + "");
//            showFrameLayout2.removeViews(1,showFrameLayout2.getChildCount());
            cardDotList.clear();
            for (int i = 0; i < 4; i++) {
                cardDotList.add(savedCardDotList.get(i));
                for (DotData d : cardDotList) {
                    d.imageView.setX((mRectF.right - mRectF.left) * d.x + mRectF.left - d.imageView.getWidth() / 2);
                    d.imageView.setScaleX((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r); // 비례식으로 나온 결과 -> 이미지는 최고 4배 확대 되고, 점들은 2배만 확대하게 함

                    d.imageView.setY((mRectF.bottom - mRectF.top) * d.y + mRectF.top - d.imageView.getHeight() / 2);
                    d.imageView.setScaleY((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r); // 비례식으로 나온 결과 -> 이미지는 최고 4배 확대 되고, 점들은 2배만 확대하게 함

                    // 현재 scale과 전단계 scale을 비교하는 용도로 있었으나 안쓰는거 같다. -> 나중에 확인하고 지우자
                    d.scale = scaleValue;
                    Log.i("점 좌표.....", (d.imageView.getX() + d.imageView.getWidth() / 2) + " / " + (d.imageView.getY() + d.imageView.getHeight() / 2));
                }
                ImageView iv = cardDotList.get(i).imageView;
                dragDot(cardDotList.get(i));
                if (iv.getParent() != null)
                    ((ViewGroup) iv.getParent()).removeView(iv);
                Log.i("//////////", i + " / " + iv.getParent());
                showFrameLayout2.addView(iv);
            }

//            for (int i = 0; i < 4; i++) {
//                final ImageView dotView = new ImageView(mContext);
//                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                dotView.setLayoutParams(layoutParams);
//                cardDotbit = BitmapFactory.decodeResource(getResources(), R.drawable.dotimage3);
//                dotView.setAlpha(95);
//                dotView.setImageBitmap(cardDotbit);
//
//                dotView.setX((mRectF.right - mRectF.left) * savedCardDotList.get(i).imageView.getX()+ mRectF.left - cardDotbit.getWidth() / 2);
//                dotView.setY((mRectF.bottom - mRectF.top) * savedCardDotList.get(i).imageView.getY()+ mRectF.top - cardDotbit.getHeight() / 2);
//                dotView.setScaleX((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r);
//                dotView.setScaleY((photoImg2.getScale() + (CameraActivity.r - 1)) / CameraActivity.r);
//                if (i == 2 || i == 3) {
//                    dotView.setVisibility(View.GONE);
//                }
//                final DotData dot = new DotData();
//                dot.imageView = dotView;
//                dot.x = ipPercent[i][0];
//                dot.y = ipPercent[i][1];
//                dot.scale = dotView.getScaleX();
//
//                cardDotList.add(dot);
//                dragDot(dot);
//                showFrameLayout2.addView(dot.imageView);
//                Log.i("cardDotList 원소 갯수 : ", cardDotList.size() + "");
        }


    }


    // 찍은점 드래그 해주는 함수
    public void dragDot(final DotData dot) {
        final ImageView dotView = dot.imageView;

        // 드래그 이벤트를 위한 변수 선언
        final float[] oldXvalue = new float[1];
        final float[] oldYvalue = new float[1];

        // 드래그 이벤트를 터치 이벤트로 준다.
        // 복붙한 코드. 될수있으면 건드리지말자
        dotView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    oldXvalue[0] = event.getX();
                    oldYvalue[0] = event.getY();
                    //터치를 했을때, 이동을 할때에만 가이드라인 그려주기
                    drawLine((ImageView) v);

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    v.setX(event.getRawX() - oldXvalue[0]);
                    v.setY(event.getRawY() - 2 * (oldYvalue[0] + v.getHeight())); // -10 : 터치한 손가락 위로 점이 이동해서 보기 편하게한다. (-3정도일때 손에 딱 달라붙음)

                    dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                    dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                    //  Log.i("Tag2", "Action Down " + me.getRawX() + "," + me.getRawY());

                    //터치를 했을때, 이동을 할때에만 가이드라인 그려주기
                    // 프레임 레이아웃에 추가하기전에 전단계 imageView를 지워줘야 라인이 하나만 나온다.
                    // 안그러면 자취대로 주욱 나오다가 OOM 터짐
                    showFrameLayout2.removeView(imageView);
                    drawLine((ImageView) v);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 손을 때는 액션 -> 점들의 뷰가 화면 밖으로 못도망가게 설정한 부분들
                    // 마찬가지로 손을 땔때 가이드라인이 사라져야하므로, removeView를 사용한다.
                    if (v.getX() > width && v.getY() > height) {
                        v.setX(width);
                        v.setY(height);
                        dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                        dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                        showFrameLayout2.removeView(imageView);

                    } else if (v.getX() < 0 && v.getY() > height) {
                        v.setX(0);
                        v.setY(height);
                        dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                        dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                        showFrameLayout2.removeView(imageView);

                    } else if (v.getX() > width && v.getY() < 0) {
                        v.setX(width);
                        v.setY(0);
                        dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                        dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                        showFrameLayout2.removeView(imageView);

                    } else if (v.getX() < 0 && v.getY() < 0) {
                        v.setX(0);
                        v.setY(0);
                        dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                        dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                        showFrameLayout2.removeView(imageView);

                    } else if (v.getX() < 0 || v.getX() > width) {
                        if (v.getX() < 0) {
                            v.setX(0);
                            v.setY(event.getRawY() - oldYvalue[0] - v.getHeight());
                            dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                            dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                            showFrameLayout2.removeView(imageView);

                        } else {
                            v.setX(width);
                            v.setY(event.getRawY() - oldYvalue[0] - v.getHeight());
                            dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                            dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                            showFrameLayout2.removeView(imageView);


                        }
                    } else if (v.getY() < 0 || v.getY() > height) {
                        if (v.getY() < 0) {
                            v.setX(event.getRawX() - oldXvalue[0]);
                            v.setY(0);
                            dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                            dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                            showFrameLayout2.removeView(imageView);

                        } else {
                            v.setX(event.getRawX() - oldXvalue[0]);
                            v.setY(height);
                            dot.x = (v.getX() - mRectF.left + cardDotbit.getWidth() / 2) / mRectF.width();
                            dot.y = (v.getY() - mRectF.top + cardDotbit.getHeight() / 2) / mRectF.height();
                            showFrameLayout2.removeView(imageView);

                        }
                    }
                    //혹시나 if문에 안걸릴경우 removeView실행
                    showFrameLayout2.removeView(imageView);


                }
                Log.i("width", dotView.getWidth() + "");
                Log.i("점 좌표.....", (dotView.getX() + dotView.getWidth() / 2) + " / " + (dotView.getY() + dotView.getHeight() / 2));
                return true;

            }
        });
    }

    //라인그려주는 함수
    public void drawLine(ImageView view) {
        // OnTouch리스너에 걸리는 형태이지만 혹시 몰라서 removeView를 한번 더해준다.
        showFrameLayout2.removeView(imageView);

        // 포토뷰 만큼 크기에서 라인들이 그려져야한다.
        Bitmap bitmap = Bitmap.createBitmap(photoImg2.getWidth(), photoImg2.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(photoImg2.getScale());

        // 세로선
        canvas.drawLine(view.getX() + view.getWidth() / 2, 0, view.getX() + view.getWidth() / 2, photoImg2.getHeight(), paint);
        // 가로선
        canvas.drawLine(0, view.getY() + view.getHeight() / 2, photoImg2.getWidth(), view.getY() + view.getHeight() / 2, paint);

        // imageView -> 가이드 라인 저장 뷰
        imageView.setImageBitmap(bitmap);

        showFrameLayout2.addView(imageView);

    }


    @Override
    public void bindViews() {
        super.bindViews();
        this.cardLengthTxt2 = (TextView) findViewById(R.id.cardLengthTxt2);
        this.rectSizeTxt2 = (TextView) findViewById(R.id.rectSizeTxt2);
        this.FrameSizeTxt = (TextView) findViewById(R.id.FrameSizeTxt);
        this.processingPhotoViewSizeTxt2 = (TextView) findViewById(R.id.processingPhotoViewSizeTxt2);
        this.processingImageSizeTxt2 = (TextView) findViewById(R.id.processingImageSizeTxt2);
        this.beforeImageSizeTxt2 = (TextView) findViewById(R.id.beforeImageSizeTxt2);
        this.decideSizeBtn2 = (Button) findViewById(R.id.decideSizeBtn2);
        this.eraseDotBtn2 = (Button) findViewById(R.id.eraseDotBtn2);
        this.showFrameLayout2 = (FrameLayout) findViewById(R.id.showFrameLayout2);
        this.photoImg2 = (PhotoView) findViewById(R.id.photoImg2);
    }
}
