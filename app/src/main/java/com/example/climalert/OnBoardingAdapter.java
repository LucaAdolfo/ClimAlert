package com.example.climalert;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnBoardingAdapter extends FragmentStateAdapter {

    public OnBoardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //restituisce il fragment corretto in base alla posizione
        switch (position) {
            case 0:
                return new OnBoardingStep1Fragment();
            case 1:
                return new OnBoardingStep2Fragment();
            case 2:
                return new OnBoardingStep3Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        //numero di schermate di onboarding
        return 3;
    }
}
