package fr.univartois.feeder;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class ClickLoadBehaviorTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);
    private MainActivity mMainActivity;

    @Before
    public void setup()
    {
         mMainActivity = activityRule.getActivity();
    }

    @Test
    public void load_clicked() {
        onView(withId(R.id.load_btn))
                .perform(click());
        onData(instanceOf(RssItem.class))
                .inAdapterView(allOf(withId(R.id.load_list), isDisplayed()))
                .atPosition(1)
                .check(matches(isDisplayed()));

    }

}
