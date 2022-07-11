package au.edu.utas.ssun1.kit203assignment2

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogConfirmationRestartExercise : DialogFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var inflated = inflater.inflate(R.layout.dialog_confirmation_restart_exercise, container, false)

        inflated.findViewById<Button>(R.id.btnConfirmRestartYes).setOnClickListener()
        {
            (context as? ActivityRepetitionExercise)?.RestartExercise()
            (context as? ActivityDesignedExercise)?.RestartExercise()
            this.dismiss()
        }

        inflated.findViewById<Button>(R.id.btnConfirmRestartNo).setOnClickListener()
        {
            (context as? ActivityRepetitionExercise)?.resumeCountDownTimer()
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