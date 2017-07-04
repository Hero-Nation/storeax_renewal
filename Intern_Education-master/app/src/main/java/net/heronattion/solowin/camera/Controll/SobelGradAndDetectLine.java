package net.heronattion.solowin.camera.Controll;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;


import net.heronattion.solowin.activity.BaseActivity;
import net.heronattion.solowin.camera.Data.PointsData;

import java.util.ArrayList;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLine;
import boofcv.abst.filter.derivative.ImageGradient;
import boofcv.alg.feature.detect.line.LineImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.android.ConvertBitmap;
import boofcv.android.VisualizeImageData;
import boofcv.factory.feature.detect.line.ConfigHoughFoot;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.factory.filter.derivative.FactoryDerivative;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;


/**
 * Created by Hero on 2017-04-10.
 */

public class SobelGradAndDetectLine {
    // 이미지 사이즈 -> 사이즈 클래스에서 불러온다.
    int width = ConvertGray.imageViewSizeData.width;
    int height = ConvertGray.imageViewSizeData.height;

    //소벨 그라디언트 멤버 변수
    public GrayU8 scaleimg;
//    public PhotoView mImageResult;
    GrayU8 grayProcX = new GrayU8(width, height);
    GrayU8 grayProcY = new GrayU8(width, height);
    GrayS16 derivX = new GrayS16(width, height);
    GrayS16 derivY = new GrayS16(width, height);
    // gradient를 실행시켜주는 객체
    ImageGradient<GrayU8, GrayS16> gradient = FactoryDerivative.sobel(GrayU8.class, GrayS16.class);
    //gradient 결과물 저장 비트맵
    Bitmap outputGradient = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    // line Detect 멤버 변수
    // config : line detect 설정값
    // lines : 검출된 라인을 저장한다.
    ConfigHoughFoot config = null;
    ArrayList<PointsData> linesX = null;
    ArrayList<PointsData> linesY = null;

    // SortLineAndPaint 클래스 선언
    SortLineAndPaint mSort = new SortLineAndPaint();

    public void sobel() {
        // 이미지 픽셀의 평균값
        double mean = ImageStatistics.mean(scaleimg);
//        CameraStartActivity.textThread.setText(mean+"");
        // mean값에 따라 config를 변경해 주자.
        if(mean<100){
            mean += mean;
        }else if(mean>170 && mean<250){
            mean /= 3;
        }else{
            mean = 100;
        }
        Log.i(" 연산된 mean 값 ",mean+"");

//        config = new ConfigHoughFoot(3, 8, 10, CameraStartActivity.threshold, 20);
        config = new ConfigHoughFoot(3, 8, 5, (int)mean, 10);

        // X축으로 그라디언트 (세로선)
        // 그라디언트 과정
        gradient.process(scaleimg, derivX, derivY);
        VisualizeImageData.colorizeGradient(derivX, derivX, -1, outputGradient, null);
        ConvertBitmap.bitmapToGray(outputGradient, grayProcX, null);
        // 라인 디텍팅(세로선)
        DetectLine<GrayU8> detectorX = FactoryDetectLineAlgs.houghFoot(config, GrayU8.class, GrayS16.class); // 라인 감지
        List<LineParametric2D_F32> foundLinesX = detectorX.detect(grayProcX); // 찾은 라인을 리스트에 저장
        Log.i("찾은 세로선 갯수 : ", foundLinesX.size() + "");
        if (foundLinesX.size() == 0) {
            Toast.makeText(BaseActivity.mContext, "인식에 실패하였습니다. 다시 촬영해 주십시오.", Toast.LENGTH_SHORT).show();
//            mImageResult.setImageBitmap(ConvertBitmap.grayToBitmap(grayProcX, Bitmap.Config.ARGB_8888));
        } else {
            linesX = new ArrayList<PointsData>(); // x축 수직(세로선)
            for (LineParametric2D_F32 p : foundLinesX) {
                Log.i("degree,,,,,,,,,", Math.toDegrees(p.getAngle()) + "");
                if (Math.toDegrees(p.getAngle()) % 90.0 >= -3.0 && Math.toDegrees(p.getAngle()) % 90.0 <= 3.0) {
                    PointsData P = new PointsData();
                    LineSegment2D_F32 ls = LineImageOps.convert(p, width, height); // width와 height의 영역과 직선들의 교점들로 선분 만든다.
                    P.setA(ls.a);
                    P.setB(ls.b);
                    P.setFlag(1);
                    linesX.add(P); // 선분의 시점과 종점을 list에 저장
                }
            }
        }
        // Y축으로 그라디언트 (가로선)
        // 그라디언트 과정
        gradient.process(scaleimg, derivX, derivY);
        VisualizeImageData.colorizeGradient(derivY, derivY, -1, outputGradient, null);
        ConvertBitmap.bitmapToGray(outputGradient, grayProcY, null);
        // 라인 디텍팅(세로선)
        DetectLine<GrayU8> detectorY = FactoryDetectLineAlgs.houghFoot(config, GrayU8.class, GrayS16.class); // 라인 감지
        List<LineParametric2D_F32> foundLinesY = detectorY.detect(grayProcY); // 찾은 라인을 리스트에 저장
        Log.i("찾은 세로선 갯수 : ", foundLinesY.size() + "");
        if (foundLinesY.size() == 0) {
            Toast.makeText(BaseActivity.mContext, "인식에 실패하였습니다. 다시 촬영해 주십시오.", Toast.LENGTH_SHORT).show();
//            mImageResult.setImageBitmap(ConvertBitmap.grayToBitmap(grayProcY, Bitmap.Config.ARGB_8888));
        } else {
            linesY = new ArrayList<PointsData>(); // x축 수직(세로선)
            for (LineParametric2D_F32 p : foundLinesY) {
                Log.i("degree,,,,,,,,,", Math.toDegrees(p.getAngle()) + "");
                if (Math.toDegrees(p.getAngle()) % 90.0 >= -3 && Math.toDegrees(p.getAngle()) % 90.0 <= 3) {
                    PointsData P = new PointsData();
                    LineSegment2D_F32 ls = LineImageOps.convert(p, width, height); // width와 height의 영역과 직선들의 교점들로 선분 만든다.
                    P.setA(ls.a);
                    P.setB(ls.b);
                    P.setFlag(0);
                    linesY.add(P); // 선분의 시점과 종점을 list에 저장
                }
            }
        }
        sendingInfo(linesX, linesY);
    }


    // 소벨 & 라인디텍팅 결과물 다음 과정 클래스로 전달달
    // 다음 클래스에서 라인 sort와 그리기 및 교점 좌표를 구한다.
    public void sendingInfo(ArrayList<PointsData> linesX, ArrayList<PointsData> linesY) {
//        mSort.mImageResult = mImageResult;
        mSort.setLinesX(linesX);
        mSort.setLinesY(linesY);
        mSort.sortAndDraw();
    }

}
