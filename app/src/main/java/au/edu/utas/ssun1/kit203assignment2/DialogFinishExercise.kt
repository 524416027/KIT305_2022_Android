package au.edu.utas.ssun1.kit203assignment2

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogFinishExercise : DialogFragment()
{
    private lateinit var sharedPref : SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var inflated = inflater.inflate(R.layout.dialog_finish_exercise, container, false)

        //get sharedPref reference
        sharedPref = requireActivity().getSharedPreferences("au.edu.utas.ssun1.kit203assignment2", Context.MODE_PRIVATE)

        var timeTaken = sharedPref.getInt("timeTaken", 0)
        var repeatComplete = sharedPref.getInt("repeatComplete", 0)

        if(timeTaken != 0 || repeatComplete != 0)
        {
            var minutes = timeTaken / 60
            var seconds = timeTaken % 60

            var minutesText : String = if(minutes < 10) "0" + minutes.toString() else minutes.toString()
            var secondsText : String = if(seconds < 10) "0" + seconds.toString() else seconds.toString()

            inflated.findViewById<TextView>(R.id.textViewTimeTaken).text = minutesText + ":" + secondsText
            inflated.findViewById<TextView>(R.id.textViewRepeatCompleted).text = repeatComplete.toString()

            if(timeTaken == 0)
            {
                //disable text view
                inflated.findViewById<TextView>(R.id.textViewTimeTakenText).visibility = View.INVISIBLE
                inflated.findViewById<TextView>(R.id.textViewTimeTaken).visibility = View.INVISIBLE
            }

            if(repeatComplete == 0)
            {
                //disable text view
                inflated.findViewById<TextView>(R.id.textViewRepeatCompletedText).visibility = View.INVISIBLE
                inflated.findViewById<TextView>(R.id.textViewRepeatCompleted).visibility = View.INVISIBLE
            }
        }

        inflated.findViewById<Button>(R.id.btnBackToMenu2).setOnClickListener()
        {
            (context as? ActivityRepetitionExercise)?.BackToMenu()
            this.dismiss()
        }

        inflated.findViewById<Button>(R.id.btnSelectPhoto).setOnClickListener()
        {
            (context as? ActivityRepetitionExercise)?.selectPhoto()
            this.dismiss()
        }

        inflated.findViewById<Button>(R.id.btnGoTakePhoto).setOnClickListener()
        {
            (context as? ActivityRepetitionExercise)?.takePhoto()
            this.dismiss()
        }

        return inflated
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog
    {
        //disable outside clicking of the dialog
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }
}