package com.example.gdg_sqlitedatabase_demo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// SQLiteOpenHelper is a abstract class with onCreate and onUpgrade methods
// So instance MyHelper should override that two methods
class MyHelper(context: Context) : SQLiteOpenHelper(context,"ACDB",null,1){
    override fun onCreate(db : SQLiteDatabase?){
        db?.execSQL("CREATE TABLE ACTABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, MEANING TEXT)")
        db?.execSQL("INSERT INTO ACTABLE (NAME,MEANING) VALUES ('WWW','World Wide Web')")
        db?.execSQL("INSERT INTO ACTABLE (NAME, MEANING) VALUES ('AVD', 'Android Virtual Device')")
        db?.execSQL("INSERT INTO ACTABLE (NAME, MEANING) VALUES ('SE','Software Engineering')")
    }
    override fun onUpgrade(db: SQLiteDatabase,p1: Int, p2: Int){

    }
}