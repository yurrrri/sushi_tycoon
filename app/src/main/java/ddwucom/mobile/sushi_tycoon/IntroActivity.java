package ddwucom.mobile.sushi_tycoon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Button btnStart;
        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                IntroActivity.this.finish();
            }
        });
    }

    //  화면이 가려지면 그냥 게임 종료
    @Override
    protected void onPause() {
        super.onPause();
        IntroActivity.this.finish();
    }
}