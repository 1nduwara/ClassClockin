// In src/test/java/com/example/classclockin/dataModels/UserTest.kt

import com.example.classclockin.fragments.dataModels.User
import org.junit.Assert.*
import org.junit.Test

class UserTest {

    @Test
    fun testUserInitialization() {
        // Initialize the User object
        val user = User(
            firstName = "Alice",
            lastName = "Smith",
            teacherId = "T001",
            phoneNumber = "123456789",
            emailAddress = "alice@example.com",
            birthday = "1990-01-01"
        )

        // Check that all properties are correctly set
        assertEquals("Alice", user.firstName)
        assertEquals("Smith", user.lastName)
        assertEquals("T001", user.teacherId)
        assertEquals("123456789", user.phoneNumber)
        assertEquals("alice@example.com", user.emailAddress)
        assertEquals("1990-01-01", user.birthday)
    }

    @Test
    fun testDefaultUserValues() {
        // Initialize the User object with default values
        val user = User()

        // Check that the default values are correctly set
        assertEquals("", user.firstName)
        assertEquals("", user.lastName)
        assertEquals("", user.teacherId)
        assertEquals("", user.phoneNumber)
        assertEquals("", user.emailAddress)
        assertEquals("", user.birthday)
    }
}
