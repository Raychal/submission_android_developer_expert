package com.raychal.submissionandroiddeveloperexpert.di

import com.raychal.submissionandroiddeveloperexpert.ui.detail.DetailViewModel
import com.raychal.submissionandroiddeveloperexpert.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
}
