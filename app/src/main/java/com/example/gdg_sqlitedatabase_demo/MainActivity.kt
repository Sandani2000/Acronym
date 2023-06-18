package com.example.gdg_sqlitedatabase_demo

import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var edName: EditText
    private lateinit var edMeaning: EditText
    private lateinit var btnFirst: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var btnLast: Button
    private lateinit var btnInsert: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnClear: Button
    private lateinit var btnViewAll: Button
    private lateinit var searchView: SearchView
    private lateinit var listView: ListView

    lateinit var db: SQLiteDatabase
    lateinit var rs: Cursor
    lateinit var adapter: SimpleCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edName = findViewById(R.id.edName)
        edMeaning = findViewById(R.id.edMeaning)
        btnFirst = findViewById(R.id.btnFirst)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnLast = findViewById(R.id.btnLast)
        btnInsert = findViewById(R.id.btnInsert)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        btnClear = findViewById(R.id.btnClear)
        btnViewAll = findViewById(R.id.btnViewAll)
        searchView = findViewById(R.id.searchView)
        listView = findViewById(R.id.listView)

        var helper = MyHelper(applicationContext)
        db = helper.readableDatabase
        rs = db.rawQuery("SELECT * FROM ACTABLE ORDER BY NAME", null)

        //------------------------------------------------------------------------------------
        adapter = SimpleCursorAdapter(applicationContext, android.R.layout.simple_expandable_list_item_2,rs,arrayOf("NAME","MEANING"),
            intArrayOf(android.R.id.text1,android.R.id.text2),0
        )
        listView.adapter = adapter
        //------------------------------------------------------------------------------------

//        if(edName.text.toString().isEmpty() || edMeaning.text.toString().isEmpty()){
//            btnInsert.isEnabled = false
//        }

        //BtnInsert
        btnInsert.setOnClickListener{
            //insert record
            var cv = ContentValues()
            cv.put("NAME",edName.text.toString())
            cv.put("MEANING", edMeaning.text.toString())
            db.insert("ACTABLE",null,cv)
            rs.requery()
            adapter.notifyDataSetChanged()
            searchView.queryHint = "Search Among ${rs.count} Record"

            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Add Record")
            alertDialog.setMessage("Record Inserted Successfully...!")
            alertDialog.setPositiveButton("OK",DialogInterface.OnClickListener{dialogInterface, i ->
                edName.setText("")
                edMeaning.setText("")
                edName.requestFocus()
            })
            alertDialog.show()
        }

        //BtnUpdate
        btnUpdate.setOnClickListener{
            var cv = ContentValues()
            cv.put("NAME",edName.text.toString())
            cv.put("MEANING",edMeaning.text.toString())
            db.update("ACTABLE",cv,"_id = ?",arrayOf(rs.getString(0)))
            rs.requery()
            adapter.notifyDataSetChanged()

            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Update Record")
            alertDialog.setMessage("Record Updated Successfully...!")
            alertDialog.setPositiveButton("OK",DialogInterface.OnClickListener{ dialogInterface, i ->
                if(rs.moveToFirst()){
                    edName.setText(rs.getString(1))
                    edMeaning.setText(rs.getString(2))
                }
            })
            alertDialog.show()
        }

        //BtnDelete
        btnDelete.setOnClickListener{
            db.delete("ACTABLE","_id = ?",arrayOf(rs.getString(0)))
            rs.requery()

            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Delete Record")
            alertDialog.setMessage("Record Deleted Successfully...!")
            alertDialog.setPositiveButton("OK", DialogInterface.OnClickListener{dialogInterface, i ->
                if(rs.moveToFirst()){
                    edName.setText(rs.getString(1))
                    edMeaning.setText(rs.getString(2))
                }
                else{
                    edName.setText("No Data Found")
                    edMeaning.setText("No Data Found")
                }
            })
            alertDialog.show()
            adapter.notifyDataSetChanged()
            searchView.queryHint = "Search Among ${rs.count} Record"
        }

        //BtnViewAll
        btnViewAll.setOnClickListener{
            adapter.notifyDataSetChanged()
            // SearchBar count also should change when ResultSet change. So below 2 lines should execute when btnClick
            searchView.isIconified = false
            searchView.queryHint = "Search Among ${rs.count} Record"
            //below 2 lines should come after the isIconfied = false line. Otherwise searchBar query hint not shown by default
            searchView.visibility = View.VISIBLE
            listView.visibility = View.VISIBLE
        }

        //Search Function
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(pattern: String?): Boolean {
                rs = db.rawQuery("SELECT * FROM ACTABLE WHERE NAME LIKE '%${pattern}%' OR MEANING LIKE '%${pattern}%'",null)
                adapter.changeCursor(rs)
                return false
            }

        })

//        var adapter = SimpleCursorAdapter(applicationContext, android.R.layout.simple_expandable_list_item_2,rs,arrayOf("NAME","MEANING"),
//            intArrayOf(android.R.id.text1,android.R.id.text2),0
//        )
//        listView.adapter = adapter

        registerForContextMenu(listView) //can write this line anywhere inside onCreate function

        //BtnClear
        btnClear.setOnClickListener{
            edName.setText("")
            edMeaning.setText("")
            edName.requestFocus()
        }

        //BtnFirst
        btnFirst.setOnClickListener{
            if(rs.moveToFirst()){
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            }else{
                Toast.makeText(applicationContext,"No Data Found", Toast.LENGTH_LONG).show()
            }
        }

        btnNext.setOnClickListener {
            if(rs.moveToNext()){
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            }
            else if(rs.moveToFirst()){
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            }
            else
                Toast.makeText(applicationContext,"No Data Found", Toast.LENGTH_LONG).show()
        }

        btnPrevious.setOnClickListener {
            if(rs.moveToPrevious()){
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            }
            else if(rs.moveToLast()){
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            }
            else
                Toast.makeText(applicationContext,"No Data Found", Toast.LENGTH_LONG).show()
        }

        btnLast.setOnClickListener{
            if(rs.moveToLast()){
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            }else{
                Toast.makeText(applicationContext,"No Data Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(101,11,1,"DELETE")
        menu?.setHeaderTitle("Removing Data")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if(item.itemId == 11){
          db.delete("ACTABLE","_id = ?", arrayOf(rs.getString(0)))
          rs.requery()
          adapter.notifyDataSetChanged()
            searchView.queryHint = "Search Among ${rs.count} Record"
        }
        return super.onContextItemSelected(item)
    }
}