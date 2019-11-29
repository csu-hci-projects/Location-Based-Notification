package com.quickbirdstudios.test.pages

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.quickbirdstudios.test.R
import org.hamcrest.CoreMatchers

internal fun PageTest.testMultipleChoiceStep(context: Context) {
    checkIfTitleInfoAndContinueAreDisplayed()

    onView(withId(R.id.button_continue)).check(matches(CoreMatchers.not(isEnabled())))
    onView(withId(R.id.multipleChoicePart)).check(matches(isDisplayed()))

    onView(withText(context.getString(R.string.allergies_back_penicillin))).perform(scrollTo(), click())
    onView(withText(context.getString(R.string.allergies_pollen))).perform(scrollTo(), click())

    continueToNextStep()
}
