package com.raychal.favorite.ui

import org.koin.dsl.module

val favoriteModule = module {
    factory { FavoriteViewModel(get(), get()) }
}