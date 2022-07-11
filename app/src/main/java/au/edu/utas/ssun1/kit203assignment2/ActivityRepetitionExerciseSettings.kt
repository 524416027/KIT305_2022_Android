package au.edu.utas.ssun1.kit203assignment2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.NumberPicker
import au.edu.utas.ssun1.kit203assignment2.databinding.ActivityRepetitionExerciseSettingsBinding
import com.google.android.material.slider.RangeSlider

class ActivityRepetitionExerciseSettings : AppCompatActivity() {
    private lateinit var ui: ActivityRepetitionExerciseSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityRepetitionExerciseSettingsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //get sharedPref reference
        val sharedPref = this?.getSharedPreferences("au.edu.utas.ssun1.kit203assignment2", Context.MODE_PRIVATE)

        var timeLimitMinutes : Int = sharedPref.getInt("timeLimitMinutes", 0)   //time limit minutes
        var timeLimitSeconds : Int = sharedPref.getInt("timeLimitSeconds", 0)   //time limit seconds
        var repeatTime : Int = sharedPref.getInt("repeatTime", 0)               //repeat times
        var appearButtons : Int = sharedPref.getInt("appearButtons", 2)         //button appears

        var randomButtonOrder : Boolean = sharedPref.getBoolean("randomButtonOrder", false)         //if button spawn in random order
        var nextButtonIndication : Boolean = sharedPref.getBoolean("nextButtonIndication", false)   //if next button have indication

        var buttonSizeIndex = sharedPref.getInt("buttonSizeIndex", 1)     //0=small, 1=medium, 2=large

        var isFreeplay : Boolean = false

        //default value display for number picker
        ui.numPickerMinutes.minValue = 0
        ui.numPickerMinutes.maxValue = 59
        ui.numPickerMinutes.value = timeLimitMinutes
        ui.numPickerSeconds.minValue = 0
        ui.numPickerSeconds.maxValue = 59
        ui.numPickerSeconds.value = timeLimitSeconds

        //time limit in using indication
        if(timeLimitMinutes == 0 && timeLimitSeconds == 0)
        {
            ui.textViewUseTimeLimit.text = getString(R.string.timeLimitNo)
            ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.btn_color_red))
        }
        else
        {
            ui.textViewUseTimeLimit.text = getString(R.string.timeLimitYes)
            ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.btn_color_green))
        }

        //default value display for slider
        ui.textViewRepeatTimeNumber.text = repeatTime.toString()
        ui.sliderRepeatTime.setValues(repeatTime.toFloat())
        ui.textViewAppearButtonsNumber.text = appearButtons.toString()
        ui.sliderAppearButtons.setValues(appearButtons.toFloat())

        //repeat time in using indication
        if(repeatTime == 0)
        {
            ui.textViewUseRepeatTime.text = getString(R.string.timeLimitNo)
            ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.btn_color_red))
        }
        else
        {
            ui.textViewUseRepeatTime.text = getString(R.string.timeLimitYes)
            ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.btn_color_green))
        }

        //change to free play mode when no time limit and repeat count
        if(repeatTime == 0 && timeLimitMinutes == 0 && timeLimitSeconds == 0)
        {
            //now on free play mode
            isFreeplay = true

            //change button name
            ui.btnFreePlaySwitch.text = getString(R.string.repetitionMode)

            //disable settings not needed for free play mode
            ui.textViewTimeLimit.setTextColor(resources.getColor(R.color.grey));
            ui.textViewTimeLimitMinute.setTextColor(resources.getColor(R.color.grey));
            ui.textViewTimeLimitSecond.setTextColor(resources.getColor(R.color.grey));
            ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.grey));
            ui.numPickerMinutes.isEnabled = false;
            ui.numPickerSeconds.isEnabled = false;

            ui.textViewRepeatTime1.setTextColor(resources.getColor(R.color.grey));
            ui.textViewRepeatTime2.setTextColor(resources.getColor(R.color.grey));
            ui.textViewRepeatTimeNumber.setTextColor(resources.getColor(R.color.grey));
            ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.grey))
            ui.sliderRepeatTime.isEnabled = false;
        }

        //default value display for toggle button
        ui.btnRandomButtonOrderToggle.isChecked = randomButtonOrder
        ui.btnRandomButtonOrderToggle.setTextColor(if(randomButtonOrder) resources.getColor(R.color.btn_color_green) else resources.getColor(R.color.btn_color_red))
        ui.btnNextButtonIndicationToggle.isChecked = nextButtonIndication
        ui.btnNextButtonIndicationToggle.setTextColor(if(nextButtonIndication) resources.getColor(R.color.btn_color_green) else resources.getColor(R.color.btn_color_red))

        //default button size color
        when(buttonSizeIndex)
        {
            0  ->
            {
                ui.btnButtonSizeSmall.setTextColor(resources.getColor(R.color.btn_color_green))
                ui.btnButtonSizeSmall.setBackgroundColor(resources.getColor(R.color.btn_color_green))

                ui.btnButtonSizeMedium.setTextColor(resources.getColor(R.color.white))
                ui.btnButtonSizeMedium.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
                ui.btnButtonSizeLarge.setTextColor(resources.getColor(R.color.white))
                ui.btnButtonSizeLarge.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            }
            1 ->
            {
                ui.btnButtonSizeMedium.setTextColor(resources.getColor(R.color.btn_color_green))
                ui.btnButtonSizeMedium.setBackgroundColor(resources.getColor(R.color.btn_color_green))

                ui.btnButtonSizeSmall.setTextColor(resources.getColor(R.color.white))
                ui.btnButtonSizeSmall.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
                ui.btnButtonSizeLarge.setTextColor(resources.getColor(R.color.white))
                ui.btnButtonSizeLarge.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            }
            2 ->
            {
                ui.btnButtonSizeLarge.setTextColor(resources.getColor(R.color.btn_color_green))
                ui.btnButtonSizeLarge.setBackgroundColor(resources.getColor(R.color.btn_color_green))

                ui.btnButtonSizeSmall.setTextColor(resources.getColor(R.color.white))
                ui.btnButtonSizeSmall.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
                ui.btnButtonSizeMedium.setTextColor(resources.getColor(R.color.white))
                ui.btnButtonSizeMedium.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            }
        }

        ui.btnBackToMenu1.setOnClickListener()
        {
            val i = Intent(this, ActivityMainMenu::class.java)
            startActivity(i)
        }

        ui.btnFreePlaySwitch.setOnClickListener()
        {
            //if current on free play mode setting
            if(isFreeplay)
            {
                //now on repetition mode
                isFreeplay = false

                //change button name
                ui.btnFreePlaySwitch.text = getString(R.string.freePlayMode)

                //enable all settings for repetition mode settings
                ui.textViewTimeLimit.setTextColor(resources.getColor(R.color.black));
                ui.textViewTimeLimitMinute.setTextColor(resources.getColor(R.color.black));
                ui.textViewTimeLimitSecond.setTextColor(resources.getColor(R.color.black));

                //time limit in using indication
                if(timeLimitMinutes == 0 && timeLimitSeconds == 0)
                {
                    ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.btn_color_red))
                }
                else
                {
                    ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.btn_color_green))
                }

                //repeat time in using indication
                if(repeatTime == 0)
                {
                    ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.btn_color_red))
                }
                else
                {
                    ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.btn_color_green))
                }

                ui.numPickerMinutes.isEnabled = true;
                ui.numPickerSeconds.isEnabled = true;

                ui.textViewRepeatTime1.setTextColor(resources.getColor(R.color.black));
                ui.textViewRepeatTime2.setTextColor(resources.getColor(R.color.black));
                ui.textViewRepeatTimeNumber.setTextColor(resources.getColor(R.color.black));
                ui.sliderRepeatTime.isEnabled = true;
            }
            //otherwise current on free play mode setting
            else
            {
                //now on free play mode
                isFreeplay = true

                //change button name
                ui.btnFreePlaySwitch.text = getString(R.string.repetitionMode)

                //disable settings not needed for free play mode
                ui.textViewTimeLimit.setTextColor(resources.getColor(R.color.grey));
                ui.textViewTimeLimitMinute.setTextColor(resources.getColor(R.color.grey));
                ui.textViewTimeLimitSecond.setTextColor(resources.getColor(R.color.grey));
                ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.grey));
                ui.numPickerMinutes.isEnabled = false;
                ui.numPickerSeconds.isEnabled = false;

                ui.textViewRepeatTime1.setTextColor(resources.getColor(R.color.grey));
                ui.textViewRepeatTime2.setTextColor(resources.getColor(R.color.grey));
                ui.textViewRepeatTimeNumber.setTextColor(resources.getColor(R.color.grey));
                ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.grey))
                ui.sliderRepeatTime.isEnabled = false;
            }
        }

        ui.numPickerMinutes.setOnValueChangedListener{ numberPicker: NumberPicker, oldValue: Int, newValue: Int ->
            timeLimitMinutes = newValue

            //update shared preferences
            setSharedPref(sharedPref, "timeLimitMinutes", newValue)

            //time limit in using indication
            if(timeLimitMinutes == 0 && timeLimitSeconds == 0)
            {
                ui.textViewUseTimeLimit.text = getString(R.string.timeLimitNo)
                ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.btn_color_red))
            }
            else
            {
                ui.textViewUseTimeLimit.text = getString(R.string.timeLimitYes)
                ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.btn_color_green))
            }
        }

        ui.numPickerSeconds.setOnValueChangedListener{ numberPicker: NumberPicker, oldValue: Int, newValue: Int ->
            timeLimitSeconds = newValue

            //update shared preferences
            setSharedPref(sharedPref, "timeLimitSeconds", newValue)

            //time limit in using indication
            if(timeLimitMinutes == 0 && timeLimitSeconds == 0)
            {
                ui.textViewUseTimeLimit.text = getString(R.string.timeLimitNo)
                ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.btn_color_red))

                if(repeatTime == 0)
                {
                    //now on free play mode
                    isFreeplay = true

                    //change button name
                    ui.btnFreePlaySwitch.text = getString(R.string.repetitionMode)

                    //disable settings not needed for free play mode
                    ui.textViewTimeLimit.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewTimeLimitMinute.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewTimeLimitSecond.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.grey));
                    ui.numPickerMinutes.isEnabled = false;
                    ui.numPickerSeconds.isEnabled = false;

                    ui.textViewRepeatTime1.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewRepeatTime2.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewRepeatTimeNumber.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.grey))
                    ui.sliderRepeatTime.isEnabled = false;
                }
            }
            else
            {
                ui.textViewUseTimeLimit.text = getString(R.string.timeLimitYes)
                ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.btn_color_green))
            }
        }

        ui.sliderRepeatTime.addOnChangeListener{ rangeSlider: RangeSlider, value: Float, b: Boolean ->
            repeatTime = value.toInt()

            //update text display
            ui.textViewRepeatTimeNumber.text = repeatTime.toString()

            //update shared preferences
            setSharedPref(sharedPref, "repeatTime", value.toInt())

            //repeat time in using indication
            if(repeatTime == 0)
            {
                ui.textViewUseRepeatTime.text = getString(R.string.timeLimitNo)
                ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.btn_color_red))

                if(timeLimitMinutes == 0 && timeLimitSeconds == 0)
                {
                    //now on free play mode
                    isFreeplay = true

                    //change button name
                    ui.btnFreePlaySwitch.text = getString(R.string.repetitionMode)

                    //disable settings not needed for free play mode
                    ui.textViewTimeLimit.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewTimeLimitMinute.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewTimeLimitSecond.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewUseTimeLimit.setTextColor(resources.getColor(R.color.grey));
                    ui.numPickerMinutes.isEnabled = false;
                    ui.numPickerSeconds.isEnabled = false;

                    ui.textViewRepeatTime1.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewRepeatTime2.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewRepeatTimeNumber.setTextColor(resources.getColor(R.color.grey));
                    ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.grey))
                    ui.sliderRepeatTime.isEnabled = false;
                }
            }
            else
            {
                ui.textViewUseRepeatTime.text = getString(R.string.timeLimitYes)
                ui.textViewUseRepeatTime.setTextColor(resources.getColor(R.color.btn_color_green))
            }
        }

        ui.sliderAppearButtons.addOnChangeListener{ rangeSlider: RangeSlider, value: Float, b: Boolean ->
            appearButtons = value.toInt()

            //update text display
            ui.textViewAppearButtonsNumber.text = appearButtons.toString()

            //update shared preferences
            setSharedPref(sharedPref, "appearButtons", value.toInt())
        }

        ui.btnRandomButtonOrderToggle.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            if(isChecked)
            {
                randomButtonOrder = true

                ui.btnRandomButtonOrderToggle.setTextColor(resources.getColor(R.color.btn_color_green))
            }
            else
            {
                randomButtonOrder = false

                ui.btnRandomButtonOrderToggle.setTextColor(resources.getColor(R.color.btn_color_red))
            }

            //update shared preferences
            setSharedPref(sharedPref, "randomButtonOrder", isChecked)
        }

        ui.btnNextButtonIndicationToggle.setOnCheckedChangeListener{ compoundButton: CompoundButton, isChecked: Boolean ->
            if(isChecked)
            {
                nextButtonIndication = true
                ui.btnNextButtonIndicationToggle.setTextColor(resources.getColor(R.color.btn_color_green))
            }
            else
            {
                nextButtonIndication = false
                ui.btnNextButtonIndicationToggle.setTextColor(resources.getColor(R.color.btn_color_red))
            }

            //update shared preferences
            setSharedPref(sharedPref, "nextButtonIndication", isChecked)
        }

        ui.btnButtonSizeSmall.setOnClickListener()
        {
            buttonSizeIndex = 0

            ui.btnButtonSizeSmall.setTextColor(resources.getColor(R.color.btn_color_green))
            ui.btnButtonSizeSmall.setBackgroundColor(resources.getColor(R.color.btn_color_green))

            ui.btnButtonSizeMedium.setTextColor(resources.getColor(R.color.white))
            ui.btnButtonSizeMedium.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            ui.btnButtonSizeLarge.setTextColor(resources.getColor(R.color.white))
            ui.btnButtonSizeLarge.setBackgroundColor(resources.getColor(R.color.btn_color_blue))

            setSharedPref(sharedPref, "buttonSizeIndex", buttonSizeIndex)
        }

        ui.btnButtonSizeMedium.setOnClickListener()
        {
            buttonSizeIndex = 1

            ui.btnButtonSizeMedium.setTextColor(resources.getColor(R.color.btn_color_green))
            ui.btnButtonSizeMedium.setBackgroundColor(resources.getColor(R.color.btn_color_green))

            ui.btnButtonSizeSmall.setTextColor(resources.getColor(R.color.white))
            ui.btnButtonSizeSmall.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            ui.btnButtonSizeLarge.setTextColor(resources.getColor(R.color.white))
            ui.btnButtonSizeLarge.setBackgroundColor(resources.getColor(R.color.btn_color_blue))

            setSharedPref(sharedPref, "buttonSizeIndex", buttonSizeIndex)
        }

        ui.btnButtonSizeLarge.setOnClickListener()
        {
            buttonSizeIndex = 2

            ui.btnButtonSizeLarge.setTextColor(resources.getColor(R.color.btn_color_green))
            ui.btnButtonSizeLarge.setBackgroundColor(resources.getColor(R.color.btn_color_green))

            ui.btnButtonSizeSmall.setTextColor(resources.getColor(R.color.white))
            ui.btnButtonSizeSmall.setBackgroundColor(resources.getColor(R.color.btn_color_blue))
            ui.btnButtonSizeMedium.setTextColor(resources.getColor(R.color.white))
            ui.btnButtonSizeMedium.setBackgroundColor(resources.getColor(R.color.btn_color_blue))

            setSharedPref(sharedPref, "buttonSizeIndex", buttonSizeIndex)
        }

        ui.btnStartExercise.setOnClickListener()
        {
            val i = Intent(this, ActivityRepetitionExercise::class.java)
            i.putExtra("IS_FREE_PLAY", isFreeplay)
            startActivity(i)
        }
    }

    private fun setSharedPref(sharedPref: SharedPreferences ,key: String, value: Int)
    {
        with (sharedPref.edit())
        {
            putInt(key, value)
            apply()
        }
    }

    private fun setSharedPref(sharedPref: SharedPreferences ,key: String, value: String)
    {
        with (sharedPref.edit())
        {
            putString(key, value)
            apply()
        }
    }

    private fun setSharedPref(sharedPref: SharedPreferences ,key: String, value: Boolean)
    {
        with (sharedPref.edit())
        {
            putBoolean(key, value)
            apply()
        }
    }
}
