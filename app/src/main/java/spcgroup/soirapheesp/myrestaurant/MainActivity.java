package spcgroup.soirapheesp.myrestaurant;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;

public class MainActivity extends AppCompatActivity {

    private ManageTABLE objManageTABLE;
    private EditText usrEditText, passwordEditText;
    private String userString, passwordString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind Widget
        bindWidget();

        //ConnectedDatabase
        objManageTABLE = new ManageTABLE(this);

        //Tester Add Value
        //testerAddValue();

        //delete all split
        deleteAllSQLite();

        //Synchronize JSON to SQLite
        synJSONtoSQLite();

    }//Main Method

    private void bindWidget() {
        usrEditText = (EditText) findViewById(R.id.editText);
        passwordEditText = (EditText) findViewById(R.id.editText2);

    }

    public void clickLogin(View view) {

        userString = usrEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();

        //check space
        if (userString.equals("") || passwordString.equals("")) {
            //Have Space
            //display alert
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(MainActivity.this, "Have Space", "Please fill all every blank.");

        } else {
            //No Space
            CheckUser();

        }

    }// ClickLogin

    private void CheckUser() {
        try {

            String[] strMyResult = objManageTABLE.searchUser(userString);
            //Check Password
            if (passwordString.equals(strMyResult[2])) {
                //intent to listview
                Intent objIntent = new Intent(MainActivity.this, OrderLISTVIEW.class);
                objIntent.putExtra("officer", strMyResult[3]);
                startActivity(objIntent);

            } else {
                //Alert
                MyAlertDialog objMyAlertDialog = new MyAlertDialog();
                objMyAlertDialog.errorDialog(MainActivity.this, "Password false", "Please try again Password your false. ");

            }

        } catch (Exception e) {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(MainActivity.this, "User Fault", "No " + userString + " in my DB.");

        }
        }

    private void synJSONtoSQLite() {

        //Setup my Policy
        StrictMode.ThreadPolicy myThreadPolicy = new StrictMode.ThreadPolicy
                .Builder().permitAll().build();

        StrictMode.setThreadPolicy(myThreadPolicy);

        int intTable = 1;
        while (intTable<=2){

            InputStream objInputStream = null;
            String strJSON = null;
            String strURLuser = "http://swiftcodingthai.com/12dec/php_get_data_soiraphee.php";
            String strURLfood = "http://swiftcodingthai.com/12dec/php_get_data_food.php";
            HttpPost objHttpPost = null;

            //1. create InputStream
            try {
                HttpClient objHttpClient = new DefaultHttpClient();
                switch (intTable) {
                    case 1:
                        objHttpPost = new HttpPost(strURLuser);
                        break;
                    case 2:
                        objHttpPost = new HttpPost(strURLfood);
                        break;
                }

                HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
                HttpEntity objHttpEntity = objHttpResponse.getEntity();
                objInputStream = objHttpEntity.getContent();


            } catch (Exception e) {
                Log.d("Rest", "InputStream ==>" + e.toString());
            }
            //2. Create strJson

            try {
                BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
                StringBuilder objStringBuilder = new StringBuilder();
                String strLine = null;

                while ((strLine = objBufferedReader.readLine())!=null){
                    objStringBuilder.append(strLine);
                 }
                objInputStream.close();
                strJSON = objStringBuilder.toString();

            } catch (Exception e) {
                Log.d("Rest", "strJSON==>" + e.toString());

            }

            //3. update to SQLite
            try {
                JSONArray objJsonArray = new JSONArray(strJSON);
                for (int i =0; i<objJsonArray.length();i++) {
                    JSONObject object = objJsonArray.getJSONObject(i);
                    switch (intTable) {
                        case 1:
                            //For UserTable
                            String strUser = object.getString("User");
                            String strPassword = object.getString("Password");
                            String strName = object.getString("Name");
                            objManageTABLE.addValueToUser(strUser, strPassword, strName);
                            break;
                        case 2:
                            //For foodTable
                            String strFood = object.getString("Food");
                            String strSource = object.getString("Source");
                            String strPrice = object.getString("Price");
                            objManageTABLE.addValueToFood(strFood, strSource, strPrice);
                            break;

                    }//switch
                }

            } catch (Exception e) {
                Log.d("Rest", "Update==>" + e.toString());
            }

            intTable += 1;

        }//while
    } //synJSON

    private void deleteAllSQLite() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase("Restaurant.db", MODE_PRIVATE, null);
        objSqLiteDatabase.delete("userTABLE", null, null);
        objSqLiteDatabase.delete("foodTABLE", null, null);

    }

    private void testerAddValue() {
        objManageTABLE.addValueToUser("user", "pass", "สร้อยรพี");
        objManageTABLE.addValueToFood("food", "source", "price");
    }
}//Main Class
