package ddwucom.mobile.sushi_tycoon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

//    필요한 변수 선언
    ProgressBar progressBar;
    TextView tvSelectedMenu;
    TextView tvCurrentMoneySum;

    //  총 매출액 변수
    int money_sum = 0;
    //  남은 횟수 출력할떄 사용할 변수
    int life_index = 0;

    // game over 여부
    boolean game_over = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        tvSelectedMenu = findViewById(R.id.tvSelectedMenu);
        tvCurrentMoneySum = (TextView) findViewById(R.id.tvCurrentMoneySum);

        //        메뉴 이미지뷰 배열
        ImageView[] menu_image_list = {findViewById(R.id.salmonsushi), findViewById(R.id.tunasushi), findViewById(R.id.ajisushi)};

        //        메뉴 이름 리스트
        String[] menu_string_list = {"연어", "참치", "키조개"};

        //        메뉴 가격 매칭
        HashMap<String, Integer> price = new HashMap<String,Integer>();

        //        손님 주문 내용을 띄워줄 textview 리스트
        TextView[] order_textview_list = {findViewById(R.id.tvOrder1), findViewById(R.id.tvOrder2), findViewById(R.id.tvOrder3)};

        //        손님 웃고있을때 이미지뷰 리스트
        ImageView[] smile_list = {findViewById(R.id.girl), findViewById(R.id.man), findViewById(R.id.woman)};

        //        손님 프레임레이아웃 리스트
        FrameLayout[] customer_list = {findViewById(R.id.first_customer), findViewById(R.id.second_customer), findViewById(R.id.third_customer)};

        //        손님 주문을 잘 주었는지 확인할 boolean list
        Boolean[] is_well_served = {false, false, false};

        //        손님이 떠났는지에 대한 boolean list -> 처음엔 다 머물러있음
        Boolean[] is_left = {false, false, false};

        //        말풍선, '주세요' Textview -> 손님이 떠나면 없애기위해서 만드는 변수
        ImageView[] word_bubble_list = {findViewById(R.id.word_bubble1), findViewById(R.id.word_bubble2), findViewById(R.id.word_bubble3)};
        TextView[] juseyo_list = {findViewById(R.id.tvjuseyo1), findViewById(R.id.tvjuseyo2), findViewById(R.id.tvjuseyo3)};

        //        life 목록
        ImageView[] life_list = {findViewById(R.id.life_3), findViewById(R.id.life_2), findViewById(R.id.life_1)};

//        life가 사라졌는지에 대한 boolean 리스트
        Boolean[] is_life_gone = {false, false, false};

//        잘못 준 횟수를 세기위한 변수 리스트 -> 처음엔 다 0
        int[] wrong_served_count_list = {0, 0, 0};
        Boolean[] flag = {true, true, true};

        //       초기 메뉴 랜덤 주문
        Random random = new Random();

        price.put("연어", 2000);
        price.put("참치", 5000);
        price.put("키조개", 3000);

        //      메뉴 주기
        int i;
        for (i=0; i<3; i++){
            int finalI = i;
            menu_image_list[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvSelectedMenu.setText(menu_string_list[finalI]);
                }
            });
        }

        for (i=0; i<3; i++){
            int finalI1 = i;
            customer_list[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(tvSelectedMenu.equals("")) && order_textview_list[finalI1].getText().toString().equals(tvSelectedMenu.getText().toString())){
                        is_well_served[finalI1] = true;
                        money_sum+=price.get(tvSelectedMenu.getText().toString());
                        tvCurrentMoneySum.setText(String.valueOf(money_sum));
                        flag[finalI1] = true;
                    }
                    else{
                        is_well_served[finalI1] = false;
                        wrong_served_count_list[finalI1]++;
                        flag[finalI1] = false;
                    }
                }
            });
        }

//        life handler
        Handler life_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (is_life_gone[0] && is_life_gone[1] && is_life_gone[2] && !(game_over)){
                    Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                    //총 매출액 데이터를 같이 보냄
                    intent.putExtra("total_score", money_sum);
                    startActivity(intent);
                    game_over = true;
                    MainActivity.this.finish();
                }
                if (msg.getData().getBoolean("first_customer_left") && !is_life_gone[0]){
                    life_list[life_index++].setVisibility(View.INVISIBLE);
                    is_life_gone[0] = true;
                }
                else if (msg.getData().getBoolean("second_customer_left") && !is_life_gone[1]){
                    life_list[life_index++].setVisibility(View.INVISIBLE);
                    is_life_gone[1] = true;
                }
                else if (msg.getData().getBoolean("third_customer_left") && !is_life_gone[2]){
                    life_list[life_index++].setVisibility(View.INVISIBLE);
                    is_life_gone[2] = true;
                }
            }
        };

//        프로그레스바 스레드 실행 ----
        Thread progressThread = new Thread(new Runnable(){
            @Override
            public void run() {
                for (int i=0; i<=30000; i+=1000) {
                    final int progress = i;

//               5분이 지나면 시간이 지났으므로 GameOver Activity로 이동함
                    if (i>=30000){
                        Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                        //총 매출액 데이터를 같이 보냄
                        intent.putExtra("total_score", money_sum);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }

                    try {
//                  ui 요소 접근 -> handler
//                  handler.post -> 곧바로 handler를 실행
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(progress);
                            }
                        });
                        Thread.sleep(1000); //1초 간격으로 thread를 실행 -> 즉, 1초 단위로 progressbar를 진행시킴
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        progressThread.start();
                //--------------------------
//    손님 각각의 Thread -> 7초이내에 메뉴가 안나오면 화나는 표정, 15초 이내에 안나오면 손님이 완전히 떠나게됨
//    메뉴를 잘 줄때마다 총 금액이 올라가게됨
        Thread firstCustomer = new Thread(new Runnable(){
            int wait_time = 0;
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int menu_index = random.nextInt(3);
                        order_textview_list[0].setText(menu_string_list[menu_index]);
                    }
                });
//               최대 머무는 시간은 15초
                while(true) {
                    try {
//                  ui 요소 접근 -> handler
//                  handler.post -> 곧바로 handler를 실행
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
//                                잘주었을 경우
                                if (is_well_served[0] && flag[0]){
//                                  새로운 메뉴를 주문하게됨
                                    int menu_index = random.nextInt(3);
                                    order_textview_list[0].setText(menu_string_list[menu_index]);
//                                  맞게 주었으면 계속 기다리니까 초기화
                                    wait_time = 0;
                                    is_well_served[0] = false;
//                                   화난 얼굴이 웃는얼굴로 바뀜 ㅎㅎ
                                    smile_list[0].setVisibility(View.VISIBLE);
                                }
                                else if (!is_well_served[0] && !(flag[0])){
                                    smile_list[0].setVisibility(View.INVISIBLE);
                                }
                                //    7초가 지나면
                                if (wait_time >= 7000) {
                                    smile_list[0].setVisibility(View.INVISIBLE);
                                }
                                //   15초 지나거나 잘못준횟수가 2번이면
                                if (wait_time >= 15000 || wrong_served_count_list[0]==2) {
                                    customer_list[0].setVisibility(View.INVISIBLE);
                                    is_left[0] = true; //더이상 머물러있지않음
                                    order_textview_list[0].setVisibility(View.GONE);
                                    word_bubble_list[0].setVisibility(View.INVISIBLE);
                                    juseyo_list[0].setVisibility(View.GONE);

                                    //wait_time = 0;

                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("first_customer_left", true);
                                    msg.setData(bundle);
                                    life_handler.sendMessage(msg);
                                }
                            }
                        });
                        Thread.sleep(1000); //1초 간격으로 thread를 실행 -> 즉, 1초 단위로 progressbar를 진행시킴
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    wait_time+=1000;
                }
            }
        });
        firstCustomer.start();

        Thread secondCustomer = new Thread(new Runnable() {
            int wait_time = 0;

            @Override
            public void run() {
                int menu_index = random.nextInt(3);
                order_textview_list[1].setText(menu_string_list[menu_index]);

//             최대 머무는 시간은 15초
                while(true) {
                    try {
                        //   ui 요소 접근 -> handler
                        //   handler.post -> 곧바로 handler를 실행
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
//                                잘주었을 경우
                                if (is_well_served[1] && flag[1]){
//                                  새로운 메뉴를 주문하게됨
                                    int menu_index = random.nextInt(3);
                                    order_textview_list[1].setText(menu_string_list[menu_index]);
//                                  맞게 주었으면 계속 남아있어하니까 초기화
                                    wait_time = 0;
                                    is_well_served[1] = false;
//                                   화난 얼굴이 웃는얼굴로 바뀜 ㅎㅎ
                                    smile_list[1].setVisibility(View.VISIBLE);
                                }
                                else if (!is_well_served[1] && !(flag[1])){
                                    smile_list[1].setVisibility(View.INVISIBLE);
                                }
                                //    7초가 지나면 화난얼굴
                                if (wait_time >= 7000) {
                                    smile_list[1].setVisibility(View.INVISIBLE);
                                }
                                //   15초 지나거나 잘못준 횟수가 2번이면 자리를 떠남
                                if (wait_time >= 15000 || wrong_served_count_list[1]==2) {
                                    customer_list[1].setVisibility(View.INVISIBLE);
                                    is_left[1] = true; //더이상 머물러있지않음
                                    order_textview_list[1].setVisibility(View.GONE);
                                    word_bubble_list[1].setVisibility(View.INVISIBLE);
                                    juseyo_list[1].setVisibility(View.GONE);

                                    //wait_time = 0;

                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("second_customer_left", true);
                                    msg.setData(bundle);
                                    life_handler.sendMessage(msg);
                                }
                            }
                        });
                        Thread.sleep(1000); //1초 간격으로 thread를 실행 -> 즉, 1초 단위로 progressbar를 진행시킴
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    wait_time += 1000;
                }
            }
        });
        secondCustomer.start();

        Thread thirdCustomer = new Thread(new Runnable(){
            int wait_time = 0;
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int menu_index = random.nextInt(3);
                        order_textview_list[2].setText(menu_string_list[menu_index]);
                    }
                });
//               최대 머무는 시간은 15초
                while(true) {
                    try {
//                  ui 요소 접근 -> handler
//                  handler.post -> 곧바로 handler를 실행
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
//                                잘주었을 경우
                                if (is_well_served[2] && flag[2]){
//                                  새로운 메뉴를 주문하게됨
                                    int menu_index = random.nextInt(3);
                                    order_textview_list[2].setText(menu_string_list[menu_index]);
//                                  맞게 주었으면 계속 기다리니까 초기화
                                    wait_time = 0;
                                    is_well_served[2] = false;
//                                   화난 얼굴이 웃는얼굴로 바뀜 ㅎㅎ
                                    smile_list[2].setVisibility(View.VISIBLE);
                                }
                                else if (!is_well_served[2] && !(flag[2])){
                                    smile_list[2].setVisibility(View.INVISIBLE);
                                }
                                //    7초가 지나면
                                if (wait_time >= 7000) {
                                    smile_list[2].setVisibility(View.INVISIBLE);
                                }
                                //   15초 지나거나 잘못준횟수가 2번이면
                                if (wait_time >= 15000 || wrong_served_count_list[2]==2) {
                                    customer_list[2].setVisibility(View.INVISIBLE);
                                    is_left[2] = true; //더이상 머물러있지않음
                                    order_textview_list[2].setVisibility(View.GONE);
                                    word_bubble_list[2].setVisibility(View.INVISIBLE);
                                    juseyo_list[2].setVisibility(View.GONE);

                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("third_customer_left", true);
                                    msg.setData(bundle);
                                    life_handler.sendMessage(msg);
                                }
                            }
                        });
                        Thread.sleep(1000); //1초 간격으로 thread를 실행 -> 즉, 1초 단위로 progressbar를 진행시킴
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    wait_time+=1000;
                }
            }
        });
        thirdCustomer.start();
    }

//  화면이 가려지면 그냥 게임 종료
    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.this.finish();
    }
}
