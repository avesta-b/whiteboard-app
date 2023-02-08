package cs346.whiteboard.service.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


// Dummy table class for testing
private object DummyTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128)
    val age = integer("age")

    override val primaryKey = PrimaryKey(id)
}

/// Testclass to validate if we can connect to database and perform some arbitrary operations such that they persist.
class DatabaseHelperTest {

    @BeforeEach
    fun setUp() {
        DatabaseHelper.init()

        transaction {
            // Drop the table
            if (DummyTable.exists()) {
                SchemaUtils.drop(DummyTable)
            }
        }

        // Create the table
        transaction {
            SchemaUtils.create(DummyTable)
        }


    }

    // Ensures we can perform a write to our DB.
    @Test
    fun checkWriteSuccess() {
        // Insert the value into the table
        var list: List<ResultRow> = emptyList()
        transaction {
            DummyTable.insert {
                it[DummyTable.name] = "Foo"
                it[DummyTable.age] = 50
            } get DummyTable.id

            list = DummyTable.selectAll().toList()
        }

        assert(list.size == 1)
        assert(list.first()[DummyTable.name] == "Foo")
        assert(list.first()[DummyTable.age] == 50)
    }

    @Test
    fun checkMultipleWriteAndFilterSuccess() {
    // Insert multiple values into the table
        var list: List<ResultRow> = emptyList()
        transaction {
            DummyTable.insert {
                it[DummyTable.name] = "Avesta"
                it[DummyTable.age] = 10
            } get DummyTable.id
            DummyTable.insert {
                it[DummyTable.name] = "Yorkie"
                it[DummyTable.age] = 5
            } get DummyTable.id
            DummyTable.insert {
                it[DummyTable.name] = "Edward"
                it[DummyTable.age] = 6
            } get DummyTable.id
            DummyTable.insert {
                it[DummyTable.name] = "Mikail"
                it[DummyTable.age] = 6
            } get DummyTable.id
            list = DummyTable.selectAll().toList()
        }

        assert(list.size == 4)
        val filteredList = list.filter { it[DummyTable.age] == 6 }
        assert(filteredList.size == 2)
    }


    @AfterEach
    fun tearDown() {
        transaction {
            // Drop the table
            SchemaUtils.drop(DummyTable)
        }
    }
}