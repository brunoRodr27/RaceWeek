package com.example.raceweek.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.raceweek.data.local.dao.CategoryDao
import com.example.raceweek.data.local.dao.SettingsDao
import com.example.raceweek.data.local.entity.CategoryEntity
import com.example.raceweek.data.local.entity.SettingsEntity

@Database(
    entities = [CategoryEntity::class, SettingsEntity::class],
    version = 4,
    exportSchema = false
)
abstract class RaceWeekDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Categories ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS settings (
                        id INTEGER NOT NULL PRIMARY KEY,
                        notifications TEXT NOT NULL DEFAULT 'T',
                        time TEXT NOT NULL DEFAULT 'B',
                        practices TEXT NOT NULL DEFAULT 'T',
                        qualifyings TEXT NOT NULL DEFAULT 'T',
                        races TEXT NOT NULL DEFAULT 'T'
                    )
                    """.trimIndent()
                )
                db.execSQL("INSERT OR IGNORE INTO settings (id) VALUES (1)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Categories ADD COLUMN [order] INTEGER NOT NULL DEFAULT 0")
                // Atribui order sequencial baseado na ordem de inserção (id ASC).
                val cursor = db.query("SELECT id FROM Categories ORDER BY id ASC")
                var idx = 0
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(0)
                    db.execSQL("UPDATE Categories SET [order] = $idx WHERE id = $id")
                    idx++
                }
                cursor.close()
            }
        }
    }
}
