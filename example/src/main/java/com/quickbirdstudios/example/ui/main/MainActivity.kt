package com.quickbirdstudios.example.ui.main

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.quickbirdstudios.example.R
import com.quickbirdstudios.surveykit.AnswerFormat
import com.quickbirdstudios.surveykit.FinishReason
import com.quickbirdstudios.surveykit.Identifier
import com.quickbirdstudios.surveykit.ImageChoice
import com.quickbirdstudios.surveykit.NavigableOrderedTask
import com.quickbirdstudios.surveykit.NavigationRule
import com.quickbirdstudios.surveykit.StepIdentifier
import com.quickbirdstudios.surveykit.SurveyTheme
import com.quickbirdstudios.surveykit.TextChoice
import com.quickbirdstudios.surveykit.backend.views.main_parts.AbortDialogConfiguration
import com.quickbirdstudios.surveykit.backend.views.step.StepView
import com.quickbirdstudios.surveykit.result.QuestionResult
import com.quickbirdstudios.surveykit.result.StepResult
import com.quickbirdstudios.surveykit.result.TaskResult
import com.quickbirdstudios.surveykit.steps.CompletionStep
import com.quickbirdstudios.surveykit.steps.InstructionStep
import com.quickbirdstudios.surveykit.steps.QuestionStep
import com.quickbirdstudios.surveykit.steps.Step
import com.quickbirdstudios.surveykit.survey.SurveyView
import java.util.Date
import kotlinx.android.parcel.Parcelize

class MainActivity : AppCompatActivity() {

    private lateinit var survey: SurveyView
    private lateinit var container: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        survey = findViewById(R.id.survey_view)
        container = findViewById(R.id.surveyContainer)
        setupSurvey(survey)
    }

    private fun setupSurvey(surveyView: SurveyView) {
        val steps = listOf(
            InstructionStep(
                title = "Hello Participant 00",
                text = "Thanks for Participating! \n Please fill out my little survey!",
                buttonText = "start"
            ),
            QuestionStep(
                title = "Current Area",
                text = "Where are you currently as of this notification \n " +
                        "Ex:Home,Work,School,Gym,or \n" +
                        "Private if you would not like to say",
                answerFormat = AnswerFormat.TextAnswerFormat(maxLines = 5)
            ),
            QuestionStep(
                title ="Rreception",
                text = "How Unpleasant was reception of this notification(1-not,7-Very Unpleasant)",
                answerFormat = AnswerFormat.ScaleAnswerFormat(
                    minimumValue = 1,
                    maximumValue = 7,
                    minimumValueDescription = this.resources.getString(R.string.how_fat_min),
                    maximumValueDescription = this.resources.getString(R.string.how_fat_max),
                    step = 1f,
                    defaultValue = 4
                )
            ),
            QuestionStep(
                title ="Interrupted",
                text = "How interruptive was this notification(1-not,7-Very interruptive)",
                answerFormat = AnswerFormat.ScaleAnswerFormat(
                    minimumValue = 1,
                    maximumValue = 7,
                    minimumValueDescription = this.resources.getString(R.string.how_fat_min),
                    maximumValueDescription = this.resources.getString(R.string.how_fat_max),
                    step = 1f,
                    defaultValue = 4
                )
            ),
            QuestionStep(
                title = "Location Activity",
                text = "What Activity were you doing \n before this notification \n ex:Programming,Homework,School Work",
                answerFormat = AnswerFormat.TextAnswerFormat(maxLines = 5)
            ),
            QuestionStep(
                title ="Activity Engagement",
                text = "How interruptive was this notification to that Activity(1-not,7-Very interruptive)",
                answerFormat = AnswerFormat.ScaleAnswerFormat(
                    minimumValue = 1,
                    maximumValue = 7,
                    minimumValueDescription = this.resources.getString(R.string.how_fat_min),
                    maximumValueDescription = this.resources.getString(R.string.how_fat_max),
                    step = 1f,
                    defaultValue = 4
                )
            ),
            InstructionStep(
                title = "Location",
                text = "",
                buttonText = "start"
            ),
            CompletionStep(
                title = this.resources.getString(R.string.finish_question_title),
                text = this.resources.getString(R.string.finish_question_text),
                buttonText = this.resources.getString(R.string.finish_question_submit)
            )
        )

        val task = NavigableOrderedTask(steps = steps)

        task.setNavigationRule(
            steps[5].id,
            NavigationRule.DirectStepNavigationRule(
                destinationStepStepIdentifier = steps[6].id
            )
        )

        task.setNavigationRule(
            steps[7].id,
            NavigationRule.ConditionalDirectionStepNavigationRule(
                resultToStepIdentifierMapper = { input ->
                    when (input) {
                        "Ja" -> steps[7].id
                        "Nein" -> steps[0].id
                        else -> null
                    }
                }
            )
        )

        surveyView.onSurveyFinish = { taskResult: TaskResult, reason: FinishReason ->
            if (reason == FinishReason.Completed) {
                taskResult.results.forEach { stepResult ->
                    Log.e("ASDF", "answer ${stepResult.results.firstOrNull()}")
                    container.removeAllViews()
                }
            }
        }

        val configuration = SurveyTheme(
            themeColorDark = ContextCompat.getColor(this, R.color.cyan_dark),
            themeColor = ContextCompat.getColor(this, R.color.cyan_normal),
            textColor = ContextCompat.getColor(this, R.color.cyan_text),
            abortDialogConfiguration = AbortDialogConfiguration(
                title = R.string.title,
                message = R.string.message,
                neutralMessage = R.string.no,
                negativeMessage = R.string.yes
            )
        )

        surveyView.start(task, configuration)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            survey.backPressed()
            true
        } else false
    }
}

class CustomStep : Step {
    override val isOptional: Boolean = true
    override val id: StepIdentifier =
        StepIdentifier()
    val tmp = id

    override fun createView(context: Context, stepResult: StepResult?): StepView {
        return object : StepView(context, id, isOptional) {

            override fun setupViews() = Unit

            val root = View.inflate(context, R.layout.custom_step, this)

            override fun createResults(): QuestionResult =
                CustomResult(
                    root.findViewById<EditText>(R.id.input).text.toString(),
                    "stringIdentifier",
                    id,
                    Date(),
                    Date()
                )

            override fun isValidInput(): Boolean = this@CustomStep.isOptional

            override var isOptional: Boolean = this@CustomStep.isOptional
            override val id: StepIdentifier = tmp

            override fun style(surveyTheme: SurveyTheme) {
                // do styling here
            }

            init {
                root.findViewById<Button>(R.id.continue_button)
                    .setOnClickListener { onNextListener(createResults()) }
                root.findViewById<Button>(R.id.back_button)
                    .setOnClickListener { onBackListener(createResults()) }
                root.findViewById<Button>(R.id.close_button)
                    .setOnClickListener { onCloseListener(createResults(), FinishReason.Completed) }
                root.findViewById<Button>(R.id.skip_button)
                    .setOnClickListener { onSkipListener() }
                root.findViewById<EditText>(R.id.input).setText(
                    (stepResult?.results?.firstOrNull() as? CustomResult)?.customData ?: ""
                )
            }
        }
    }
}

@Parcelize
data class CustomResult(
    val customData: String,
    override val stringIdentifier: String,
    override val id: Identifier,
    override val startDate: Date,
    override var endDate: Date
) : QuestionResult, Parcelable
