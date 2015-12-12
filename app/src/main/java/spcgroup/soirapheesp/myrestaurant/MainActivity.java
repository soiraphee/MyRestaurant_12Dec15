package spcgroup.soirapheesp.myrestaurant;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ManageTABLE objManageTABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ConnectedDatabase
        objManageTABLE = new ManageTABLE(this);

        //Tester Add Value
        //testerAddValue();

        //delete all sqlite
        deleteAllSQLite();

    }//Main Method

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
