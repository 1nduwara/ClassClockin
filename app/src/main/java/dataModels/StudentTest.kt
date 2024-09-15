// In src/test/java/com/example/classclockin/dataModels/StudentTest.kt

import com.example.classclockin.fragments.dataModels.Student
import org.junit.Assert.*
import org.junit.Test

class StudentTest {

    @Test
    fun testStudentInitialization() {
        // Initialize the student object
        val student = Student(
            studentId = "S123",
            studentName = "John Doe",
            studentPhoto = "url/to/photo.jpg",
            studentAttendance = 75.5f
        )

        // Check that all properties are correctly set
        assertEquals("S123", student.studentId)
        assertEquals("John Doe", student.studentName)
        assertEquals("url/to/photo.jpg", student.studentPhoto)
        assertEquals(75.5f, student.studentAttendance, 0.01f)
    }

    @Test
    fun testUpdateStudentAttendance() {
        // Initialize the student object
        val student = Student(
            studentId = "S123",
            studentName = "John Doe",
            studentPhoto = "url/to/photo.jpg",
            studentAttendance = 75.5f
        )

        // Update student attendance and verify the change
        student.studentAttendance = 90.0f
        assertEquals(90.0f, student.studentAttendance, 0.01f)
    }
}
