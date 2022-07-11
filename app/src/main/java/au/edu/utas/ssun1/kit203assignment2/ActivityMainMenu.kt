package au.edu.utas.ssun1.kit203assignment2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import au.edu.utas.ssun1.kit203assignment2.databinding.ActivityMainMenuBinding
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ActivityMainMenu : AppCompatActivity() {
    private lateinit var ui: ActivityMainMenuBinding

    private val db = Firebase.firestore
    private val databaseCollection = db.collection("stroke")
    private lateinit var databaseResult : QuerySnapshot

    private var correctButtonCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //get sharedPref reference
        val sharedPref = this?.getSharedPreferences("au.edu.utas.ssun1.kit203assignment2", Context.MODE_PRIVATE)

        ui.textViewUserName.text = sharedPref.getString("userName", "anonymous user")

        ui.btnEditName.setOnClickListener()
        {
            val i = Intent(this, ActivityEditName::class.java)
            i.putExtra("IF_RETURN", true)
            startActivity(i)
        }

        ui.btnRepetitionExercise.setOnClickListener()
        {
            val i = Intent(this, ActivityRepetitionExerciseSettings::class.java)
            startActivity(i)
        }

        ui.btnEntertainmentExercise.setOnClickListener()
        {
            val i = Intent(this, ActivityDesignedExerciseSettings::class.java)
            startActivity(i)
        }

        ui.btnHistoryList.setOnClickListener()
        {
            val i = Intent(this, ActivityHistoryList::class.java)
            startActivity(i)
        }
    }
}