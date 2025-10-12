package app.wojablo.alk

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.db.QueryResult

actual class DatabaseDriverFactory {
    actual suspend fun createDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
        name: String
    ): SqlDriver? {
        val driver = InMemorySqlDriver()
        schema.create(driver)
        return driver
    }
}


