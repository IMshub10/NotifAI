package com.summer.core.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.summer.core.data.local.dao.SMSDao
import com.summer.core.data.local.entities.SMSEntity

@Database(entities = [SMSEntity::class], version = 1)
abstract class SMSDatabase : RoomDatabase() {

    abstract fun smsDao(): SMSDao

    companion object {
        private const val DB_NAME = "sms_database"

        @Volatile
        private var INSTANCE: SMSDatabase? = null

        fun getDatabase(context: Context): SMSDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SMSDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}