package au.edu.utas.ssun1.kit203assignment2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.ssun1.kit203assignment2.databinding.ActivityHistoryDetailListBinding
import au.edu.utas.ssun1.kit203assignment2.databinding.ListHistoryBinding
import au.edu.utas.ssun1.kit203assignment2.databinding.ListHistoryDetailBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val actionItems = mutableListOf<ActionDetail>()

class ActivityHistoryDetail : AppCompatActivity() {
    private lateinit var ui : ActivityHistoryDetailListBinding
    private val db = Firebase.firestore
    private val databaseCollection = db.collection("stroke")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityHistoryDetailListBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val exerciseID = intent.getIntExtra("exercise_index", -1)
        var exerciseObject = items[exerciseID]

        ui.historyDetailList.adapter = HistoryDetailAdapter(actionDetails = actionItems)
        ui.historyDetailList.layoutManager = LinearLayoutManager(this)

        actionItems.clear()

        for (actionDetail in exerciseObject.action)
        {
            actionItems.add(actionDetail)
        }
        (ui.historyDetailList.adapter as HistoryDetailAdapter).notifyDataSetChanged()

        ui.btnBackToHistoryList.setOnClickListener {
            val i = Intent(this, ActivityHistoryList::class.java)
            startActivity(i)
        }

        ui.btnDeleteRecord.setOnClickListener {
            DialogConfirmationDeleteHistory().show(supportFragmentManager, "Dialog_Confirmation_Delete_History")
        }

        ui.btnShareRecord.setOnClickListener {
            var shareText : String = ""

            for (actionDetail in exerciseObject.action)
            {
                if(actionDetail.actionType == "buttonPress")
                {
                    var buttonText = actionDetail.description + if(actionDetail.buttonCorrect == true) " correctly" else " wrong"
                    shareText += buttonText + " at " + actionDetail.actionTime + "\n"
                }
                else
                {
                    shareText += actionDetail.description + " at " + actionDetail.actionTime + "\n"
                }
            }


            var sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via..."))
        }
    }

    public fun deleteConfirmed()
    {
        val exerciseID = intent.getIntExtra("exercise_index", -1)
        var exerciseObject = items[exerciseID]

        databaseCollection.document(exerciseObject.id.toString())
            .delete()
            .addOnSuccessListener { Log.d("firebase_tag", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("firebase_tag", "Error deleting document", e) }

        val i = Intent(this, ActivityHistoryList::class.java)
        startActivity(i)
    }

    inner class HistoryDetailHolder(var ui: ListHistoryDetailBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class HistoryDetailAdapter(private val actionDetails: MutableList<ActionDetail>) : RecyclerView.Adapter<HistoryDetailHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHistoryDetail.HistoryDetailHolder {
            val ui = ListHistoryDetailBinding.inflate(layoutInflater, parent, false)
            return HistoryDetailHolder(ui)
        }

        override fun getItemCount(): Int {
            return actionDetails.size
        }

        override fun onBindViewHolder(holder: ActivityHistoryDetail.HistoryDetailHolder, position: Int) {
            val actionDetail = actionDetails[position]

            holder.ui.textViewAction.text = actionDetail.description

            if(actionDetail.actionType != "buttonPress")
            {
                holder.ui.textViewButtonAction.visibility = View.INVISIBLE
            }
            else
            {
                holder.ui.textViewButtonAction.visibility = View.VISIBLE

                if(actionDetail.buttonCorrect == true)
                {
                    holder.ui.textViewButtonAction.text = getString(R.string.correct)
                    holder.ui.textViewButtonAction.setTextColor(resources.getColor(R.color.btn_color_green))
                }
                else
                {
                    holder.ui.textViewButtonAction.text = getString(R.string.wrong)
                    holder.ui.textViewButtonAction.setTextColor(resources.getColor(R.color.btn_color_red))
                }
            }
            holder.ui.textViewActionTime.text = actionDetail.actionTime

            if(position % 2 == 0)
            {
                holder.ui.linearLayoutHistoryDetail.setBackgroundColor(resources.getColor(R.color.light_grey))
            }
            else
            {
                holder.ui.linearLayoutHistoryDetail.setBackgroundColor(resources.getColor(R.color.white))
            }
        }
    }
}