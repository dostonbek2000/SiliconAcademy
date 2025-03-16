package com.example.siliconacademy.db

import Group
import Student
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.PathUtils
import com.example.siliconacademy.interfaces.DatabaseService
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.Payment
import com.example.siliconacademy.models.Results
import com.example.siliconacademy.models.Teacher
import com.example.siliconacademy.utils.Content.COURSE_DESCRIPTION
import com.example.siliconacademy.utils.Content.COURSE_ID
import com.example.siliconacademy.utils.Content.COURSE_TABLE
import com.example.siliconacademy.utils.Content.COURSE_TITLE
import com.example.siliconacademy.utils.Content.DB_NAME
import com.example.siliconacademy.utils.Content.DB_VERSION
import com.example.siliconacademy.utils.Content.GROUP_COURSE_ID
import com.example.siliconacademy.utils.Content.GROUP_DAY
import com.example.siliconacademy.utils.Content.GROUP_FEE
import com.example.siliconacademy.utils.Content.GROUP_ID
import com.example.siliconacademy.utils.Content.GROUP_POSITION
import com.example.siliconacademy.utils.Content.GROUP_SUBJECT
import com.example.siliconacademy.utils.Content.GROUP_TABLE
import com.example.siliconacademy.utils.Content.GROUP_TIME
import com.example.siliconacademy.utils.Content.GROUP_TITLE
import com.example.siliconacademy.utils.Content.PAYMENT_AMOUNT
import com.example.siliconacademy.utils.Content.PAYMENT_DATE
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
import com.example.siliconacademy.utils.Content.STUDENT_ACCOUNT_BALANCE
import com.example.siliconacademy.utils.Content.STUDENT_AGE
import com.example.siliconacademy.utils.Content.STUDENT_DATE
import com.example.siliconacademy.utils.Content.STUDENT_FATHER_NAME
import com.example.siliconacademy.utils.Content.STUDENT_GROUP_ID
import com.example.siliconacademy.utils.Content.STUDENT_ID
import com.example.siliconacademy.utils.Content.STUDENT_NAME
import com.example.siliconacademy.utils.Content.STUDENT_SURNAME
import com.example.siliconacademy.utils.Content.STUDENT_TABLE
import com.example.siliconacademy.utils.Content.TEACHERS_DESCRIPTION
import com.example.siliconacademy.utils.Content.TEACHERS_ID
import com.example.siliconacademy.utils.Content.TEACHERS_NAME
import com.example.siliconacademy.utils.Content.TEACHERS_TABLE
import com.example.siliconacademy.utils.Content.TEACHER_AGE
import com.example.siliconacademy.utils.Content.TEACHER_SUBJECT
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CodialDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),
    DatabaseService {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA foreign_keys = ON;")

        val teacherQuery = """
            CREATE TABLE $TEACHERS_TABLE (
                $TEACHERS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TEACHERS_NAME TEXT NOT NULL,
                $TEACHERS_DESCRIPTION TEXT NOT NULL,
                $TEACHER_AGE TEXT NOT NULL,
                $TEACHER_SUBJECT TEXT NOT NULL
            );
        """.trimIndent()


        val groupQuery = """
            CREATE TABLE $GROUP_TABLE (
                $GROUP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $GROUP_POSITION INTEGER NOT NULL,
                $GROUP_TITLE TEXT NOT NULL,
                $GROUP_SUBJECT TEXT NOT NULL,
                $GROUP_TIME TEXT NOT NULL,
                $GROUP_DAY TEXT NOT NULL,
                $GROUP_COURSE_ID INTEGER NOT NULL,
                $GROUP_FEE TEXT NOT NULL,
                 FOREIGN KEY ($GROUP_COURSE_ID) REFERENCES $TEACHERS_TABLE($TEACHERS_ID) ON DELETE CASCADE
            );
        """.trimIndent()
        val studentQuery = """
            CREATE TABLE $STUDENT_TABLE (
                $STUDENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $STUDENT_NAME TEXT NOT NULL,
                $STUDENT_SURNAME TEXT NOT NULL,
                
                $STUDENT_FATHER_NAME TEXT NOT NULL,
                $STUDENT_AGE TEXT NOT NULL,
                
                $STUDENT_GROUP_ID INTEGER NOT NULL,
                $STUDENT_ACCOUNT_BALANCE REAL NOT NULL,
                $STUDENT_DATE TEXT NOT NULL,
                removed_status TEXT DEFAULT 'not removed',
                
                FOREIGN KEY ($STUDENT_GROUP_ID) REFERENCES $GROUP_TABLE($GROUP_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        val paymentQuery = """
    CREATE TABLE $PAYMENT_TABLE (
        $PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $PAYMENT_FULL_NAME TEXT NOT NULL,
        $PAYMENT_AMOUNT REAL NOT NULL,
        $PAYMENT_MONTH TEXT NOT NULL,
        payment_date TEXT NOT NULL      
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
        if (oldVersion < 60) {
            db?.execSQL("DROP TABLE IF EXISTS $PAYMENT_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $STUDENT_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $RESULT_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $GROUP_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $TEACHERS_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $COURSE_TABLE")
            onCreate(db)
        }
    }
    fun deductMonthlyFeeForEachStudent(context: Context) {
        val db = writableDatabase
        val students = getAllStudentsList()
        val cursor = db.rawQuery("SELECT $STUDENT_ID, removed_status FROM $STUDENT_TABLE", null)

        val deductionAmount = 200000.0
        if (cursor.moveToFirst()) {
            do {
                val studentId = cursor.getInt(0)
                val removedStatus = cursor.getString(1) ?: "not removed"

                if (removedStatus == "not removed") {
                    val student = students.find { it.id == studentId }
                    student?.let {
                        it.accountBalance = (it.accountBalance ?: 0.0) - deductionAmount
                        val cv = ContentValues().apply {
                            put(STUDENT_ACCOUNT_BALANCE, it.accountBalance)
                            put("removed_status", "removed")
                        }
                        db.update(STUDENT_TABLE, cv, "$STUDENT_ID=?", arrayOf("$studentId"))

                        Log.d("CODIAL_DB", "Deducted 200000 from ${it.name} ${it.surname}, new balance: ${it.accountBalance}")
                    }
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    fun resetAllStudentsRemovedStatus(context: Context) {
        val calendar = Calendar.getInstance()
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
        if (todayDay == 1) {
            val db = writableDatabase
            val cv = ContentValues().apply { put("removed_status", "not removed") }
            db.update(STUDENT_TABLE, cv, null, null)
            Log.d("CODIAL_DB", "Reset removed_status for all students")
        }
    }



    override fun addPayment(payment: Payment) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(PAYMENT_FULL_NAME, payment.fullName)
            put(PAYMENT_AMOUNT, payment.amount)
            put(PAYMENT_MONTH, payment.month)
            put(PAYMENT_DATE, payment.date)
        }
        db.insert(PAYMENT_TABLE, null, cv)

        val student = getAllStudentsList().find {
            "${it.name} ${it.surname}".trim() == payment.fullName?.trim()
        }
        student?.let {
            it.accountBalance = it.accountBalance?.plus(payment.amount.toDoubleOrNull() ?: 0.0)
            editStudent(it)
        }
        db.close()
    }

    override fun editPayment(payment: Payment): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(PAYMENT_FULL_NAME, payment.fullName)
            put(PAYMENT_AMOUNT, payment.amount)
            put(PAYMENT_MONTH, payment.month)
            put(PAYMENT_DATE, payment.date)
        }
        return db.update(PAYMENT_TABLE, cv, "$PAYMENT_ID=?", arrayOf("${payment.id}"))
    }

    override fun deletePayment(payment: Payment) {
        writableDatabase.delete(PAYMENT_TABLE, "$PAYMENT_ID=?", arrayOf("${payment.id}"))
    }

    override fun getAllPaymentList(): ArrayList<Payment> {
        val list = ArrayList<Payment>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM $PAYMENT_TABLE", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Payment(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
    /*  fun getPaymentsByStudentId(studentId: Int): ArrayList<Payment> {
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
       }*/

    override fun addTeacher(teacher: Teacher) {
        val database = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(TEACHERS_NAME, teacher.title)
            put(TEACHERS_DESCRIPTION, teacher.desc)
            put(TEACHER_AGE,teacher.age)
            put(TEACHER_SUBJECT,teacher.subject)
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
                        cursor.getString(cursor.getColumnIndexOrThrow(TEACHERS_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TEACHER_AGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TEACHER_SUBJECT))

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
            arrayOf(TEACHERS_ID, TEACHERS_NAME, TEACHERS_DESCRIPTION, TEACHER_AGE, TEACHER_SUBJECT),
            "$TEACHERS_ID = ?",
            arrayOf("$id"),
            null,
            null,
            null
        )
        cursor.moveToFirst()
        return Teacher(cursor.getInt(0), cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4))
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
        contentValues.put(TEACHER_AGE,teacher.age)
        contentValues.put(TEACHER_SUBJECT,teacher.subject)

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
        contentValues.put(GROUP_SUBJECT,group.groupSubject)
        contentValues.put(GROUP_TIME, group.groupTime)
        contentValues.put(GROUP_DAY, group.groupDay)
        contentValues.put(GROUP_COURSE_ID, group.courseId?.id)
        contentValues.put(GROUP_FEE,group.fee)
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
                    cursor.getString(5),
                    getTeacherById(cursor.getInt(6)),
                    cursor.getString(7),
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
        contentValues.put(GROUP_SUBJECT,group.groupSubject)
        contentValues.put(GROUP_TIME, group.groupTime)
        contentValues.put(GROUP_DAY, group.groupDay)
        contentValues.put(GROUP_COURSE_ID, group.courseId?.id)
        contentValues.put(GROUP_FEE,group.fee)
        return database.update(GROUP_TABLE, contentValues, "$GROUP_ID = ?", arrayOf("${group.id}"))
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


    override fun addStudent(student: Student) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(STUDENT_NAME, student.name)
            put(STUDENT_SURNAME, student.surname)
            put(STUDENT_AGE,student.age)
            put(STUDENT_FATHER_NAME, student.fatherName)
            put(STUDENT_GROUP_ID, student.groupId?.id)
            put(STUDENT_DATE,student.date)
            put(STUDENT_ACCOUNT_BALANCE, student.accountBalance)

        }
        db.insert(STUDENT_TABLE, null, cv)
        db.close()
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
                    cursor.getString(4),
                    getGroupById(cursor.getInt(5)),
                    cursor.getString(6),
                    cursor.getDouble(7)


                )
                deleteStudent(studentA)
            } while (cursor.moveToNext())
        }
    }
    // ✅ Utility Method to Update Student Payment Status — Exact match between payment month and current month


    // ✅ Modified getAllStudentsList() using updated logic
    override fun editStudent(student: Student): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(STUDENT_NAME, student.name)
            put(STUDENT_SURNAME, student.surname)
            put(STUDENT_AGE,student.age)
            put(STUDENT_FATHER_NAME, student.fatherName)
            put(STUDENT_GROUP_ID, student.groupId?.id)
            put(STUDENT_DATE,student.date)
            put(STUDENT_ACCOUNT_BALANCE, student.accountBalance) // ✅ ADD THIS

        }
        return db.update(STUDENT_TABLE, cv, "$STUDENT_ID=?", arrayOf("${student.id}"))
    }


    override fun getAllStudentsList(): ArrayList<Student> {
        val list = ArrayList<Student>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $STUDENT_TABLE", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Student(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        getGroupById(cursor.getInt(5)),
                        cursor.getString(7),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(STUDENT_ACCOUNT_BALANCE)),

                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }


    override fun deleteStudent(student: Student) {
        //  getStudentById(student)
        val database = this.writableDatabase
        database.delete(STUDENT_TABLE, "$STUDENT_ID= ?", arrayOf("${student.id}"))
        database.close()
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
                GROUP_ID, GROUP_POSITION, GROUP_TITLE, GROUP_SUBJECT, GROUP_TIME, GROUP_DAY, GROUP_COURSE_ID,
                GROUP_FEE
            ), "$GROUP_ID = ?", arrayOf("$id"), null, null, null
        )
        cursor.moveToFirst()
        return Group(
            cursor.getInt(0),
            cursor.getInt(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4),
            cursor.getString(5),
            getTeacherById(cursor.getInt(6)),
                cursor.getString(7)
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
                    cursor.getString(4),
                    getGroupById(cursor.getInt(5)),

                    cursor.getString(7),
                    cursor.getDouble(6)
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