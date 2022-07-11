package au.edu.utas.ssun1.kit203assignment2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import au.edu.utas.ssun1.kit203assignment2.databinding.ActivityRepetitionExerciseBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

const val FIREBASE_TAG = "FirebaseLogging"
const val REQUEST_IMAGE_CAPTURE = 1
const val REQUEST_IMAGE_SELECT = 100

class ActivityRepetitionExercise : AppCompatActivity() {
    private lateinit var ui: ActivityRepetitionExerciseBinding
    private val db = Firebase.firestore
    private val databaseCollection = db.collection("stroke")
    private var exerciseRecord = RepetitionExercise()

    private val BUTTON_SIZE: Array<Int> = arrayOf(128, 256, 384)
    private lateinit var sharedPref: SharedPreferences
    private lateinit var buttons: Array<MaterialButton>

    private var buttonSizeIndex: Int = 1
    private var repeatTime: Int = 1
    private var appearButtons: Int = 2
    private var nextButtonIndication: Boolean = false
    private var randomButtonOrder: Boolean = false
    private var timeLimitMinutes: Int = 0
    private var timeLimitSeconds: Int = 0

    private var timeCountUp: Int = 0

    private var isFreeplay: Boolean = false
    private var isStarted: Boolean = false
    private var isEnd: Boolean = false

    private var currentButtonAction: Int = 0
    private var repetitionCount: Int = 0

    private var timeCounterSecondsTotal: Long = 0
    private var timeCounterSecondsCurrent: Long = 0
    private var timeCounterUpSecondsCurrent: Long = 0
    private var timeCounterOnPause: Boolean = false
    private var timeCounterUpOnPause: Boolean = false

    private lateinit var ImageUri: Uri
    private var dataID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityRepetitionExerciseBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //get sharedPref reference
        sharedPref =
            this?.getSharedPreferences("au.edu.utas.ssun1.kit203assignment2", Context.MODE_PRIVATE)

        buttons = arrayOf(
            ui.btnRoundButton1,
            ui.btnRoundButton2,
            ui.btnRoundButton3,
            ui.btnRoundButton4,
            ui.btnRoundButton5
        )

        buttonSizeIndex = sharedPref.getInt("buttonSizeIndex", 1)
        repeatTime = sharedPref.getInt("repeatTime", 0)
        appearButtons = sharedPref.getInt("appearButtons", 2)
        nextButtonIndication = sharedPref.getBoolean("nextButtonIndication", false)
        randomButtonOrder = sharedPref.getBoolean("randomButtonOrder", false)
        timeLimitMinutes = sharedPref.getInt("timeLimitMinutes", 0)
        timeLimitSeconds = sharedPref.getInt("timeLimitSeconds", 0)

        timeCounterSecondsTotal = (timeLimitMinutes * 60 + timeLimitSeconds).toLong()
        timeCounterSecondsCurrent = timeCounterSecondsCurrent
        updateTimeLimitText(timeCounterSecondsTotal.toInt())

        isFreeplay = intent.getBooleanExtra("IS_FREE_PLAY", false)

        //if current is on free play
        if (isFreeplay) {
            ui.textViewTimeLeft.visibility = View.INVISIBLE
            ui.textViewTimeLeftText.visibility = View.INVISIBLE
        } else {
            //if current don't has time limit
            if (timeCounterSecondsTotal.toInt() == 0) {
                ui.textViewTimeLeft.visibility = View.INVISIBLE
                ui.textViewTimeLeftText.visibility = View.INVISIBLE
            }
        }

        for (i in 0..4) {
            buttons[i].layoutParams = AbsoluteLayout.LayoutParams(
                BUTTON_SIZE[buttonSizeIndex],
                BUTTON_SIZE[buttonSizeIndex],
                BUTTON_SIZE[buttonSizeIndex],
                BUTTON_SIZE[buttonSizeIndex]
            )

            buttons[i].cornerRadius = BUTTON_SIZE[buttonSizeIndex] / 2

            //buttons start with invisible
            buttons[i].visibility = View.INVISIBLE
        }

        ui.textViewRepeatDone.text = repetitionCount.toString()

        ui.btnStartExerciseReal.setOnClickListener()
        {
            ui.btnStartExerciseReal.visibility = View.INVISIBLE

            isStarted = true

            isEnd = false

            //reset repeat count
            repetitionCount = 0

            //reset count up time
            timeCountUp = 0

            //new repetition exercise object
            exerciseRecord = RepetitionExercise()

            //random button position for the first start
            randomButton(true)

            //start timer
            if (timeCounterSecondsTotal.toInt() != 0) {
                startCountDownTimer()
            }
            startCountUpTimer()

            //database record
            startExerciseRecord()
        }

        ui.btnRoundButton1.setOnClickListener()
        {
            val thisButtonIndex: Int = 0

            buttonAction(thisButtonIndex)
        }

        ui.btnRoundButton2.setOnClickListener()
        {
            val thisButtonIndex: Int = 1

            buttonAction(thisButtonIndex)
        }

        ui.btnRoundButton3.setOnClickListener()
        {
            val thisButtonIndex: Int = 2

            buttonAction(thisButtonIndex)
        }

        ui.btnRoundButton4.setOnClickListener()
        {
            val thisButtonIndex: Int = 3

            buttonAction(thisButtonIndex)
        }

        ui.btnRoundButton5.setOnClickListener()
        {
            val thisButtonIndex: Int = 4

            buttonAction(thisButtonIndex)
        }

        ui.btnOptions.setOnClickListener()
        {
            if (isStarted) {
                pauseCountDownTimer()
            }
            DialogInGameMenu().show(supportFragmentManager, "Dialog_In_Game_Menu")
        }
    }

    public fun ConfirmationBackToMainMenu() {
        DialogConfirmationBackToMainMenu().show(
            supportFragmentManager,
            "Dialog_Confirmation_Back_To_Main_Menu"
        )
    }

    public fun ConfirmationRestart() {
        DialogConfirmationRestartExercise().show(
            supportFragmentManager,
            "Dialog_Confirmation_Restart_Exercise"
        )
    }

    public fun BackToMenu() {
        isStarted = false

        //database record
        endExerciseRecord()

        val i = Intent(this, ActivityMainMenu::class.java)
        startActivity(i)
    }

    public fun RestartExercise() {
        isStarted = false

        //database record
        endExerciseRecord()

        //show start button
        ui.btnStartExerciseReal.visibility = View.VISIBLE
        //reset timer
        timeCounterSecondsCurrent = timeCounterSecondsTotal
        //update timer text
        updateTimeLimitText(timeCounterSecondsTotal.toInt())
        //reset next button press index
        currentButtonAction = 0
        //disable all buttons
        for (i in 0..4) {
            //buttons start with invisible
            buttons[i].visibility = View.INVISIBLE
        }
    }

    private fun updateTimeLimitText(currentSeconds: Int) {
        var minutes: String =
            if (currentSeconds / 60 < 10) "0" + (currentSeconds / 60).toString() else (currentSeconds / 60).toString()
        var seconds: String =
            if (currentSeconds % 60 < 10) "0" + (currentSeconds % 60).toString() else (currentSeconds % 60).toString()
        var displayText: String = minutes + ":" + seconds
        ui.textViewTimeLeft.text = displayText
    }

    public fun resumeTimer() {
        if (timeCounterSecondsTotal.toInt() != 0) {
            resumeCountDownTimer()
        } else {
            resumeCountUpTimer()
        }
    }

    private fun startCountDownTimer() {
        var timeCounter = object : CountDownTimer((timeCounterSecondsTotal) * 1000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimeLimitText((millisUntilFinished / 1000).toInt())

                if (timeCounterOnPause) {
                    timeCounterSecondsCurrent = millisUntilFinished
                    timeCounterOnPause = false
                    this.cancel()
                }
            }

            override fun onFinish() {
                if (isFreeplay) {
                    setSharedPref(sharedPref, "timeTaken", timeTaken())
                    setSharedPref(sharedPref, "repeatComplete", repetitionCount)
                    completeExercise()
                } else {
                    //repetition required
                    if (repeatTime != 0) {
                        setSharedPref(sharedPref, "timeTaken", timeCounterSecondsTotal.toInt())
                        setSharedPref(sharedPref, "repeatComplete", repetitionCount)

                        //time up and not finish
                        inCompleteExercise()
                    }
                    //no repetition required
                    else {
                        setSharedPref(sharedPref, "timeTaken", timeCounterSecondsTotal.toInt())
                        setSharedPref(sharedPref, "repeatComplete", repetitionCount)
                        completeExercise()
                    }
                }
            }
        }.start()
    }

    private fun pauseCountDownTimer() {
        timeCounterOnPause = true
        timeCounterUpOnPause = true
    }

    public fun resumeCountDownTimer() {
        var timeCounter = object : CountDownTimer(timeCounterSecondsCurrent, 100) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimeLimitText((millisUntilFinished / 1000).toInt())

                if (timeCounterOnPause) {
                    timeCounterSecondsCurrent = millisUntilFinished
                    timeCounterOnPause = false
                    this.cancel()
                }
            }

            override fun onFinish() {
                if (isFreeplay) {
                    setSharedPref(sharedPref, "timeTaken", timeTaken())
                    setSharedPref(sharedPref, "repeatComplete", repetitionCount)
                    completeExercise()
                } else {
                    //repetition required
                    if (repeatTime != 0) {
                        setSharedPref(sharedPref, "timeTaken", timeCounterSecondsTotal.toInt())
                        setSharedPref(sharedPref, "repeatComplete", repetitionCount)

                        //time up and not finish
                        inCompleteExercise()
                    }
                    //no repetition required
                    else {
                        setSharedPref(sharedPref, "timeTaken", 0)
                        setSharedPref(sharedPref, "repeatComplete", repetitionCount)
                        completeExercise()
                    }
                }
            }
        }.start()
    }

    private fun startCountUpTimer() {
        var timeCounter = object : CountDownTimer(1000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (timeCounterUpOnPause) {
                    timeCounterUpSecondsCurrent = millisUntilFinished
                    timeCounterUpOnPause = false
                    this.cancel()
                }
            }

            override fun onFinish() {
                timeCountUp += 1
                startCountUpTimer()
            }
        }.start()
    }

    public fun resumeCountUpTimer() {
        var timeCounter = object : CountDownTimer(timeCounterUpSecondsCurrent, 100) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimeLimitText((millisUntilFinished / 1000).toInt())

                if (timeCounterUpOnPause) {
                    timeCounterUpSecondsCurrent = millisUntilFinished
                    timeCounterOnPause = false
                    this.cancel()
                }
            }

            override fun onFinish() {
                startCountUpTimer()
                timeCountUp += 1
            }
        }
    }

    private fun timeTaken(): Int {
        var result: Int = (timeCounterSecondsTotal - timeCounterSecondsCurrent).toInt()

        return result
    }

    private fun endExerciseRecord() {
        if (!isEnd) {
            isEnd = true

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

            exerciseRecord.repeatTimes = repetitionCount
            exerciseRecord.endTime = nowTime

            if (!isFreeplay) {
                exerciseRecord.completion = if (repetitionCount >= repeatTime) true else false
            }
            exerciseRecord.action.add(action)

            databaseCollection.document(exerciseRecord.id!!)
                .set(exerciseRecord)
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, "Successfully updated exercise ${exerciseRecord?.id}")
                }
        }
    }

    private fun startExerciseRecord() {
        var hour = if(LocalDateTime.now().hour < 0) "0" + LocalDateTime.now().hour else LocalDateTime.now().hour
        var minute = if(LocalDateTime.now().minute < 0) "0" + LocalDateTime.now().minute else LocalDateTime.now().minute
        var second = if(LocalDateTime.now().second < 0) "0" + LocalDateTime.now().second else LocalDateTime.now().second
        //record end exercise information to database
        var nowTime: String = hour.toString() + ":" + minute.toString() + ":" + second.toString()

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

            mode = if (isFreeplay) "Free-Play" else "Repetition",
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
                dataID = it.id
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error writing document", it)
            }
    }

    private fun exerciseRecord() {
        var hour = if(LocalDateTime.now().hour < 0) "0" + LocalDateTime.now().hour else LocalDateTime.now().hour
        var minute = if(LocalDateTime.now().minute < 0) "0" + LocalDateTime.now().minute else LocalDateTime.now().minute
        var second = if(LocalDateTime.now().second < 0) "0" + LocalDateTime.now().second else LocalDateTime.now().second
        //record end exercise information to database
        var nowTime: String = hour.toString() + ":" + minute.toString() + ":" + second.toString()

        //detail
        val action = ActionDetail(
            description = "Round " + repetitionCount + " Completed",
            actionTime = nowTime,
            actionType = "round",
            buttonCorrect = null
        )

        exerciseRecord.action.add(action)

        databaseCollection.document(exerciseRecord.id!!)
            .set(exerciseRecord)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Successfully updated movie ${exerciseRecord?.id}")
            }
    }

    private fun buttonPressRecord(isCorrect: Boolean, buttonPressed: Int) {
        //record button press information to database
        var nowTime: String =
            LocalDateTime.now().hour.toString() + ":" + LocalDateTime.now().minute.toString() + ":" + LocalDateTime.now().second.toString()

        //detail
        val action = ActionDetail(
            description = "Button " + buttonPressed + " pressed",
            actionTime = nowTime,
            actionType = "buttonPress",
            buttonCorrect = isCorrect
        )

        exerciseRecord.action.add(action)

        databaseCollection.document(exerciseRecord.id!!)
            .set(exerciseRecord)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Successfully updated exercise ${exerciseRecord?.id}")
            }
    }

    private fun repetitionReset() {
        if (randomButtonOrder) {
            randomButton(true)
            currentButtonAction = 0
        } else {
            randomButton(false)
            currentButtonAction = 0
        }
    }

    public fun takePhoto() {
        requestToTakeAPicture()
    }

    public fun selectPhoto() {
        selectAPicture()
    }

    private fun completeExercise() {
        //function called when the exercise successful completed

        for (i in 0..4) {
            buttons[i].visibility = View.INVISIBLE
        }

        pauseCountDownTimer()

        //database record
        endExerciseRecord()

        //show completed dialog ask for photo
        DialogFinishExercise().show(supportFragmentManager, "Dialog_Finish_Exercise")
    }

    private fun inCompleteExercise() {
        //function called when the exercise not successful completed

        for (i in 0..4) {
            buttons[i].visibility = View.INVISIBLE
        }

        pauseCountDownTimer()

        //database record
        endExerciseRecord()

        //show completed dialog ask for photo
        DialogFinishExerciseFail().show(supportFragmentManager, "Dialog_Finish_Exercise_Fail")
    }

    private fun buttonAction(buttonIndex: Int) {
        //pressed correct button
        if (currentButtonAction == buttonIndex) {
            currentButtonAction += 1

            //change button colors to next stage
            if (nextButtonIndication) {
                buttons[buttonIndex].setBackgroundColor(resources.getColor(R.color.btn_color_red))

                if (buttonIndex + 1 != buttons.size) {
                    buttons[buttonIndex + 1].setBackgroundColor(resources.getColor(R.color.btn_color_green))
                }
            }
            //correct button pressed
            buttonPressRecord(true, buttonIndex + 1)

            //if all buttons are pressed
            if (currentButtonAction == appearButtons) {
                if (isFreeplay) {
                    repetitionCount += 1

                    exerciseRecord()
                    repetitionReset()
                } else {
                    repetitionCount += 1
                    exerciseRecord()

                    //repetition exercise
                    if (repeatTime != 0) {
                        //meet the number of repetition
                        if (repetitionCount >= repeatTime) {
                            //no time limit
                            if (timeCounterSecondsTotal.toInt() == 0) {
                                setSharedPref(sharedPref, "timeTaken", timeCountUp)
                                setSharedPref(sharedPref, "repeatComplete", repetitionCount)
                            } else {
                                setSharedPref(sharedPref, "timeTaken", timeCountUp)
                                setSharedPref(sharedPref, "repeatComplete", repetitionCount)
                            }

                            completeExercise()
                        } else {
                            repetitionReset()
                        }
                    } else {
                        repetitionReset()
                    }
                }
            }
        } else {
            //wrong button pressed
            buttonPressRecord(false, buttonIndex + 1)
        }

        ui.textViewRepeatDone.text = repetitionCount.toString()
    }

    private fun randomButton(isRandom: Boolean) {
        for (i in 0..4) {
            //show appear buttons
            if (i < appearButtons) {
                //random button order if needed
                if (isRandom) {
                    //first button no need worry about overlapping
                    if (i == 0) {
                        randomButtonPosition(buttons[i], BUTTON_SIZE[buttonSizeIndex])
                    } else {
                        do {
                            var overLapping: Boolean = false

                            randomButtonPosition(buttons[i], BUTTON_SIZE[buttonSizeIndex])
                            for (j in 0..(i - 1)) {
                                if (checkButtonOverlap(
                                        buttons[i],
                                        buttons[j],
                                        BUTTON_SIZE[buttonSizeIndex].toFloat()
                                    )
                                ) {
                                    overLapping = true
                                }
                            }
                        } while (overLapping)
                    }

                    //default all button color to red if indication is on
                    if (nextButtonIndication) {
                        buttons[i].setBackgroundColor(resources.getColor(R.color.btn_color_red))
                    }
                }

                buttons[i].visibility = View.VISIBLE
            } else {
                buttons[i].visibility = View.INVISIBLE
            }
        }

        //default first button color to red if indication is on
        if (nextButtonIndication) {
            ui.btnRoundButton1.setBackgroundColor(resources.getColor(R.color.btn_color_green))
        }
    }

    private fun randomButtonPosition(button: MaterialButton, buttonSize: Int) {
        var randomX: Int = Random.nextInt(16 * 2, 1200 * 2 - buttonSize)
        var randomY: Int = Random.nextInt(16 * 2, 584 * 2 - buttonSize)

        button.x = randomX.toFloat()
        button.y = randomY.toFloat()
    }

    private fun checkButtonOverlap(
        button1: MaterialButton,
        button2: MaterialButton,
        buttonSize: Float
    ): Boolean {
        if (button1.x >= button2.x + buttonSize || button2.x >= button1.x + buttonSize) {
            return false
        }

        if (button1.y + buttonSize <= button2.y || button2.y + buttonSize <= button1.y) {
            return false
        }
        return true
    }

    private fun setSharedPref(sharedPref: SharedPreferences, key: String, value: Int) {
        with(sharedPref.edit())
        {
            putInt(key, value)
            apply()
        }
    }

    private fun setSharedPref(sharedPref: SharedPreferences, key: String, value: String) {
        with(sharedPref.edit())
        {
            putString(key, value)
            apply()
        }
    }

    private fun setSharedPref(sharedPref: SharedPreferences, key: String, value: Boolean) {
        with(sharedPref.edit())
        {
            putBoolean(key, value)
            apply()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestToTakeAPicture() {
        Log.d("camera", "in take picture")
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_IMAGE_CAPTURE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("camera", "in permission result")

        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("camera", "in permission result if")
                    // Permission is granted.
                    takeAPicture() // NEXT SLIDE
                } else {
                    Log.d("camera", "in permission result else")
                    Toast.makeText(this, "Cannot access camera", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun takeAPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //try {
        val photoFile: File = createImageFile()!!
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "au.edu.utas.ssun1.kit203assignment2",
            photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun selectAPicture()
    {
        val selectPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(selectPictureIntent, REQUEST_IMAGE_SELECT)
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //take a picture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var ImageUri = Uri.fromFile(File(currentPhotoPath))
            setPic(ui.imageViewCameraPicture)

            val storage = Firebase.storage.reference.child("image/${dataID}.jpg")
            storage.putFile(ImageUri)
                .addOnSuccessListener {
                    Log.d("firebase_storage", "image added")
                    Toast.makeText(this@ActivityRepetitionExercise, "Picture uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.d("firebase_storage", "image add failed")
                    Toast.makeText(this@ActivityRepetitionExercise, "Picture upload failed", Toast.LENGTH_SHORT).show()
                }
        }

        //select a picture
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            ImageUri = data?.data!!

            ui.imageViewCameraPicture.setImageURI(ImageUri)

            val storage = Firebase.storage.reference.child("image/${dataID}.jpg")
            storage.putFile(ImageUri)
                .addOnSuccessListener {
                    Log.d("firebase_storage", "image added")
                    Toast.makeText(this@ActivityRepetitionExercise, "Picture uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.d("firebase_storage", "image add failed")
                    Toast.makeText(this@ActivityRepetitionExercise, "Picture upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height
        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight
            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))
            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }
}