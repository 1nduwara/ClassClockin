// In src/test/java/com/example/classclockin/dataModels/AttendanceSummaryTest.kt

import com.example.classclockin.fragments.dataModels.AttendanceSummary
import org.junit.Assert.*
import org.junit.Test

class AttendanceSummaryTest {

    @Test
    fun testAttendanceSummaryInitialization() {
        // Initialize the AttendanceSummary object
        val summary = AttendanceSummary(
            date = "2024-09-05",
            totalPresent = "30",
            totalAbsent = "5"
        )

        // Check that all properties are correctly set
        assertEquals("2024-09-05", summary.date)
        assertEquals("30", summary.totalPresent)
        assertEquals("5", summary.totalAbsent)
    }

    @Test
    fun testDefaultValues() {
        // Initialize the AttendanceSummary object with default values
        val summary = AttendanceSummary()

        // Check that the default values are correctly set
        assertEquals("", summary.date)
        assertEquals("0", summary.totalPresent)
        assertEquals("0", summary.totalAbsent)
    }
}
