package au.edu.utas.ssun1.kit203assignment2

class RepetitionExercise
    (
    var id : String? = null,

    var mode : String? = null,
    var repeatTimes : Int? = null,
    var startTime : String? = null,
    var endTime : String? = null,
    var completion : Boolean? = null,
    var action : MutableList<ActionDetail> = mutableListOf()
)