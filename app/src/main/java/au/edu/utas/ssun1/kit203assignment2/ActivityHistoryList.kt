package au.edu.utas.ssun1.kit203assignment2

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.ssun1.kit203assignment2.databinding.ActivityHistoryListBinding
import au.edu.utas.ssun1.kit203assignment2.databinding.ListHistoryBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

val items = mutableListOf<RepetitionExercise>()

class ActivityHistoryList : AppCompatActivity() {
    private lateinit var ui : ActivityHistoryListBinding
    private val db = Firebase.firestore
    private val databaseCollection = db.collection("stroke")

    private var correctButtonCount : Int = 0
    private var shareText : String = ""

    private var filterButtonIndex : Int = 0 //0: show all, 1: show completed, 2: show in-completed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityHistoryListBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.historyList.adapter = HistoryAdapter(exercises = items)

        ui.historyList.layoutManager = LinearLayoutManager(this)

        databaseCollection
            .get()
            .addOnSuccessListener { result ->
                items.clear()
                correctButtonCount = 0
                shareText = ""
                Log.d("firebase_tag", "--- all exercises ---")
                for (document in result)
                {
                    val history = document.toObject<RepetitionExercise>()
                    history.id = document.id
                    Log.d(FIREBASE_TAG, history.toString())

                    var singleText = history.mode.toString() + " mode of " + history.repeatTimes.toString() + " round, start at " + history.startTime.toString() + " and finish at " + history.endTime.toString()
                    shareText += singleText  + "\n"

                    for(detail in history.action)
                    {
                        if(detail.buttonCorrect == true)
                        {
                            correctButtonCount += 1
                        }
                    }

                    items.add(history)
                }
                (ui.historyList.adapter as HistoryAdapter).notifyDataSetChanged()
                ui.textViewTotalCorrectButtons.text = correctButtonCount.toString()
                ui.textViewTotalCorrectButtons.text = correctButtonCount.toString()
            }

        ui.btnBackToMenu4.setOnClickListener()
        {
            val i = Intent(this, ActivityMainMenu::class.java)
            startActivity(i)
        }

        ui.btnSwapFilter.setOnClickListener()
        {
            filterButtonIndex += 1
            if(filterButtonIndex > 2)
            {
                filterButtonIndex = 0
            }

            when(filterButtonIndex)
            {
                0 ->
                {
                    ui.btnSwapFilter.text = getString(R.string.all)
                }
                1 ->
                {
                    ui.btnSwapFilter.text = getString(R.string.completed)
                }
                2 ->
                {
                    ui.btnSwapFilter.text = getString(R.string.inCompleted)
                }
            }

            databaseCollection
                .get()
                .addOnSuccessListener { result ->
                    items.clear()
                    Log.d("firebase_tag", "--- all exercises ---")
                    for (document in result)
                    {
                        val history = document.toObject<RepetitionExercise>()
                        history.id = document.id
                        Log.d(FIREBASE_TAG, history.toString())

                        when(filterButtonIndex)
                        {
                            0 ->
                            {
                                items.add(history)
                            }
                            1 ->
                            {
                                if(history.completion == true)
                                {
                                    items.add(history)
                                }
                            }
                            2 ->
                            {
                                if(history.completion == false)
                                {
                                    items.add(history)
                                }
                            }
                        }
                    }
                    (ui.historyList.adapter as HistoryAdapter).notifyDataSetChanged()
                }
        }

        ui.btnShareRecordList.setOnClickListener()
        {
            var sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via..."))
        }
    }

    inner class HistoryHolder(var ui: ListHistoryBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class HistoryAdapter(private val exercises: MutableList<RepetitionExercise>) : RecyclerView.Adapter<HistoryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
            val ui = ListHistoryBinding.inflate(layoutInflater, parent, false)
            return HistoryHolder(ui)
        }

        override fun getItemCount(): Int {
            return exercises.size
        }

        override fun onBindViewHolder(holder: ActivityHistoryList.HistoryHolder, position: Int) {
            val exercise = exercises[position]
            holder.ui.textViewModeName.text = exercise.mode
            holder.ui.textViewRepeatTime.text = exercise.repeatTimes.toString()
            holder.ui.textViewStartTime.text = exercise.startTime
            holder.ui.textViewEndTime.text = exercise.endTime

            val imageName = exercise.id
            val storageRef = FirebaseStorage.getInstance().reference.child("image/$imageName.jpg")

            val localfile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localfile)
                .addOnSuccessListener {
                    Log.d("firebase_storage", "image loaded")
                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    holder.ui.imageView.setImageBitmap(bitmap)
                }
                .addOnFailureListener{
                    Log.d("firebase_storage", "image load failed")
                }

            if(position % 2 == 0)
            {
                holder.ui.linearLayoutHistory.setBackgroundColor(resources.getColor(R.color.light_grey))
            }
            else
            {
                holder.ui.linearLayoutHistory.setBackgroundColor(resources.getColor(R.color.white))
            }

            holder.ui.root.setOnClickListener {
                var i = Intent(holder.ui.root.context, ActivityHistoryDetail::class.java)
                i.putExtra("exercise_index", position)
                startActivity(i)
            }
        }
    }
}