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
    version = 5,
    exportSchema = true
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

        // Converte colunas TEXT "T"/"F" para INTEGER 0/1 em ambas as tabelas,
        // e renomeia Categories.status → Categories.active.
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE Categories_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        active INTEGER NOT NULL DEFAULT 1,
                        description TEXT NOT NULL DEFAULT '',
                        [order] INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO Categories_new (id, name, active, description, [order])
                    SELECT id, name,
                        CASE WHEN status = 'T' THEN 1 ELSE 0 END,
                        description, [order]
                    FROM Categories
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE Categories")
                db.execSQL("ALTER TABLE Categories_new RENAME TO Categories")

                db.execSQL(
                    """
                    CREATE TABLE settings_new (
                        id INTEGER NOT NULL PRIMARY KEY,
                        notifications INTEGER NOT NULL DEFAULT 1,
                        time TEXT NOT NULL DEFAULT 'B',
                        practices INTEGER NOT NULL DEFAULT 1,
                        qualifyings INTEGER NOT NULL DEFAULT 1,
                        races INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO settings_new (id, notifications, time, practices, qualifyings, races)
                    SELECT id,
                        CASE WHEN notifications = 'T' THEN 1 ELSE 0 END,
                        time,
                        CASE WHEN practices = 'T' THEN 1 ELSE 0 END,
                        CASE WHEN qualifyings = 'T' THEN 1 ELSE 0 END,
                        CASE WHEN races = 'T' THEN 1 ELSE 0 END
                    FROM settings
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE settings")
                db.execSQL("ALTER TABLE settings_new RENAME TO settings")
            }
        }
    }
}
