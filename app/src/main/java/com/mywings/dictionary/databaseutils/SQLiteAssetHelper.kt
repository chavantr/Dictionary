package com.mywings.dictionary.databaseutils

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.*
import java.util.*
import java.util.regex.Pattern
import java.util.zip.ZipInputStream


class SQLiteAssetHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int, errorHandler: DatabaseErrorHandler?) : SQLiteOpenHelper(context, name, factory, version, errorHandler) {

    //region Field Declaration
    private lateinit var mUpgradePathFormat: String
    private var mContext: Context
    private var mName: String
    private var mFactory: SQLiteDatabase.CursorFactory
    private var mNewVersion: Int? = null
    private lateinit var mDatabase: SQLiteDatabase
    private var mIsInitializing: Boolean = false
    private lateinit var mDatabasePath: String
    private lateinit var mArchivePath: String
    private var mForcedUpgradeVersion: Int = 0
    //endregion Field Declaration

    init {
        if (version < 0) throw IllegalArgumentException("Version must be >= 1, was version $version")
        if (null == name) throw IllegalArgumentException("Database name cannot be null")
        mContext = context!!
        mName = name
        mFactory = factory!!
        mNewVersion = version
        mArchivePath = name + ".zip"


    }


    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        if (null != mDatabase && mDatabase.isOpen) {
            return mDatabase
        }
        if (mIsInitializing) {
            throw IllegalStateException("getReadableDatabase called recursively")
        }
        try {
            return writableDatabase

        } catch (e: SQLiteException) {
            if (null == mName) throw e
        }
        var db: SQLiteDatabase? = null
        try {
            mIsInitializing = true
            val path = mContext.getDatabasePath(mName).path
            db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READONLY)
            if (db.version != mNewVersion) throw SQLiteException("Can't upgrade read-only database from version ${db.version} to $mNewVersion : $path")
            onOpen(db)
            mDatabase = db
            return mDatabase
        } finally {
            mIsInitializing = false
            if (null != mDatabase && db != mDatabase) db!!.close()
        }
    }

    private fun copyDatabaseFromAssets() {
        try {
            val zipFileInputStream = mContext.assets.open(mArchivePath)
            val f = File(mDatabasePath + "/")
            if (!f.exists()) f.mkdir()
            val zipInputStream = getFileFromZip(zipFileInputStream) ?: throw SQLiteAssetException("Archive is missing a SQLite database file")
            writeExtractedFileToDisk(zipInputStream, FileOutputStream(mDatabasePath + "/" + mName) as OutputStream)
        } catch (e: FileNotFoundException) {
            val se = SQLiteAssetException("Missing $mArchivePath file in assets or target folder not writable")
            se.stackTrace = e.stackTrace
            throw se
        } catch (e: IOException) {
            val se = SQLiteAssetException("Unable to extract $mArchivePath to data directory")
            se.stackTrace = e.stackTrace
            throw se
        }
    }

    private fun writeExtractedFileToDisk(zipInputStream: ZipInputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024)
        while ((zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer)
        }
        outputStream.flush()
        outputStream.close()
        zipInputStream.close()

    }


    private fun getFileFromZip(`zipFileStream`: InputStream): ZipInputStream? {
        val zipInputStream = ZipInputStream(zipFileStream)
        while ((zipInputStream.nextEntry) != null) {
            return zipInputStream
        }
        return null
    }

    private fun createOrOpenDatabase(force: Boolean): SQLiteDatabase? {
        var db = returnDatabase()
        if (null != db) {
            if (force) {
                copyDatabaseFromAssets()
                db = returnDatabase()
            }
            return db
        } else {
            copyDatabaseFromAssets()
            db = returnDatabase()
            return db
        }
        return null
    }

    private fun returnDatabase(): SQLiteDatabase? {
        return try {
            SQLiteDatabase.openDatabase(mDatabasePath + "/" + mName, mFactory, SQLiteDatabase.OPEN_READWRITE)
        } catch (e: SQLiteException) {
            null
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        if (null != mDatabase && mDatabase.isOpen && !mDatabase.isReadOnly) {
            return mDatabase
        }
        if (mIsInitializing) throw IllegalStateException("getWritableDatabase called recursively")
        var success = false
        var db: SQLiteDatabase? = null
        try {
            mIsInitializing = true
            db = createOrOpenDatabase(false)
            var version = db!!.version
            if (version != 0 && version < mForcedUpgradeVersion) {
                db = createOrOpenDatabase(true)
                db!!.version = this.mNewVersion!!
                version = db.version
            }

        } finally {
        }
        return super.getWritableDatabase()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val paths = ArrayList<String>()
        getUpgradeFilePaths(oldVersion, newVersion - 1, newVersion, paths)
        if (paths.isEmpty()) throw SQLiteAssetException("no upgrade script path from $oldVersion to $newVersion")

        Collections.sort(paths, VersionComparator())
        for (path in paths) {
            try {
                val `is` = mContext.assets.open(path)
                val sql = convertStreamToString(`is`)
                val cmds = sql.split(";".toRegex()).dropLastWhile { it -> it.isEmpty() }.toTypedArray()
                for (cmd in cmds) {
                    if (cmd.isNotEmpty()) {
                        db!!.execSQL(cmd)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
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

    private fun convertStreamToString(`is`: InputStream): String {
        return Scanner(`is`).useDelimiter("\\A").next()
    }

    inner class VersionComparator : Comparator<String> {
        private val pattern = Pattern
                .compile(".*_upgrade_([0-9]+)-([0-9]+).*")

        override fun compare(file0: String?, file1: String?): Int {
            val m0 = pattern.matcher(file0)
            val m1 = pattern.matcher(file1)
            if (!m0.matches()) {
                throw SQLiteAssetException("Invalid upgrade script file")
            }
            if (!m1.matches()) {
                throw SQLiteAssetException("Invalid upgrade script file")
            }
            val v0_from = Integer.valueOf(m0.group(1))
            val v1_from = Integer.valueOf(m1.group(1))
            val v0_to = Integer.valueOf(m0.group(2))
            val v1_to = Integer.valueOf(m1.group(2))
            if (v0_from === v1_from) {
                if (v0_to === v1_to) {
                    return 0
                }
                return if (v0_to < v1_to) -1 else 1
            }
            return if (v0_from < v1_from) -1 else 1
        }
    }
}