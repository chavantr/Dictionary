package com.mywings.dictionary.databaseutils

import android.database.sqlite.SQLiteException


class SQLiteAssetException(error: String?) : SQLiteException(error) {
    private val serialVersionUID = 1L
}