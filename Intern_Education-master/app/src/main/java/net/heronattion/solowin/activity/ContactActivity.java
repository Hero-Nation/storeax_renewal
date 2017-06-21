package net.heronattion.solowin.activity;

import android.os.Bundle;
import android.widget.TextView;

import net.heronattion.solowin.R;

public class ContactActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        bindViews();
        setupEvents();

        setCustomActionBar();
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("여니씨커스텀바");
    }

    @Override
    public void bindViews() {

    }

    @Override
    public void setupEvents() {

    }
}
