package com.raychal.core.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raychal.core.domain.model.GameMovie
import java.lang.reflect.Type

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType: Type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    @TypeConverter
    fun fromGameMovieList(value: String?): List<GameMovie> {
        if (value == null) return emptyList()
        val listType: Type = object : TypeToken<List<GameMovie>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toGameMovieList(list: List<GameMovie>?): String {
        return gson.toJson(list ?: emptyList<GameMovie>())
    }
}
