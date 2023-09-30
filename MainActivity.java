package com.example.test_01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    EditText et_username;
    EditText et_university;
    EditText et_major;
    TextView tv;
    String key = "123";
    String encData = "";
    String decData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = findViewById(R.id.et_username);
        et_university = findViewById(R.id.et_university);
        et_major = findViewById(R.id.et_major); //밖의 괄호에서 EditText로 선언했으므로 다른 {}에서도 사용 가능.
        tv=findViewById(R.id.tv);

        //쓰고 읽는 권한 설정
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MODE_PRIVATE);
    }

    public void clickSave(View view) {

        //SD Card(외장 메모리) 여부 확인
        String state = Environment.getExternalStorageState();

        //SD Card가 기기에 연결되어 있는지 확인
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD card is not mounted", Toast.LENGTH_SHORT).show();
            return;
        }

        //사용자가 입력한 EditText의 내용 저장
        String user_name = et_username.getText().toString(); //text를 읽어들여 string 형태로 저장
        String user_university = et_university.getText().toString();
        String user_major = et_major.getText().toString();
        //String data = user_name + user_university + user_major; //그냥 String 형태
        String data=XML.createXML(user_name, user_university, user_major); //XML 형태

        try{
            encData=AES.encByKey(key, data);
        } catch (Exception e){
            e.printStackTrace();
        }

        et_username.setText("");
        et_university.setText("");
        et_major.setText("");

        File path;//파일저장될 경로정보를 가진 객체 [Data.txt 파일이 저장될 경로]

        File[] dirs = getExternalFilesDirs("MyDir");//SD Card 내부에 "Mydir" 폴더 생성
        path = dirs[0];//생성된 폴더 경로
        tv.setText(path.getPath());//생성된 폴더 경로 출력

        File file = new File(path, "studentID.txt");//studentID.txt 파일로 저장

        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            PrintWriter writer = new PrintWriter(fos);

            writer.println(encData); //파일에 입력(전처리)
            writer.flush(); //파일에 저장
            writer.close(); //종료

            Toast.makeText(this, "SAVED", Toast.LENGTH_SHORT).show();

            tv.setText("저장 경로: \n" + path.getPath() + "\n사용자 정보가 저장되었습니다.");
            //파일 저장 경로 출력

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } //파일 저장
    }

    public void clickLoad(View view) {
        //SD Card가 있는지 확인
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED) ||
                state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {

            //파일을 읽기 위한 작업 수행
            File path = getExternalFilesDirs("MyDir")[0]; //SD Card에 "<MyDir" 폴더가 있는지 확인 목적
            File file = new File(path, "studentID.txt"); //"MyDir" 폴더에 "studentID.txt"파일 확인 목적
            try {    //기본적인 문법으로 그대로 사용
                FileReader fr = new FileReader(file);
                BufferedReader reader = new BufferedReader(fr);
                StringBuffer buffer = new StringBuffer();

                String line = reader.readLine();
                while (line != null) {
                    buffer.append(line + "\n");
                    line = reader.readLine();
                }
                reader.close();

                String temp_Buffer = buffer.toString();//파일로 부터 한 줄씩 읽은 내용이 저장된 buffer
                try{
                    decData=AES.decByKey(key, temp_Buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tv.setText(decData);

            }catch (FileNotFoundException e){e.printStackTrace();} catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}