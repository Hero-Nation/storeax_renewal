package net.heronattion.solowin.camera.Data;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Hero on 2017-04-06.
 */

public class CardData {

    public Bitmap[] cards = new Bitmap[8];
    private Resources resources;
    private AppCompatActivity activity;

    // Acrtivity와 resource 연결
    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public void setCards(int i) {
        // CardData 초기화 -> 메모리 누수 방지용
        cards = new Bitmap[8];
        // drawable의 카드 이미지 id 동적할당
        String CARD = "card_size" + i;
        Log.i("CARD", CARD);
        int resID = resources.getIdentifier(CARD, "drawable", activity.getPackageName());
        // 용량 줄이기 위해 BitmapFactory의 option클래스 사용
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        cards[i] = BitmapFactory.decodeResource(resources, resID, options);

    }

    public Bitmap getCards(int i) {
        return cards[i];
    }


}
