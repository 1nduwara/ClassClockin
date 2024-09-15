// In src/test/java/com/example/classclockin/dataModels/NotificationTest.kt

import com.example.classclockin.fragments.dataModels.Notification
import org.junit.Assert.*
import org.junit.Test

class NotificationTest {

    @Test
    fun testNotificationInitialization() {
        // Initialize the Notification object
        val notification = Notification(
            id = "N123",
            message = "Class will start at 9 AM.",
            timestamp = 1693910400000,  // Example timestamp
            marked = true
        )

        // Check that all properties are correctly set
        assertEquals("N123", notification.id)
        assertEquals("Class will start at 9 AM.", notification.message)
        assertEquals(1693910400000, notification.timestamp)
        assertTrue(notification.marked)
    }

    @Test
    fun testDefaultMarkedValue() {
        // Initialize the Notification object without specifying "marked"
        val notification = Notification(
            id = "N123",
            message = "Reminder to submit homework.",
            timestamp = 1693910400000
        )

        // Check that the default "marked" value is false
        assertFalse(notification.marked)
    }
}
