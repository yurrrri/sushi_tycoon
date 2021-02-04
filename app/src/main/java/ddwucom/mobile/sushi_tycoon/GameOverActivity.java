package ddwucom.mobile.sushi_tycoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    Button btnRetry;
    TextView tvMoneySum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        tvMoneySum = findViewById(R.id.tvMoneySum);
        btnRetry = findViewById(R.id.btnRetry);

        Intent intent = getIntent();
        int money_sum = intent.getIntExtra("total_score", 0);
        tvMoneySum.setText(String.valueOf(money_sum));

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                GameOverActivity.this.finish();
            }
        });
    }

    //  화면이 가려지면 그냥 게임 종료
    @Override
    protected void onPause() {
        super.onPause();
        GameOverActivity.this.finish();
    }
}
