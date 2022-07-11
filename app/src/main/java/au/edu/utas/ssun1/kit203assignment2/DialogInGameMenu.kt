package au.edu.utas.ssun1.kit203assignment2

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class DialogInGameMenu : DialogFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var inflated = inflater.inflate(R.layout.dialog_in_game_menu, container, false)

        inflated.findViewById<Button>(R.id.btnBackToMenu).setOnClickListener()
        {
            //ask activity call out confirmation fragment
            (context as? ActivityRepetitionExercise)?.ConfirmationBackToMainMenu()
            (context as? ActivityDesignedExercise)?.ConfirmationBackToMainMenu()
            this.dismiss()
        }

        inflated.findViewById<Button>(R.id.btnRestart).setOnClickListener()
        {
            //ask activity call out confirmation fragment
            (context as? ActivityRepetitionExercise)?.ConfirmationRestart()
            (context as? ActivityDesignedExercise)?.ConfirmationRestart()
            this.dismiss()
        }

        inflated.findViewById<Button>(R.id.btnBackToExercise).setOnClickListener()
        {
            (context as? ActivityRepetitionExercise)?.resumeTimer()
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