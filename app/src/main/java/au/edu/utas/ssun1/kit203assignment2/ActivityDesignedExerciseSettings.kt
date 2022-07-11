package au.edu.utas.ssun1.kit203assignment2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import au.edu.utas.ssun1.kit203assignment2.databinding.ActivityDesignedExerciseSettingsBinding

class ActivityDesignedExerciseSettings : AppCompatActivity() {
    private lateinit var ui: ActivityDesignedExerciseSettingsBinding
    private lateinit var sharedPref : SharedPreferences

    private var sliderLengthIndex : Int = 0 //0=short, 1=medium, 2=long

    private var randomSliderRotation : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityDesignedExerciseSettingsBinding.inflate(layoutInflater)
        setContentView(ui.root)
        //get sharedPref reference
        val sharedPref = this?.getSharedPreferences("au.edu.utas.ssun1.kit203assignment2", Context.MODE_PRIVATE)

        sliderLengthIndex = sharedPref.getInt("sliderLength", 0)
        when(sliderLengthIndex)
        {
            0 ->
            {
                ui.btnSliderLengthShort.setBackgroundColor(resources.getColor(R.color.btn_color_green))

                ui.btnSliderLengthMedium.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
                ui.btnSliderLengthLong.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            }
            1 ->
            {
                ui.btnSliderLengthMedium.setBackgroundColor(resources.getColor(R.color.btn_color_green))

                ui.btnSliderLengthShort.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
                ui.btnSliderLengthLong.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            }
            2 ->
            {
                ui.btnSliderLengthLong.setBackgroundColor(resources.getColor(R.color.btn_color_green))

                ui.btnSliderLengthShort.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
                ui.btnSliderLengthMedium.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            }
        }

        randomSliderRotation = sharedPref.getBoolean("randomSliderRotation", false)
        ui.btnRandomSliderRotationToggle.isChecked = randomSliderRotation
        ui.btnRandomSliderRotationToggle.setTextColor(if(randomSliderRotation) resources.getColor(R.color.btn_color_green) else resources.getColor(R.color.btn_color_red))

        ui.btnBackToMenu5.setOnClickListener()
        {
            val i = Intent(this, ActivityMainMenu::class.java)
            startActivity(i)
        }

        ui.btnSliderLengthLong.setOnClickListener()
        {
            sliderLengthIndex = 2
            setSharedPref(sharedPref, "sliderLength", sliderLengthIndex)

            ui.btnSliderLengthLong.setBackgroundColor(resources.getColor(R.color.btn_color_green))

            ui.btnSliderLengthShort.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            ui.btnSliderLengthMedium.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
        }

        ui.btnSliderLengthMedium.setOnClickListener()
        {
            sliderLengthIndex = 1
            setSharedPref(sharedPref, "sliderLength", sliderLengthIndex)

            ui.btnSliderLengthMedium.setBackgroundColor(resources.getColor(R.color.btn_color_green))

            ui.btnSliderLengthShort.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            ui.btnSliderLengthLong.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
        }

        ui.btnSliderLengthShort.setOnClickListener()
        {
            sliderLengthIndex = 0
            setSharedPref(sharedPref, "sliderLength", sliderLengthIndex)

            ui.btnSliderLengthShort.setBackgroundColor(resources.getColor(R.color.btn_color_green))

            ui.btnSliderLengthMedium.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            ui.btnSliderLengthLong.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
        }

        ui.btnRandomSliderRotationToggle.setOnCheckedChangeListener{ comoundButton: CompoundButton, isChecked: Boolean ->
            if(isChecked)
            {
                randomSliderRotation = true
                ui.btnRandomSliderRotationToggle.setTextColor(resources.getColor(R.color.btn_color_green))
            }
            else
            {
                randomSliderRotation = false
                ui.btnRandomSliderRotationToggle.setTextColor(resources.getColor(R.color.btn_color_red))
            }

            //update shared preferences
            setSharedPref(sharedPref, "randomSliderRotation", isChecked)
        }

        ui.btnStartExercise2.setOnClickListener()
        {
            val i = Intent(this, ActivityDesignedExercise::class.java)
            startActivity(i)
        }
    }

    private fun setSharedPref(sharedPref: SharedPreferences, key: String, value: Int)
    {
        with (sharedPref.edit())
        {
            putInt(key, value)
            apply()
        }
    }

    private fun setSharedPref(sharedPref: SharedPreferences, key: String, value: String)
    {
        with (sharedPref.edit())
        {
            putString(key, value)
            apply()
        }
    }

    private fun setSharedPref(sharedPref: SharedPreferences, key: String, value: Boolean)
    {
        with (sharedPref.edit())
        {
            putBoolean(key, value)
            apply()
        }
    }
}
