package au.edu.utas.ssun1.kit203assignment2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.ssun1.kit203assignment2.databinding.ActivityEditNameBinding

//const val USERNAME_KEY: String = "USERNAME"

class ActivityEditName : AppCompatActivity() {
    private lateinit var ui: ActivityEditNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityEditNameBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //get sharedPref reference
        val sharedPref = this?.getSharedPreferences("au.edu.utas.ssun1.kit203assignment2", Context.MODE_PRIVATE)

        //getExtra data, if this page is returned from main menu for edit name
        var ifReturn : Boolean = intent.getBooleanExtra("IF_RETURN", false)

        //check if user name is already existed
        if(sharedPref.getString("userName", "") != "")
        {
            //if this page is not returned from main menu for edit name
            if(!ifReturn)
            {
                //go to main menu page
                goToMainMenu()
            }
            //otherwise this page is returned from main menu for edit name
            else
            {
                //change hint to stored name
                ui.editTextUserName.hint = sharedPref.getString("userName", "")
            }
        }

        ui.btnConfirmedName.setOnClickListener()
        {
            val enteredText = ui.editTextUserName.text.toString()

            //only store when the user name is not empty
            if(enteredText != "")
            {
                //store user name to shared preferences
                with (sharedPref.edit())
                {
                    putString("userName", enteredText)
                    apply()
                }
            }

            goToMainMenu()
        }

        ui.btnNameLater.setOnClickListener()
        {
            goToMainMenu()
        }
    }

    private fun goToMainMenu()
    {
        val i = Intent(this, ActivityMainMenu::class.java)
        startActivity(i)
    }
}