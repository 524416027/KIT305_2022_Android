package au.edu.utas.ssun1.kit203assignment2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import au.edu.utas.ssun1.kit203assignment2.databinding.ActivityDesignedExerciseBinding
import com.google.android.material.slider.RangeSlider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import kotlin.random.Random

class ActivityDesignedExercise : AppCompatActivity() {
    private lateinit var ui: ActivityDesignedExerciseBinding
    private lateinit var sharedPref: SharedPreferences

    private val db = Firebase.firestore
    private val databaseCollection = db.collection("stroke")

    private val SLIDER_LENGTH: Array<Int> = arrayOf(512, 768, 1024)
    private var sliderLengthIndex: Int = 0 //0=short, 1=medium, 2=long

    private var exerciseRecord = RepetitionExercise()

    private var randomSliderRotation : Boolean = false

    private var completedCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityDesignedExerciseBinding.inflate(layoutInflater)
        setContentView(ui.root)
        //get sharedPref reference
        sharedPref =
            this?.getSharedPreferences("au.edu.utas.ssun1.kit203assignment2", Context.MODE_PRIVATE)

        randomSliderRotation = sharedPref.getBoolean("randomSliderRotation", false)
        sliderLengthIndex = sharedPref.getInt("sliderLength", 0)

        ui.sliderExercise.layoutParams = AbsoluteLayout.LayoutParams(
            SLIDER_LENGTH[sliderLengthIndex],
            SLIDER_LENGTH[sliderLengthIndex],
            SLIDER_LENGTH[sliderLengthIndex] / 2,
            SLIDER_LENGTH[sliderLengthIndex] / 2
        )


        ui.sliderExercise.visibility = View.INVISIBLE

        ui.btnStartExerciseReal2.setOnClickListener()
        {
            startExerciseRecord()

            ui.sliderExercise.visibility = View.VISIBLE

            if(randomSliderRotation)
            {
                randomRotation()
            }

            ui.btnStartExerciseReal2.visibility = View.INVISIBLE
        }

        ui.sliderExercise.addOnChangeListener { rangeSlider: RangeSlider, value : Float, n : Boolean ->
            if(value.toInt() == 1)
            {
                ui.sliderExercise.isEnabled = false

                completedCount += 1
                timer()
            }
        }

        ui.btnOptions.setOnClickListener()
        {
            DialogInGameMenu().show(supportFragmentManager, "Dialog_In_Game_Menu")
        }
    }

    private fun startExerciseRecord()
    {
        //record start exercise information to database
        var nowTime: String =
            LocalDateTime.now().hour.toString() + ":" + LocalDateTime.now().minute.toString() + ":" + LocalDateTime.now().second.toString()

        //detail
        val action = ActionDetail(
            description = "Exercise Start",
            actionTime = nowTime,
            actionType = "start",
            buttonCorrect = null
        )

        //outer list
        exerciseRecord = RepetitionExercise(
            id = null,

            mode = "Designed Exercise",
            repeatTimes = null,
            startTime = nowTime,
            endTime = null
        )

        exerciseRecord.action.add(action)

        databaseCollection
            .add(exerciseRecord)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Document created with id ${it.id}")
                exerciseRecord.id = it.id
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error writing document", it)
            }
    }

    private fun endExerciseRecord()
    {
        var hour = if(LocalDateTime.now().hour < 0) "0" + LocalDateTime.now().hour else LocalDateTime.now().hour
        var minute = if(LocalDateTime.now().minute < 0) "0" + LocalDateTime.now().minute else LocalDateTime.now().minute
        var second = if(LocalDateTime.now().second < 0) "0" + LocalDateTime.now().second else LocalDateTime.now().second
        //record end exercise information to database
        var nowTime: String = hour.toString() + ":" + minute.toString() + ":" + second.toString()

        //detail
        val action = ActionDetail(
            description = "Exercise End",
            actionTime = nowTime,
            actionType = "end",
            buttonCorrect = null
        )

        exerciseRecord.repeatTimes = completedCount
        exerciseRecord.endTime = nowTime

        exerciseRecord.action.add(action)

        databaseCollection.document(exerciseRecord.id!!)
            .set(exerciseRecord)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Successfully updated exercise ${exerciseRecord?.id}")
            }
    }

    private fun actionRecord()
    {
        var hour = if(LocalDateTime.now().hour < 0) "0" + LocalDateTime.now().hour else LocalDateTime.now().hour
        var minute = if(LocalDateTime.now().minute < 0) "0" + LocalDateTime.now().minute else LocalDateTime.now().minute
        var second = if(LocalDateTime.now().second < 0) "0" + LocalDateTime.now().second else LocalDateTime.now().second
        //record end exercise information to database
        var nowTime: String = hour.toString() + ":" + minute.toString() + ":" + second.toString()

        //detail
        val action = ActionDetail(
            description = "Slider Performed",
            actionTime = nowTime,
            actionType = "sliderPerform",
            buttonCorrect = null
        )

        exerciseRecord.action.add(action)

        databaseCollection.document(exerciseRecord.id!!)
            .set(exerciseRecord)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Successfully updated exercise ${exerciseRecord?.id}")
            }
    }

    private fun timer()
    {
        var timeCounter = object : CountDownTimer(1000, 100) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                actionRecord()
                if(randomSliderRotation) {
                    randomRotation()
                }
                ui.sliderExercise.isEnabled = true
                ui.sliderExercise.setValues(0f)
            }
        }.start()
    }

    private fun randomRotation()
    {
        ui.sliderExercise.rotation = (Random.nextInt(0, 360)).toFloat()
    }

    public fun ConfirmationBackToMainMenu()
    {
        DialogConfirmationBackToMainMenu().show(
            supportFragmentManager,
            "Dialog_Confirmation_Back_To_Main_Menu"
        )
    }

    public fun ConfirmationRestart()
    {
        DialogConfirmationRestartExercise().show(
            supportFragmentManager,
            "Dialog_Confirmation_Restart_Exercise"
        )
    }

    public fun BackToMenu()
    {
        endExerciseRecord()
        val i = Intent(this, ActivityMainMenu::class.java)
        startActivity(i)
    }

    public fun RestartExercise()
    {
        endExerciseRecord()
        ui.btnStartExerciseReal2.visibility = View.VISIBLE
        ui.sliderExercise.visibility = View.INVISIBLE
        completedCount = 0
    }
}