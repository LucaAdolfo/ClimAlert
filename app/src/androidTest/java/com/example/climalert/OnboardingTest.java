//Testo l'effettivo funzionamento dell'onboarding dell'applicazione
package com.example.climalert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OnboardingTest {

    @Test
    public void onboardingActivity_startsSuccessfully() {
        ActivityScenario<OnBoardingSliderActivity> scenario =
                ActivityScenario.launch(OnBoardingSliderActivity.class);

        scenario.onActivity(activity -> {
            //entro qui solo nel caso in cui l'activity non è crashata
            assertNotNull(activity);
            assertTrue(!activity.isFinishing());
        });
        scenario.close();
    }
}
