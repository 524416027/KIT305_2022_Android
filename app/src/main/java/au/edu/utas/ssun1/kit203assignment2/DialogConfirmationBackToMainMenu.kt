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

class DialogConfirmationBackToMainMenu : DialogFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var inflated = inflater.inflate(R.layout.dialog_confirmation_back_to_main_menu, container, false)

        inflated.findViewById<Button>(R.id.btnConfirmBackToMainMenuYes).setOnClickListener()
        {
            (context as? ActivityRepetitionExercise)?.BackToMenu()
            (context as? ActivityDesignedExercise)?.BackToMenu()
            this.dismiss()
        }

        inflated.findViewById<Button>(R.id.btnConfirmBackToMainMenuNo).setOnClickListener()
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