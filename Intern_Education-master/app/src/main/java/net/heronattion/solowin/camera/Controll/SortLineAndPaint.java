package net.heronattion.solowin.camera.Controll;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;


import net.heronattion.solowin.camera.CameraActivity;
import net.heronattion.solowin.camera.Data.PointsData;

import java.util.ArrayList;
import java.util.Collections;

import georegression.struct.point.Point2D_F32;
import georegression.struct.point.Vector2D_F32;

import static net.heronattion.solowin.activity.BaseActivity.mContext;
import static net.heronattion.solowin.camera.CameraActivity.bitmap;
import static net.heronattion.solowin.camera.Show2Activity.ipPercent;

/**
 * Created by Hero on 2017-04-10.
 */

public class SortLineAndPaint {
    // 출력용 이미지뷰 생성
//    public PhotoView mImageResult;
    // ConvertGray 클래스에서 받아옴 -> Canvas에서 라인을 그리기 위해 사용
    public Bitmap resizeImage = ConvertGray.resizeImage;

    // linesX, Y에 대한 설정, 접근자들
    private ArrayList<PointsData> linesX = null;
    private ArrayList<PointsData> linesY = null;
    // 접근자는 사실상 안쓰임. 보험용으로 만들었다.
//    public ArrayList<PointsData> getLinesX() {
//        return linesX;
//    }
//
//    public ArrayList<PointsData> getLinesY() {
//        return linesY;
//    }
    // ShowActivity에서 길이 구할때 접근용
    static public double length_y;
    static public double length_x;
    // ShowActivity에서 사용될 플래그
    static public int totalSquareFlag = 0;

    static public Bitmap resultBitmap;

    public void setLinesX(ArrayList<PointsData> linesX) {
        this.linesX = linesX;
    }

    public void setLinesY(ArrayList<PointsData> linesY) {
        this.linesY = linesY;
    }

    public void sortAndDraw() {
        //라인검출이 안될경우, nullpointexception이 나온다.
        //if문으로 걸러도 되지만, 너무 광범위해서 그냥 try catch 사용
        try {
            Collections.sort(linesX);
            Collections.sort(linesY);

            for (PointsData p : linesX) {
                Log.i("세로선 x좌표..........", p.getA().getX() + "");
            }
            for (PointsData p : linesY) {
                Log.i("가로선 y좌표..........", p.getA().getY() + "");
            }
            // 사각형의 교점 4개 저장
            Point2D_F32 IP1 = new Point2D_F32();
            Point2D_F32 IP2 = new Point2D_F32();
            Point2D_F32 IP3 = new Point2D_F32();
            Point2D_F32 IP4 = new Point2D_F32();
            Point2D_F32[] IP = new Point2D_F32[4];

            // lines에서 0과 sizeData()-1은 각각 최외각의 선을 의미
            if (linesX.size() == 0 || linesY.size() == 0) {
                Toast.makeText(mContext, "인식에 실패하였습니다. 다시 촬영해 주십시오.", Toast.LENGTH_SHORT).show();
            } else {
                if (GetIntersectPoint(linesX.get(0).getA(), linesX.get(0).getB(), // 제일 좌측 선
                        linesY.get(0).getA(), linesY.get(0).getB(), IP1)) {       // 제일 상위 선
                    IP[0] = IP1;
                    System.out.println("X : " + IP[0].x + " / Y : " + IP[0].y);
                } else {
                    System.out.println("false");
                }
                if (GetIntersectPoint(linesX.get(0).getA(), linesX.get(0).getB(), // 제일 좌측선
                        linesY.get(linesY.size() - 1).getA(), linesY.get(linesY.size() - 1).getB(), IP2)) { // 제일 아래선
                    IP[1] = IP2;
                    System.out.println("X : " + IP[1].x + " / Y : " + IP[1].y);
                } else {
                    System.out.println("false");
                }
                if (GetIntersectPoint(linesX.get(linesX.size() - 1).getA(), linesX.get(linesX.size() - 1).getB(), // 제일 우측선
                        linesY.get(linesY.size() - 1).getA(), linesY.get(linesY.size() - 1).getB(), IP3)) { // 제일 아래선
                    IP[2] = IP3;
                    System.out.println("X : " + IP[2].x + " / Y : " + IP[2].y);
                } else {
                    System.out.println("false");
                }
                if (GetIntersectPoint(linesX.get(linesX.size() - 1).getA(), linesX.get(linesX.size() - 1).getB(), // 제일 우측선
                        linesY.get(0).getA(), linesY.get(0).getB(), IP4)) {   // 제일 상위선
                    IP[3] = IP4;
                    System.out.println("X : " + IP[3].x + " / Y : " + IP[3].y);
                } else {
                    System.out.println("false");
                }

                //좌표 4개의 각을 계산하고 사각형인지, 카드인지 인식을 한다.
                calAngle(IP);

                //사각형이 아니거나, 카드인식에 실패해도 line detect한 결과를 보여주기 위한 과정

                Bitmap temp = Bitmap.createBitmap(resizeImage.getWidth(), resizeImage.getHeight(), Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(temp);

                canvas.drawBitmap(resizeImage, 0, 0, null);

                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2.0F);

                canvas.drawLine(linesX.get(0).getA().x, linesX.get(0).getA().y, linesX.get(0).getB().x, linesX.get(0).getB().y, paint);
                canvas.drawLine(linesX.get(linesX.size() - 1).getA().x, linesX.get(linesX.size() - 1).getA().y, linesX.get(linesX.size() - 1).getB().x, linesX.get(linesX.size() - 1).getB().y, paint);

                canvas.drawLine(linesY.get(0).getA().x, linesY.get(0).getA().y, linesY.get(0).getB().x, linesY.get(0).getB().y, paint);
                canvas.drawLine(linesY.get(linesY.size() - 1).getA().x, linesY.get(linesY.size() - 1).getA().y, linesY.get(linesY.size() - 1).getB().x, linesY.get(linesY.size() - 1).getB().y, paint);

                canvas.drawCircle(IP[0].x, IP[0].y, 10f, paint);
                canvas.drawCircle(IP[1].x, IP[1].y, 10f, paint);
                canvas.drawCircle(IP[2].x, IP[2].y, 10f, paint);
                canvas.drawCircle(IP[3].x, IP[3].y, 10f, paint);

                resultBitmap = temp;

                // 최종 결과 그리기
//                mImageResult.setImageDrawable(new BitmapDrawable(mContext.getResources(), temp));

//                ShowActivity.processingImageSizeTxt.setText("변환된 비트맵 사이즈 : " + temp.getWidth() + " / " + temp.getHeight());
//                ShowActivity.processingPhotoViewSizeTxt.setText("포토뷰 사이즈 : " + mImageResult.getWidth() + " / " + mImageResult.getHeight());
//                ShowActivity.rectSizeTxt.setText("Rect 사이즈 : " + mImageResult.getDisplayRect().width() + " / " + mImageResult.getDisplayRect().height());

//                Log.i("bitmpap.getWidth...", ShowActivity.bitmap.getWidth() + "");
//                Log.i("bitmpap.getHeight...", ShowActivity.bitmap.getHeight() + "");

                float alpha1 = (bitmap.getWidth() / 2) - (bitmap.getWidth() / (2 * CameraActivity.r));
                Log.i("w/2 - w/2r", alpha1 + "");

//                float alpha2 = (CameraActivity.bitmap.getWidth()/2)*(1+(1/CameraActivity.r));

                float beta1 = (bitmap.getHeight() / 2) - (bitmap.getHeight() / (2 * CameraActivity.r));
                Log.i("h/2 - h/2r", beta1 + "");
//                float beta2 = (ShowActivity.bitmap.getHeight()/2)*(1+(1/ShowActivity.r));

                float[] actual_a = new float[]{alpha1 + IP[0].x / CameraActivity.r, beta1 + IP[0].y / CameraActivity.r};
                float[] actual_b = new float[]{alpha1 + IP[3].x / CameraActivity.r, beta1 + IP[3].y / CameraActivity.r};
                float[] actual_c = new float[]{alpha1 + IP[2].x / CameraActivity.r, beta1 + IP[2].y / CameraActivity.r};
                float[] actual_d = new float[]{alpha1 + IP[1].x / CameraActivity.r, beta1 + IP[1].y / CameraActivity.r};

                float[][] percent = new float[][]{{actual_a[0] / bitmap.getWidth(), actual_a[1] / bitmap.getHeight()},
                        {actual_b[0] / bitmap.getWidth(), actual_b[1] / bitmap.getHeight()},
                        {actual_c[0] / bitmap.getWidth(), actual_c[1] / bitmap.getHeight()},
                        {actual_d[0] / bitmap.getWidth(), actual_d[1] / bitmap.getHeight()}};
                for (float[] f : percent) {
                    for (float f2 : f) {
                        System.out.println("좌표 퍼센트 ...." + f2);
                    }
                }
                ipPercent = percent;

            }
        } catch (NullPointerException e) {

            Toast.makeText(mContext.getApplicationContext(), "인식에 실패하였습니다. 다시 촬영해 주십시오.", Toast.LENGTH_SHORT).show();

        }
    }

    // 교점의 좌표를 구하는 메소드
    public static boolean GetIntersectPoint(Point2D_F32 AP1, Point2D_F32 AP2, Point2D_F32 BP1, Point2D_F32 BP2, Point2D_F32 IP) {
        double t;
        double s;
        double under = (BP2.y - BP1.y) * (AP2.x - AP1.x) - (BP2.x - BP1.x) * (AP2.y - AP1.y);
        if (under == 0) return false;

        double _t = (BP2.x - BP1.x) * (AP1.y - BP1.y) - (BP2.y - BP1.y) * (AP1.x - BP1.x);
        double _s = (AP2.x - AP1.x) * (AP1.y - BP1.y) - (AP2.y - AP1.y) * (AP1.x - BP1.x);

        t = _t / under;
        s = _s / under;

        if (t < 0.0 || t > 1.0 || s < 0.0 || s > 1.0) return false;
        if (_t == 0 && _s == 0) return false;

        IP.x = (float) (AP1.x + t * (double) (AP2.x - AP1.x));
        IP.y = (float) (AP1.y + t * (double) (AP2.y - AP1.y));
        return true;

    }

    // 사각형의 네 꼭지점을 받아서 내각들을 구하는 메소드
    public static void calAngle(Point2D_F32[] p) {


        Vector2D_F32[] vec = new Vector2D_F32[4];
        for (int i = 0; i < 4; i++) {
            vec[i] = new Vector2D_F32();
        }
        vec[0].set(p[1].x - p[0].x, p[1].y - p[0].y);
        vec[1].set(p[2].x - p[1].x, p[2].y - p[1].y);
        vec[2].set(p[3].x - p[2].x, p[3].y - p[2].y);
        vec[3].set(p[0].x - p[3].x, p[0].y - p[3].y);

        Double[] angle = new Double[4];
        for (int i = 0; i < 3; i++) {
            angle[i] = Math.toDegrees(vec[i].acute(vec[i + 1]));
        }
        angle[3] = Math.toDegrees(vec[3].acute(vec[0]));

//        return angle;

        // 내각들으 전부 구하면, 각도의 크기를 판정해서 직사각형인지 구별한다.
        int i = 0;
        boolean square_flag = true;
        while (i < 4) {

            if (angle[i] > 85 && angle[i] < 95) {
                System.out.println(angle[i]);
                i++;
            } else {
//                Toast.makeText(mContext, "직사각형이 아닙니다.", Toast.LENGTH_SHORT).show();
                square_flag = false; // 뒤에 나올 길이 구하는 연산에 대한 플래그
                totalSquareFlag = 1;
                break;
            }
        }

        //가로와 세로 길이를 구하는 연산
        //길이들을 구한 뒤, 비율을 계산하여 카드인지 판별한다.

        length_y = Math.sqrt(vec[0].dot(vec[0])); // 세로
        length_x = Math.sqrt(vec[1].dot(vec[1])); // 가로
        double ratio = length_x / length_y;
        boolean ratio_flag = true;

        if (ratio > 1.5 && ratio < 1.7) {
            ratio_flag = true;
            ratio = length_x / length_y;

        } else {
            ratio = length_y / length_x;
            if (ratio > 1.5 && ratio < 1.7) {
                ratio_flag = true;

            } else {
                ratio_flag = false;

            }
        }
//        totalSquareFlag = 1; // 0이면 사각형 X 1이면 사각형 O

        // 사각형이 아니거나 카드인지에 대한 판별 분기점
        if (square_flag && ratio_flag) {
//            Toast.makeText(mContext, "신용카드 인식. 비율 : " + ratio, Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "신용카드 인식 \n 측정하려는 부분을 찍어주세요." ,Toast.LENGTH_SHORT).show();
            System.out.println("가로픽셀 : " + length_x + " / 세로픽셀 : " + length_y);
            totalSquareFlag = 0;
            System.out.println(totalSquareFlag);

        } else {
            Toast.makeText(mContext, "신용카드 인식 불확실. \n 카드의 모서리에 정확히 맞춰주세요", Toast.LENGTH_SHORT).show();
            totalSquareFlag = 1;
        }
    }

}
