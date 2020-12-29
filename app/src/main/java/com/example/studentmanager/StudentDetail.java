package com.example.studentmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class StudentDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_view_detail);

        Button edit_detail = findViewById(R.id.btn_edit_detail);
        Button cancel_detail = findViewById(R.id.btn_cancel_detail);
        TextView textMssv = findViewById(R.id.text_mssv_detail);
        TextView textName = findViewById(R.id.text_name_detail);
        TextView textBirthday = findViewById(R.id.text_birthday_detail);
        TextView textEmail = findViewById(R.id.text_email_detail);
        TextView textAddress = findViewById(R.id.text_address_detail);

        Intent intent = getIntent();

        int mssv = intent.getIntExtra("mssv", 0);
        String name = intent.getStringExtra("name");
        String birthday = intent.getStringExtra("birthday");
        String email = intent.getStringExtra("email");
        String address = intent.getStringExtra("address");

        System.out.println(mssv + "\n" + name +"\n" + birthday +"\n" + email + "\n" +address);
        textMssv.setText(String.valueOf(mssv));
        textName.setText(name);
        textBirthday.setText(birthday);
        textEmail.setText(email);
        textAddress.setText(address);

        edit_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mssv = Integer.parseInt(textMssv.getText().toString());
                String name = textName.getText().toString();
                String birthday = textBirthday.getText().toString();
                String email = textEmail.getText().toString();
                String address = textAddress.getText().toString();

                Student st = new Student(mssv, name, birthday, email, address);
//                MainActivity.dialogEditSV(st);
            }
        });

        cancel_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
