package com.example.studentmanager;

import android.content.Intent;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Student> studentList;
    MainActivity context;
    public String[] menuItems = {"Edit", "Delete"};

    public StudentAdapter(List<Student> studentList, MainActivity context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_view,parent,false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StudentViewHolder viewHolder = (StudentViewHolder) holder;
        Student student = studentList.get(position);

        viewHolder.textMSSV.setText(student.getMssv()+"");
        viewHolder.textName.setText(student.getName());
        viewHolder.textBirthday.setText(student.getBirthday());
        viewHolder.textEmail.setText(student.getEmail());
        viewHolder.textAddress.setText(student.getAddress());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.dialogDeleteSV(student);
            }
        });

        viewHolder.tag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

//                Toast.makeText(
//                        compoundButton.getContext(),
//                        compoundButton.getText()+"|"+b,
//                        Toast.LENGTH_SHORT).show();

                student.setTag(b);

                for(Student st:studentList){
                    System.out.println(st.getTag());
                }

                System.out.println(student.getTag());
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, StudentDetail.class);

                int msv = student.getMssv();

                intent.putExtra("mssv", msv);
                intent.putExtra("name", student.getName());
                intent.putExtra("birthday", student.getBirthday());
                intent.putExtra("email", student.getEmail());
                intent.putExtra("address", student.getAddress());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView textMSSV;
        TextView textName;
        TextView textBirthday;
        TextView textEmail;
        TextView textAddress;
        ImageButton btn_edit;
        ImageButton btn_delete;
        CheckBox tag;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            textMSSV = itemView.findViewById(R.id.text_mssv);
            textName = itemView.findViewById(R.id.text_name);
            textBirthday = itemView.findViewById(R.id.text_birthday);
            textEmail = itemView.findViewById(R.id.text_email);
            textAddress = itemView.findViewById(R.id.text_address);
//            btn_edit = itemView.findViewById(R.id.btn_edit);
//            btn_delete = itemView.findViewById(R.id.btn_delete);
            tag = itemView.findViewById(R.id.checkBoxSV);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(0, view.getId(), 0, menuItems[0]);
            menu.add(1, view.getId(), 1, menuItems[1]);
            menu.getItem(0).setOnMenuItemClickListener(groupMenuClickListener);
            menu.getItem(1).setOnMenuItemClickListener(groupMenuClickListener);
        }

        final MenuItem.OnMenuItemClickListener groupMenuClickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int pos = getLayoutPosition();

                if ((menuItem.getTitle().toString()).equals("Edit") == true){
                    context.dialogEditSV(studentList.get(pos));
                }
                if ((menuItem.getTitle().toString()).equals("Delete") == true){
                    context.dialogDeleteSV(studentList.get(pos));
                }

                return true;
            }
        };


    }

}
