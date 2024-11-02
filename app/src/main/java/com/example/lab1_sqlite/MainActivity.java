package com.example.lab1_sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    EditText edt_maSV, edt_tenLop, edt_hoTen, edt_namSinh;
    Button btn_insert, btn_delete, btn_update, btn_query;
    ListView lv;
    ArrayList<String> myList;
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase mydatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edt_maSV = findViewById(R.id.edt_maSV);
        edt_tenLop = findViewById(R.id.edt_tenLop);
        edt_hoTen = findViewById(R.id.edt_hoTen);
        edt_namSinh = findViewById(R.id.edt_namSinh);
        btn_insert = findViewById(R.id.btn_insert);
        btn_delete = findViewById(R.id.btn_delete);
        btn_update = findViewById(R.id.btn_update);
        btn_query = findViewById(R.id.btn_query);

        // Tạo ListView
        lv = findViewById(R.id.lv);
        // Tạo mới mảng dữ liệu
        myList = new ArrayList<>();
        // Tạo mới Adapter
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        lv.setAdapter(myAdapter);
        // Hàm tạo csdl SQlite
        mydatabase = openOrCreateDatabase("QLSV.db", MODE_PRIVATE, null); // nếu tồn tại thì mở còn không thì tự tạo và mở
        // Tạo Table chứa dữ liệu (chỉ tạo duy nhất ở lần chạy đầu tiên)
        try {
            String sql = "CREATE TABLE tbsv(masv INTEGER primary key, tenlop TEXT, hoten TEXT, namsinh TEXT)";
            mydatabase.execSQL(sql);

        } catch (Exception e) {
            Log.e("Error", "Table đã tồn tại");
        }
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maSV = edt_maSV.getText().toString().trim();
                String tenLop = edt_tenLop.getText().toString().trim();
                String hoTen = edt_hoTen.getText().toString().trim();
                String namSinh = edt_namSinh.getText().toString().trim();

                if (maSV.isEmpty() || tenLop.isEmpty() || hoTen.isEmpty() || namSinh.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra nếu mã sinh viên đã tồn tại
                Cursor cursor = mydatabase.query("tbsv", null, "masv = ?", new String[]{maSV}, null, null, null);
                if (cursor.moveToFirst()) {
                    Toast.makeText(MainActivity.this, "Mã sinh viên đã tồn tại!", Toast.LENGTH_SHORT).show();
                    cursor.close(); // Đóng cursor
                    return;
                }
                cursor.close(); // Đóng cursor

                ContentValues myvalue = new ContentValues();
                myvalue.put("maSV", maSV);
                myvalue.put("tenLop", tenLop);
                myvalue.put("hoTen", hoTen);
                myvalue.put("namSinh", namSinh);

                String msg = "";
                if (mydatabase.insert("tbsv", null, myvalue) == - 1) {
                    msg = "Thêm bản ghi thất bại";
                } else {
                    msg = "Thêm bản ghi thành công";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maSV = edt_maSV.getText().toString();
                int n = mydatabase.delete("tbsv", "masv = ?", new String[]{maSV});
                String msg = "";
                if (n == 0) {
                    msg = "Xóa bản ghi thất bại";
                } else {
                    msg = "Có " + n +" bản ghi được xóa";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maSV = edt_maSV.getText().toString();
                String tenLop = edt_tenLop.getText().toString();
                String hoTen = edt_hoTen.getText().toString();
                String namSinh = edt_namSinh.getText().toString();
                ContentValues myvalue = new ContentValues();
                myvalue.put("tenLop", tenLop);
                myvalue.put("hoTen", hoTen);
                myvalue.put("namSinh", namSinh);
                int n = mydatabase.update("tbsv", myvalue, "masv = ?", new String[]{maSV});
                String msg = "";
                if (n == 0) {
                    msg = "Không có bản ghi nào được cập nhật";
                } else {
                    msg = "Có " + n +" bản ghi được cập nhật";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myList.clear();
                Cursor c = mydatabase.query("tbsv", null, null, null, null, null, null);
                c.moveToNext();
                String data = "";
                while(c.isAfterLast() == false) {
                    data = "Mã sinh viên: " + c.getString(0)+"\n Tên lớp: " + c.getString(1) + "\n Họ tên: " + c.getString(2) + "\n Năm sinh: " + c.getString(3);
                    c.moveToNext();
                    myList.add(data);
                }
                c.close();
                myAdapter.notifyDataSetChanged();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                // Lấy chuỗi đã chọn từ ListView
                String selectedItem = myList.get(i);

                // Tách dữ liệu từ chuỗi
                String[] parts = selectedItem.split("\n");
                if (parts.length >= 4) {
                    // Tách lấy mã sinh viên
                    String maSV = parts[0].split(": ")[1];
                    // Tách lấy tên lớp
                    String tenLop = parts[1].split(": ")[1];
                    // Tách lấy họ tên
                    String hoTen = parts[2].split(": ")[1];
                    // Tách lấy năm sinh
                    String namSinh = parts[3].split(": ")[1];

                    // Đưa dữ liệu lên các EditText
                    edt_maSV.setText(maSV);
                    edt_tenLop.setText(tenLop);
                    edt_hoTen.setText(hoTen);
                    edt_namSinh.setText(namSinh);
                }
            }
        });

    }
}