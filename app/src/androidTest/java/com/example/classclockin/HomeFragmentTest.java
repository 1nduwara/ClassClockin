package com.example.classclockin;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testMarkAttendanceNavigation() {
        // Test if clicking on Mark Attendance navigates to the correct fragment
        onView(withId(R.id.crd2_txt_markAttendance)).perform(click());
    }

    @Test
    public void testAttendanceSummaryNavigation() {
        // Test if clicking on Attendance Summary navigates to the correct fragment
        onView(withId(R.id.crd3_txt_attendanceSummary)).perform(click());
    }

    @Test
    public void testViewStudentListNavigation() {
        // Test if clicking on View Student List navigates to the correct fragment
        onView(withId(R.id.crd4_txt_view_stu_list)).perform(click());
    }

    @Test
    public void testAccountNavigation() {
        // Test if clicking on the account icon navigates to the account fragment
        onView(withId(R.id.nav_account)).perform(click());
    }

    @Test
    public void testNotificationNavigation() {
        // Test if clicking on the notification icon navigates to the notification fragment
        onView(withId(R.id.nav_notification)).perform(click());
}
}