package com.example.studentmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.bloco.faker.Faker;

public class MainActivity extends AppCompatActivity {

    Database database;
    List<Student> studentList;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentList = new ArrayList<>();

        database = new Database(this,"student.sqlite",null,1);

        database.QueryData("CREATE TABLE IF NOT EXISTS Student(Mssv INTEGER PRIMARY KEY , HoTen VARCHAR(100) , NgaySinh VARCHAR(100), Email VARCHAR(100) , DiaChi VARCHAR(100) );");

//        try {
//            Faker faker = new Faker();
//            for (int i = 0; i < 10; i++) {
//                String mssv = "2017" + faker.number.number(4);
//                String hoten = faker.name.name();
//                String ngaysinh = faker.date.birthday(18, 22).toString();
//                String email = faker.internet.email();
//                String diachi = faker.address.city() + ", " + faker.address.country();
//
//                String sql = String.format("insert into Student " +
//                        "values('%s', '%s', '%s', '%s', '%s')", mssv, hoten, ngaysinh, email, diachi);
//
//                System.out.println(sql);
//                database.QueryData(sql);
//            }
//        } catch (Exception e){
//
//        }

        getDataFromSQL();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getDataFromSQLBySearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() < 1) {
                    getDataFromSQL();
                }
                else {
                    getDataFromSQLBySearch(newText);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            dialogAddSV();
        }

        if (item.getItemId() == R.id.action_del) {
            ArrayList<Student> studentArrayList_del = new ArrayList<Student>();

            for(Student st:studentList){
                if(st.getTag() == true){
                    studentArrayList_del.add(st);
                }
            }

            dialogDeleteAllSV(studentArrayList_del);
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogAddSV() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_sv);

        final EditText editMssv = dialog.findViewById(R.id.edit_mssv);
        final EditText editName = dialog.findViewById(R.id.edit_name);
        final EditText editBirthday = dialog.findViewById(R.id.edit_birthday);
        final EditText editEmail = dialog.findViewById(R.id.edit_email);
        final EditText editAddress = dialog.findViewById(R.id.edit_address);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_save = dialog.findViewById(R.id.btn_save);

        editBirthday.addTextChangedListener(new TextWatcher(){
            private String current = "";
            private String ddmmyyyy = "ddmmyyyy";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{

                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    editBirthday.setText(current);
                    editBirthday.setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mssv = editMssv.getText().toString().trim();
                String name = editName.getText().toString().trim();
                String birthday = editBirthday.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String address = editAddress.getText().toString().trim();
                if (mssv.equals("") || name.equals("") || birthday.equals("") || email.equals("") || address.equals("")) {
                    dialogMessage("Thông tin còn thiếu!");
                }
                else {
                    Cursor tmp = database.GetData("SELECT * FROM Student WHERE Mssv="+Integer.parseInt(mssv)+" ");
                    if( !tmp.moveToFirst()) {
                        database.QueryData("INSERT INTO Student VALUES ("+mssv+",'"+name+"','"+birthday+"','"+email+"','"+address+"')");
                        dialogMessage("Thêm sinh viên thành công");
                        dialog.cancel();
                        getDataFromSQL();
                    }
                    else {
                        dialogMessage("Mã sinh viên đã tồn tại");
                    }
                }
            }
        });

        dialog.show();
    }

    public void dialogEditSV(Student student) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_sv);
        final EditText editMssv = dialog.findViewById(R.id.edit_mssv);
        final EditText editName = dialog.findViewById(R.id.edit_name);
        final EditText editBirthday = dialog.findViewById(R.id.edit_birthday);
        final EditText editEmail = dialog.findViewById(R.id.edit_email);
        final EditText editAddress = dialog.findViewById(R.id.edit_address);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        Button btn_save = dialog.findViewById(R.id.btn_save);

        editMssv.setText(student.getMssv()+"");
        editMssv.setEnabled(false);
        editName.setText(student.getName());
        editBirthday.setText(student.getBirthday());
        editEmail.setText(student.getEmail());
        editAddress.setText(student.getAddress());

        editBirthday.addTextChangedListener(new TextWatcher(){
            private String current = "";
            private String ddmmyyyy = "ddmmyyyy";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{

                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    editBirthday.setText(current);
                    editBirthday.setSelection(sel < current.length() ? sel : current.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mssv = editMssv.getText().toString().trim();
                String name = editName.getText().toString().trim();
                String birthday = editBirthday.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String address = editAddress.getText().toString().trim();
                if (name.equals("") || birthday.equals("") || email.equals("") || address.equals("")) {
                    dialogMessage("Thông tin còn thiếu!");
                }
                else {
                    database.QueryData("UPDATE Student SET HoTen='"+name+"',NgaySinh='"+birthday+"',Email='"+email+"',DiaChi='"+address+"' WHERE Mssv="+Integer.parseInt(mssv)+" ");
                    dialogMessage("Sửa thông tin sinh viên thành công.");
                    dialog.cancel();
                    getDataFromSQL();
                }
            }
        });

        dialog.show();
    }

    public void dialogDeleteSV(final Student student) {
        final AlertDialog.Builder  alBuilder = new AlertDialog.Builder(this);
        alBuilder.setTitle("Thông báo!");
        alBuilder.setMessage("Bạn muốn xoá sinh viên có tên: "+student.getName()+" ?");
        alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int mssv = student.getMssv();
                database.QueryData("DELETE FROM Student WHERE Mssv="+mssv+" ");
                dialogMessage("Xoá sinh viên thành công");
                getDataFromSQL();
            }
        });
        alBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alBuilder.show();
    }

    public void dialogDeleteAllSV(final ArrayList<Student> students) {
        final AlertDialog.Builder  alBuilder = new AlertDialog.Builder(this);
        alBuilder.setTitle("Thông báo!");
        alBuilder.setMessage("Bạn muốn xoá sinh viên các sinh viên đã được đánh dấu ?");
        alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(Student student:students){
                    int mssv = student.getMssv();
                    database.QueryData("DELETE FROM Student WHERE Mssv="+mssv+" ");

                    getDataFromSQL();
                }

            }
        });

        alBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alBuilder.show();
    }

    private void dialogMessage(String message){
        final AlertDialog.Builder  alBuilder = new AlertDialog.Builder(this);
        alBuilder.setTitle("Thông báo !");
        alBuilder.setMessage(message);
        alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alBuilder.show();
    }

    private void getDataFromSQL() {
        studentList.clear();

        Cursor dataSV = database.GetData("SELECT * FROM Student");
        while (dataSV.moveToNext()) {
            int mssv = dataSV.getInt(0);
            String name = dataSV.getString(1);
            String birthday = dataSV.getString(2);
            String email = dataSV.getString(3);
            String address = dataSV.getString(4);

            studentList.add(new Student(mssv,name,birthday,email,address));
        }
        showData();
    }

    private void showData(){

        final RecyclerView recyclerView = findViewById(R.id.list_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new StudentAdapter(studentList,this);
        recyclerView.setAdapter(adapter);
    }

    private void getDataFromSQLBySearch(String keyword) {
        studentList.clear();

        int check = 0;
        for(int i = 0 ; i < keyword.length() ; i++) {
            if( !Character.isDigit(keyword.charAt(i)) ) {
                check = 1;
            }
        }

        Cursor dataSV;
        if (check == 0) {
            dataSV = database.GetData("SELECT * FROM Student WHERE Mssv="+keyword+" OR HoTen like '%"+keyword+"%'");
        }
        else {
            dataSV = database.GetData("SELECT * FROM Student WHERE HoTen like '%"+keyword+"%'");
        }

        while (dataSV.moveToNext()) {
            int mssv = dataSV.getInt(0);
            String name = dataSV.getString(1);
            String birthday = dataSV.getString(2);
            String email = dataSV.getString(3);
            String address = dataSV.getString(4);

            studentList.add(new Student(mssv,name,birthday,email,address));
        }

        showData();
    }

}