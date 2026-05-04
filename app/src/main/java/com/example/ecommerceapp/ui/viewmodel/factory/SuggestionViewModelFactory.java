package com.example.ecommerceapp.ui.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.data.repository.ProductEvaluationRepository;
import com.example.ecommerceapp.data.repository.RecommendationsRepository;
import com.example.ecommerceapp.ui.viewmodel.user.SuggestionViewModel;

public class SuggestionViewModelFactory implements ViewModelProvider.Factory {
    private final RecommendationsRepository recRepo;
    private final ProductEvaluationRepository evalRepo;

    public SuggestionViewModelFactory(RecommendationsRepository recRepo, ProductEvaluationRepository evalRepo) {
        this.recRepo = recRepo;
        this.evalRepo = evalRepo;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SuggestionViewModel.class)) {
            return (T) new SuggestionViewModel(recRepo, evalRepo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
