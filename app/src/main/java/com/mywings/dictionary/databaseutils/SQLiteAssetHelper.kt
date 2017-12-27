package com.mywings.dictionary.databaseutils

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.InputStream
import java.util.regex.Pattern


class SQLiteAssetHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int, errorHandler: DatabaseErrorHandler?) : SQLiteOpenHelper(context, name, factory, version, errorHandler) {


    //region Field Declaration
    private lateinit var mUpgradePathFormat: String
    private lateinit var mContext: Context
    //endregion Field Declaration


    init {

    }


    override fun onCreate(db: SQLiteDatabase?) {

    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val paths = ArrayList<String>()
        getUpgradeFilePaths(oldVersion, newVersion - 1, newVersion, paths)
        if (paths.isEmpty()) {
            throw SQLiteAssetException("no upgrade script path from "
                    + oldVersion + " to " + newVersion)
        }

    }

    private fun getUpgradeFilePaths(baseVersion: Int, start: Int, end: Int, paths: ArrayList<String>) {
        var a: Int
        var b: Int
        var `is`: InputStream? = getUpgradeSQLStream(start, end)
        if (null != `is`) {
            val path = String.format(mUpgradePathFormat, start, end)
            paths.add(path)
            a = start - 1
            b = end
            `is` = null
        } else {
            a = start - 1
            b = end
        }

        if (a < baseVersion) {
            return
        } else {
            getUpgradeFilePaths(baseVersion, a, b, paths)
        }
    }

    private fun getUpgradeSQLStream(oldVersion: Int, newVersion: Int): InputStream? {
        val path = String.format(mUpgradePathFormat, oldVersion, newVersion)
        val `is` = mContext.assets.open(path)
        return `is`
    }

    inner class VersionComparator : Comparator<String> {

        private val pattern = Pattern
                .compile(".*_upgrade_([0-9]+)-([0-9]+).*")


        override fun compare(p0: String?, p1: String?): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }


}