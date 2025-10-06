package app.wojablo.alk

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.async.coroutines.synchronous
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
        name: String
    ): SqlDriver? = withContext(Dispatchers.IO) {
        val dbFile = File(System.getProperty("user.home"))
        .resolve(".config/alk")
        .apply { mkdirs() }
        .resolve("database.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        if (!dbFile.exists()) {
            schema.synchronous().create(driver)
        }
        driver
    }
}
