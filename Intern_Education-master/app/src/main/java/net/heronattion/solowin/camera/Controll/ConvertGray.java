package net.heronattion.solowin.camera.Controll;

import android.graphics.Bitmap;
import android.util.Log;


import net.heronattion.solowin.camera.CameraActivity;
import net.heronattion.solowin.camera.Data.ImageViewSizeData;
import net.heronattion.solowin.camera.util.BitmapUtil;

import boofcv.alg.filter.misc.AverageDownSampleOps;
import boofcv.android.ConvertBitmap;
import boofcv.struct.image.GrayU8;

/**
 * Created by Hero on 2017-04-10.
 */

public class ConvertGray {

    public Bitmap mOrgImage; // 이미지 프로세싱 대상 비트맵
//    public PhotoView mImageResult; // 결과 출력용 ImageView -> 다음 단계로 넘겨준다.

    // 다른 클래스에서도 사이즈 및 크키조절한 비트맵 접근 가능 하기 위해 static 선언
    public static ImageViewSizeData imageViewSizeData; // SobelGradAndDetectLine에서  사용
    public static Bitmap resizeImage; // SortLineAndPaint에서 사용

    // 이미지를 crop 및 사이즈 조절
    public void cropAndResize() {

        int r = CameraActivity.r; // r배 만큼 이미지 확대

        int width = mOrgImage.getWidth()/r, height = mOrgImage.getHeight()/r;

        // 이미지 확대 원리
        // 이미지 중심에서 부터 사이즈 * 1/r 만큼 부분만 추출
        // 추출된 이미지를 다시 원래 사이즈 만큼 확대 시킨다. 즉 r배 만큼 확대
        // 단 crop이므로 줌 개념이 아닌 잘라내는 개념
        Bitmap cropImage = BitmapUtil.cropCenterBitmap(mOrgImage, width, height); // 이미지 추출

        // 이미지 확대
        // 분기점을 안달면 BoofCV Exception 발생
        width = r*width;
        height = r*height;
        if(width >= height)
            resizeImage = BitmapUtil.resizeBitmap(cropImage, width);
        else
            resizeImage = BitmapUtil.resizeBitmap(cropImage, height);

        // 사이즈 정보 저장, 다른 연산 클래스에서 접근 가능
        // 원본 이미지 비트맵을 r배만큼 줄인것(1/r)
        imageViewSizeData = new ImageViewSizeData();
        imageViewSizeData.width = width; imageViewSizeData.height = height;

        Log.i("width : ", imageViewSizeData.width+"");
        Log.i("height : ", imageViewSizeData.height+"");

        //BoofCV 연산은 기본적으로 Gray클래스들로 진행(메모리 사용 최적화)
        //사용할 Gray변수들 선언
        GrayU8 cropUnit = new GrayU8(resizeImage.getWidth(), resizeImage.getHeight());
        GrayU8 scaleImg = new GrayU8(width, height);

        // resize한 이미지를 Gray로 변환 (input, output, storage(null))
        ConvertBitmap.bitmapToGray(resizeImage, cropUnit, null);
        // cropUnit의 크기를 다시 scaleImage로 축소 -> 앞으로 연산은 scaleImg로
        AverageDownSampleOps.down(cropUnit, scaleImg);

        // 소벨 그라디언트 작업 시작 -> SobelGradAndDetectLine 클래스 사용
        SobelGradAndDetectLine sobelGradAndDetectLine = new SobelGradAndDetectLine();
        sobelGradAndDetectLine.scaleimg = scaleImg;
//        sobelGradAndDetectLine.mImageResult = mImageResult;
        sobelGradAndDetectLine.sobel();


    }


}
