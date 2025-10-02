package com.assad.expenselog;

import static java.sql.DriverManager.getConnection;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private static final String DRIVER ="oracle.jdbc.driver.OracleDriver";
   
    private static final String URL  ="jdbc:oracle:thin:@//192.168.0.0:1521/DB";
    private static final String USERNAME ="master";
    private static final String PASSWORD ="master";
    private Connection connection;

    ImageView imageView;
    Button buttonDB;
    Button buttonCaptureImage;
    EditText editTextRemarks;
    EditText editTextAmount;
    //ListView listView;

    EditText dateFormat;

    String formattedDate;
    int year, month, day;

    Spinner spino ;
    ArrayList<ExpenseType> expenseTypeArrayList = new ArrayList<ExpenseType>();
    ArrayList<String> typearrayList = new ArrayList<>();
    ArrayAdapter<String> typeadapter;

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

        imageView = findViewById(R.id.imageView);
        //buttonDB = findViewById(R.id.buttonDB);
        buttonCaptureImage = findViewById(R.id.buttonCapture);
        editTextRemarks = findViewById(R.id.editTextRemarks);

        editTextAmount = findViewById(R.id.editTextAmount);

        dateFormat = findViewById(R.id.editTextDate);
        Calendar calendar = Calendar.getInstance();
        dateFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int iy, int im, int id) {
                        //dateFormat.setText(SimpleDateFormat.getDateInstance().format(calendar.getTime()));
                        dateFormat.setText(id + "/" + (im + 1) + "/" + iy);
                        calendar.set(iy, im, id);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        formattedDate = sdf.format(calendar.getTime());


                    }
                }, year, month, day);

                datePickerDialog.show();

            }
        });



        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy (threadPolicy);

        buttonCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);

            }
        });

        spinnerDataPopulator();


    }

    public void spinnerDataPopulator() {
        //Toast.makeText(this, "test a man", Toast.LENGTH_SHORT).show();

        try {


            //Class.forName("oracle.jdbc.driver.OracleDriver");
            Class.forName(DRIVER);
            this.connection = getConnection(URL, USERNAME, PASSWORD);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT EXP_TYPE_ID, EXP_TYPE_NAME  FROM EL_EXPENSE_TYPE ");
            while (resultSet.next()) {
                typearrayList.add(resultSet.getString(2));
                expenseTypeArrayList.add(new ExpenseType(resultSet.getInt(1), resultSet.getString(2)));
            }

            spino = findViewById(R.id.spinnerType);

            ArrayAdapter ad
                    = new ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item ,
                    typearrayList);

            // set simple layout resource file
            // for each item of spinner
            ad.setDropDownViewResource(
                    android.R.layout
                            .simple_spinner_dropdown_item);

            // Set the ArrayAdapter (ad) data on the
            // Spinner which binds data to spinner
            spino.setAdapter(ad);


            connection.close();
        } catch (Exception e) {
            textView.setText(e.toString());

        }
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    public void buttonSaveData(View view) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //Class.forName(DRIVER);
            this.connection = getConnection(URL, USERNAME, PASSWORD);
            Toast.makeText(this, "CONNECTED", Toast.LENGTH_LONG).show();
            PreparedStatement preparedStatement = null;
            Statement statement = connection.createStatement();

            //textView.setText( statement. );
            StringBuffer stringBuffer1 = new StringBuffer();

            editTextRemarks = findViewById(R.id.editTextRemarks);


            ImageView imageView = findViewById(R.id.imageView);

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            // Scale the bitmap to a higher resolution
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = width * 1; // Increase width
            int newHeight = height * 1; // Increase height
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Check Bitmap configuration
            if (scaledBitmap.getConfig() != Bitmap.Config.ARGB_8888) {
                scaledBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);
            }
            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();



            String sysdateStr="";
            Date sysdate = new Date();

            SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String forDate = sdff.format(sysdate.getTime());

            ResultSet resultSet = statement.executeQuery("SELECT SYSDATE, TO_CHAR(SYSDATE,'RRMMDDHHMISS') ||'.jpg' FILENAME FROM DUAL  ");
            while (resultSet.next()) {
                sysdate = resultSet.getTimestamp(1) ;
                sysdateStr = resultSet.getString(2) ;
            }

            int selectedItemPosition = spino.getSelectedItemPosition();
            int expTypeId = expenseTypeArrayList.get(selectedItemPosition).getExpTypeId();



            //Cursor cursor = (Cursor) spino.getItemAtPosition(selectedItemPosition);
            //int itemId = cursor.getInt(cursor.getColumnIndexOrThrow("empNo"));


            String insertString = "insert into EL_EXPENSE_VOUCHER " +
                    "(EXP_TYPE_ID , EXP_DATE, EXP_AMOUNT, REMARKS, " +
                    " EXP_IMAGE_FILENAME, EXP_IMAGE_MIME_TYPE, CREATION_DATE, EXP_IMAGE ) " +
                    "values (?,?,?,?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(insertString);
            preparedStatement.setInt(   1, expTypeId);
            //preparedStatement.setString(1, editText.getText().toString());
            //preparedStatement.setDate(  2,  java.sql.Date.valueOf( dateFormat.toString() ));
            preparedStatement.setDate(  2, formattedDate.isEmpty() ? null : java.sql.Date.valueOf(formattedDate));
            preparedStatement.setInt(   3, editTextAmount.getText().toString().isEmpty() ? 0 : Integer.parseInt(editTextAmount.getText().toString()));
            preparedStatement.setString(4, editTextRemarks.getText().toString());
            preparedStatement.setString(5, sysdateStr);
            preparedStatement.setString(6, "image/jpeg");
            //preparedStatement.setDate(  7,  new java.sql.Date(System.currentTimeMillis() ));
            //preparedStatement.setDate(  7, new java.sql.Date(sysdate.getTime() ));
            preparedStatement.setTimestamp(7, new Timestamp(sysdate.getTime()));
            preparedStatement.setBytes( 8, imageBytes);
            //preparedStatement.setTimestamp(9, new Timestamp(sysdate.getTime()));

            // Execute the query
            preparedStatement.executeUpdate();

            Toast.makeText(this, "Image saved to database successfully!", Toast.LENGTH_LONG).show();





            connection.close();
        } catch (Exception e) {
            textView.setText(e.toString());

        }
    }
}
