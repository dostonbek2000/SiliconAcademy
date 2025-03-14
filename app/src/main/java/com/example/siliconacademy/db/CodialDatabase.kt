package com.example.siliconacademy.db

import Group
import Student
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.siliconacademy.interfaces.DatabaseService
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.Payment
import com.example.siliconacademy.models.Results
import com.example.siliconacademy.models.Teacher
import com.example.siliconacademy.utils.Content
import com.example.siliconacademy.utils.Content.COURSE_DESCRIPTION
import com.example.siliconacademy.utils.Content.COURSE_ID
import com.example.siliconacademy.utils.Content.COURSE_TABLE
import com.example.siliconacademy.utils.Content.COURSE_TITLE
import com.example.siliconacademy.utils.Content.DB_NAME
import com.example.siliconacademy.utils.Content.DB_VERSION
import com.example.siliconacademy.utils.Content.GROUP_COURSE_ID
import com.example.siliconacademy.utils.Content.GROUP_DAY
import com.example.siliconacademy.utils.Content.GROUP_ID
import com.example.siliconacademy.utils.Content.GROUP_POSITION
import com.example.siliconacademy.utils.Content.GROUP_TABLE
import com.example.siliconacademy.utils.Content.GROUP_TIME
import com.example.siliconacademy.utils.Content.GROUP_TITLE
import com.example.siliconacademy.utils.Content.PAYMENT_AMOUNT
import com.example.siliconacademy.utils.Content.PAYMENT_FULL_NAME
import com.example.siliconacademy.utils.Content.PAYMENT_ID
import com.example.siliconacademy.utils.Content.PAYMENT_MONTH
import com.example.siliconacademy.utils.Content.PAYMENT_TABLE
import com.example.siliconacademy.utils.Content.RESULT_AGE
import com.example.siliconacademy.utils.Content.RESULT_ID
import com.example.siliconacademy.utils.Content.RESULT_POSITION
import com.example.siliconacademy.utils.Content.RESULT_SUBJECT
import com.example.siliconacademy.utils.Content.RESULT_S_NAME
import com.example.siliconacademy.utils.Content.RESULT_TABLE
import com.example.siliconacademy.utils.Content.RESULT_TYPE
import com.example.siliconacademy.utils.Content.RESULT_T_NAME
import com.example.siliconacademy.utils.Content.STUDENT_FATHER_NAME
import com.example.siliconacademy.utils.Content.STUDENT_GROUP_ID
import com.example.siliconacademy.utils.Content.STUDENT_ID
import com.example.siliconacademy.utils.Content.STUDENT_NAME
import com.example.siliconacademy.utils.Content.STUDENT_PAYMENT_STATUS
import com.example.siliconacademy.utils.Content.STUDENT_SURNAME
import com.example.siliconacademy.utils.Content.STUDENT_TABLE
import com.example.siliconacademy.utils.Content.TEACHERS_DESCRIPTION
import com.example.siliconacademy.utils.Content.TEACHERS_ID
import com.example.siliconacademy.utils.Content.TEACHERS_NAME
import com.example.siliconacademy.utils.Content.TEACHERS_TABLE
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CodialDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),
    DatabaseService {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA foreign_keys = ON;")

        val teacherQuery = """
            CREATE TABLE $TEACHERS_TABLE (
                $TEACHERS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TEACHERS_NAME TEXT NOT NULL,
                $TEACHERS_DESCRIPTION TEXT NOT NULL
            );
        """.trimIndent()


        val groupQuery = """
            CREATE TABLE $GROUP_TABLE (
                $GROUP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $GROUP_POSITION INTEGER NOT NULL,
                $GROUP_TITLE TEXT NOT NULL,
                $GROUP_TIME TEXT NOT NULL,
                $GROUP_DAY TEXT NOT NULL,
                $GROUP_COURSE_ID INTEGER NOT NULL,
                 FOREIGN KEY ($GROUP_COURSE_ID) REFERENCES $TEACHERS_TABLE($TEACHERS_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        val studentQuery = """
            CREATE TABLE $STUDENT_TABLE (
                $STUDENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $STUDENT_NAME TEXT NOT NULL,
                $STUDENT_SURNAME TEXT NOT NULL,
                $STUDENT_FATHER_NAME TEXT NOT NULL,
                $STUDENT_GROUP_ID INTEGER NOT NULL,
                $STUDENT_PAYMENT_STATUS TEXT NOT NULL,
                FOREIGN KEY ($STUDENT_GROUP_ID) REFERENCES $GROUP_TABLE($GROUP_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        val paymentQuery = """
            CREATE TABLE $PAYMENT_TABLE (
                $PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            
                $PAYMENT_FULL_NAME TEXT NOT NULL,
                $PAYMENT_AMOUNT REAL NOT NULL,
                $PAYMENT_MONTH TEXT NOT NULL
                  );
        """.trimIndent()

        val resultQuery = """
            CREATE TABLE $RESULT_TABLE (
                $RESULT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $RESULT_POSITION INTEGER NOT NULL,
                $RESULT_S_NAME TEXT NOT NULL,
                $RESULT_AGE TEXT NOT NULL,
                $RESULT_TYPE TEXT NOT NULL,
                $RESULT_T_NAME TEXT NOT NULL,
                $RESULT_SUBJECT TEXT NOT NULL
                               
                
            );
            
        """.trimIndent()
        val courseQuery =
            """CREATE TABLE $COURSE_TABLE(
$COURSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COURSE_TITLE TEXT NOT NULL,
            $COURSE_DESCRIPTION TEXT NOT NULL 
            
            
);

""".trimIndent()


        db?.execSQL(teacherQuery)
        db?.execSQL(groupQuery)
        db?.execSQL(studentQuery)
        db?.execSQL(paymentQuery)
        db?.execSQL(resultQuery)
        db?.execSQL(courseQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 25) {
            db?.execSQL("DROP TABLE IF EXISTS $PAYMENT_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $STUDENT_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $RESULT_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $GROUP_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $TEACHERS_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $COURSE_TABLE")
            onCreate(db)
        }
    }


    override fun addPayment(payment: Payment) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(PAYMENT_FULL_NAME,payment.fullName)
            put(PAYMENT_AMOUNT, payment.amount)
            put(PAYMENT_MONTH, payment.month)
        }
        db.insert(PAYMENT_TABLE, null, contentValues)
        db.close()
    }
    fun getPaymentsByStudentId(studentId: Int): ArrayList<Payment> {
        val payments = ArrayList<Payment>()
        val database = this.readableDatabase
        val cursor = database.rawQuery("SELECT * FROM $PAYMENT_TABLE", null)

        // ✅ Move this OUTSIDE the loop!
        val allStudents = getAllStudentsList()

        if (cursor.moveToFirst()) {
            do {
                val payment = Payment(
                    cursor.getInt(cursor.getColumnIndexOrThrow(PAYMENT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_MONTH))
                )

                val matchedStudent = allStudents.find {
                    "${it.name} ${it.surname}".trim() == payment.fullName?.trim()
                }
                if (matchedStudent?.id == studentId) {
                    payments.add(payment)
                }

            } while (cursor.moveToNext())
        }
        cursor.close()
        return payments
    }


    override fun editPayment(payment: Payment): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PAYMENT_ID, payment.id)
        contentValues.put(PAYMENT_FULL_NAME, payment.fullName)
        contentValues.put(PAYMENT_AMOUNT, payment.amount)
        contentValues.put(PAYMENT_MONTH, payment.month)

        return database.update(
            PAYMENT_TABLE, contentValues, "$PAYMENT_ID= ?", arrayOf("${payment.id}")
        )
    }

    override fun deletePayment(payment: Payment) {
        val database = this.writableDatabase
        database.delete(PAYMENT_TABLE, "$PAYMENT_ID=?", arrayOf("${payment.id}"))
        database.close()
    }

    override fun getAllPaymentList(): ArrayList<Payment> {

        val paymentList = ArrayList<Payment>()
        val query = "SELECT * FROM $PAYMENT_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                paymentList.add(
                    Payment(
                        cursor.getInt(cursor.getColumnIndexOrThrow(PAYMENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_FULL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_MONTH))

                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return paymentList
    }

    override fun addTeacher(teacher: Teacher) {
        val database = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(TEACHERS_NAME, teacher.title)
            put(TEACHERS_DESCRIPTION, teacher.desc)
        }
        database.insert(TEACHERS_TABLE, null, contentValues)
        database.close()
    }

    override fun getAllTeachersList(): ArrayList<Teacher> {
        val teachersList = ArrayList<Teacher>()
        val query = "SELECT * FROM $TEACHERS_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                teachersList.add(
                    Teacher(
                        cursor.getInt(cursor.getColumnIndexOrThrow(TEACHERS_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TEACHERS_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TEACHERS_DESCRIPTION))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return teachersList
    }


    override fun getTeacherById(id: Int): Teacher {
        val database = this.readableDatabase
        val cursor: Cursor = database.query(
            TEACHERS_TABLE,
            arrayOf(TEACHERS_ID, TEACHERS_NAME, TEACHERS_DESCRIPTION),
            "$TEACHERS_ID = ?",
            arrayOf("$id"),
            null,
            null,
            null
        )
        cursor.moveToFirst()
        return Teacher(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
    }

    override fun deleteTeacher(teacher: Teacher) {
        val database = this.writableDatabase
        database.delete(TEACHERS_TABLE, "$TEACHERS_ID=?", arrayOf("${teacher.id}"))
        database.close()
    }

    override fun editTeacher(teacher: Teacher): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TEACHERS_ID, teacher.id)
        contentValues.put(TEACHERS_NAME, teacher.title)
        contentValues.put(TEACHERS_DESCRIPTION, teacher.desc)

        return database.update(
            TEACHERS_TABLE, contentValues, "$TEACHERS_ID= ?", arrayOf("${teacher.id}")
        )
    }

    override fun addCourse(course: Course) {
        val database = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COURSE_TITLE, course.title)
            put(COURSE_DESCRIPTION, course.description)
        }
        database.insert(COURSE_TABLE, null, contentValues)
        database.close()
    }

    override fun getAllCourseList(): ArrayList<Course> {
        val coursesList = ArrayList<Course>()
        val query = "SELECT * FROM $COURSE_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                coursesList.add(
                    Course(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return coursesList
    }

    override fun deleteCourse(course: Course) {
        val database = this.writableDatabase
        database.delete(COURSE_TABLE, "$TEACHERS_ID=?", arrayOf("${course.id}"))
        database.close()
    }

    override fun editCourse(course: Course): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COURSE_ID, course.id)
        contentValues.put(COURSE_TITLE, course.title)
        contentValues.put(COURSE_DESCRIPTION, course.description)

        return database.update(
            COURSE_TABLE, contentValues, "$COURSE_ID= ?", arrayOf("${course.id}")
        )
    }


    override fun addGroup(group: Group) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(GROUP_POSITION, group.groupPosition)
        contentValues.put(GROUP_TITLE, group.groupTitle)
        contentValues.put(GROUP_TIME, group.groupTime)
        contentValues.put(GROUP_DAY, group.groupDay)
        contentValues.put(GROUP_COURSE_ID, group.courseId?.id)
        database.insert(GROUP_TABLE, null, contentValues)
        database.close()
    }

    override fun getAllGroupsList(): ArrayList<Group> {
        val groupsList = ArrayList<Group>()
        val query: String = "select * from $GROUP_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val group = Group(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    getTeacherById(cursor.getInt(5))
                )
                groupsList.add(group)
            } while (cursor.moveToNext())
        }
        //


        return groupsList
    }


    // +

    override fun editGroup(group: Group): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(GROUP_ID, group.id)
        contentValues.put(GROUP_POSITION, group.groupPosition)
        contentValues.put(GROUP_TITLE, group.groupTitle)
        contentValues.put(GROUP_TIME, group.groupTime)
        contentValues.put(GROUP_DAY, group.groupDay)
        contentValues.put(GROUP_COURSE_ID, group.courseId?.id)
        return database.update(GROUP_TABLE, contentValues, "$GROUP_ID = ?", arrayOf("${group.id}"))
    }


    override fun addStudent(student: Student) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(STUDENT_NAME, student.name)
        contentValues.put(STUDENT_SURNAME, student.surname)
        contentValues.put(STUDENT_FATHER_NAME, student.fatherName)
        contentValues.put(STUDENT_GROUP_ID, student.groupId?.id)
        contentValues.put(STUDENT_PAYMENT_STATUS,student.paymentStatus)
        database.insert(STUDENT_TABLE, null, contentValues)
        database.close()
    }

    override fun addResult(result: Results) {
        val database = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(RESULT_POSITION, result.resultPosition)
            put(RESULT_S_NAME, result.name)
            put(RESULT_AGE, result.age)
            put(RESULT_TYPE, result.testType)
            put(RESULT_T_NAME, result.teacherName)
            put(RESULT_SUBJECT, result.subject)
            put(RESULT_ID, result.id)

        }
        database.insert(RESULT_TABLE, null, contentValues)
        database.close()

    }

    override fun getAllResultsList(): ArrayList<Results> {
        val resultsList = ArrayList<Results>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $RESULT_TABLE", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(RESULT_ID))
                val resultPosition = cursor.getInt(cursor.getColumnIndexOrThrow(RESULT_POSITION))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(RESULT_S_NAME))
                val age = cursor.getString(cursor.getColumnIndexOrThrow(RESULT_AGE))
                val testType = cursor.getString(cursor.getColumnIndexOrThrow(RESULT_TYPE))
                val teacherName = cursor.getString(cursor.getColumnIndexOrThrow(RESULT_T_NAME))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow(RESULT_SUBJECT))

                val result = Results(
                    id = id,
                    resultPosition = resultPosition,
                    name = name,
                    age = age,
                    testType = testType,
                    teacherName = teacherName,
                    subject = subject,

                    )

                resultsList.add(result)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return resultsList
    }


    override fun editResult(results: Results): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(RESULT_ID, results.id)
        contentValues.put(RESULT_POSITION, results.resultPosition)
        contentValues.put(RESULT_S_NAME, results.name)
        contentValues.put(RESULT_AGE, results.age)
        contentValues.put(RESULT_TYPE, results.testType)
        contentValues.put(RESULT_T_NAME, results.teacherName)
        contentValues.put(RESULT_SUBJECT, results.subject)
        return database.update(
            RESULT_TABLE, contentValues, "$RESULT_ID=?", arrayOf("${results.id}")
        )


    }

    override fun deleteResult(results: Results) {
        val database = this.writableDatabase
        database.delete(RESULT_TABLE, "$RESULT_ID=?", arrayOf("${results.id}"))
        database.close()


    }


    override fun editStudent(student: Student): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(STUDENT_ID, student.id)
        contentValues.put(STUDENT_NAME, student.name)
        contentValues.put(STUDENT_SURNAME, student.surname)
        contentValues.put(STUDENT_FATHER_NAME, student.fatherName)
        contentValues.put(STUDENT_GROUP_ID, student.groupId?.id)
        return database.update(
            STUDENT_TABLE, contentValues, "$STUDENT_ID = ?", arrayOf("${student.id}")
        )
    }

    override fun deleteStudent(student: Student) {
        //  getStudentById(student)
        val database = this.writableDatabase
        database.delete(STUDENT_TABLE, "$STUDENT_ID= ?", arrayOf("${student.id}"))
        database.close()
    }

    override fun getStudentById(student: Student) {
        val database = this.writableDatabase
        val query: String = "select * from $STUDENT_TABLE where $STUDENT_GROUP_ID = ${student.id}"
        val cursor: Cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val studentA = Student(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    getGroupById(cursor.getInt(4)),
                    cursor.getString(5)
                )
                deleteStudent(studentA)
            } while (cursor.moveToNext())
        }
    }
    // ✅ Utility Method to Update Student Payment Status — Exact match between payment month and current month
    fun updateStudentPaymentStatus(student: Student) {
        val payments = getPaymentsByStudentIdByFullName(student)
        val currentMonth = getCurrentMonthInUzbek()

        // ✅ Check if any payment's month exactly matches current month
        val isPaidForCurrentMonth = payments.any { payment ->
            payment.month.trim().equals(currentMonth.trim(), ignoreCase = true)
        }

        student.paymentStatus = if (isPaidForCurrentMonth) "Paid" else "Unpaid"
        editStudent(student)
    }

    // ✅ Modified getAllStudentsList() using updated logic
    override fun getAllStudentsList(): ArrayList<Student> {
        val studentsList: ArrayList<Student> = ArrayList()
        val query: String = "SELECT * FROM $STUDENT_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    getGroupById(cursor.getInt(4)),
                    cursor.getString(5)
                )
                val payments = getPaymentsByStudentIdByFullName(student)
                val currentMonth = getCurrentMonthInUzbek()
                val isPaid = payments.any { it.month.trim().equals(currentMonth.trim(), ignoreCase = true) }
                student.paymentStatus = if (isPaid) "Paid" else "Unpaid"
                studentsList.add(student)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return studentsList
    }

    // ✅ Get payments for a student by matching full name
    fun getPaymentsByStudentIdByFullName(student: Student): ArrayList<Payment> {
        val payments = ArrayList<Payment>()
        val database = this.readableDatabase
        val cursor = database.rawQuery("SELECT * FROM $PAYMENT_TABLE", null)
        val fullName = "${student.name ?: ""} ${student.surname ?: ""}".trim()

        if (cursor.moveToFirst()) {
            do {
                val payment = Payment(
                    cursor.getInt(cursor.getColumnIndexOrThrow(PAYMENT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_FULL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_AMOUNT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PAYMENT_MONTH))
                )
                if (payment.fullName?.trim() == fullName) {
                    payments.add(payment)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return payments
    }

    // ✅ Helper: Get current month in Uzbek language
    private fun getCurrentMonthInUzbek(): String {
        val months = listOf(
            "Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun",
            "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr"
        )
        val calendar = Calendar.getInstance()
        return months[calendar.get(Calendar.MONTH)]
    }
    override fun deleteGroup(group: Group) {
        getStudentByGroupId(group)
        val database = this.writableDatabase
        database.delete(GROUP_TABLE, "$GROUP_ID = ?", arrayOf("${group.id}"))
        database.close()
    }

    override fun getGroupById(id: Int): Group {
        val database = this.readableDatabase
        val cursor: Cursor = database.query(
            GROUP_TABLE, arrayOf(
                GROUP_ID, GROUP_POSITION, GROUP_TITLE, GROUP_TIME, GROUP_DAY, GROUP_COURSE_ID
            ), "$GROUP_ID = ?", arrayOf("$id"), null, null, null
        )
        cursor.moveToFirst()
        return Group(
            cursor.getInt(0),
            cursor.getInt(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4),
            getTeacherById(cursor.getInt(5))
        )
    }


    override fun getStudentByGroupId(group: Group) {
        val database = this.writableDatabase
        val query: String = "select * from $STUDENT_TABLE where $STUDENT_GROUP_ID = ${group.id}"
        val cursor: Cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    getGroupById(cursor.getInt(4)),
                    cursor.getString(5)
                )
                deleteStudent(student)
            } while (cursor.moveToNext())
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CodialDatabase? = null

        fun getInstance(context: Context): CodialDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CodialDatabase(context).also { INSTANCE = it }
            }
        }
    }
}