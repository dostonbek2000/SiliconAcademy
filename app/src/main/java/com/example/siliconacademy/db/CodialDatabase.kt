package com.example.siliconacademy.db

import Group
import Student
import Teacher
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.siliconacademy.interfaces.DatabaseService
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.Payment
import com.example.siliconacademy.models.Results
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
import com.example.siliconacademy.utils.Content.GROUP_TEACHER_ID
import com.example.siliconacademy.utils.Content.GROUP_TEACHER_NAME
import com.example.siliconacademy.utils.Content.GROUP_TIME
import com.example.siliconacademy.utils.Content.GROUP_TITLE
import com.example.siliconacademy.utils.Content.PAYMENT_ID
import com.example.siliconacademy.utils.Content.PAYMENT_TABLE
import com.example.siliconacademy.utils.Content.RESULT_DESCC
import com.example.siliconacademy.utils.Content.RESULT_ID
import com.example.siliconacademy.utils.Content.RESULT_IMAGE
import com.example.siliconacademy.utils.Content.RESULT_SUBJECT
import com.example.siliconacademy.utils.Content.RESULT_S_NAME
import com.example.siliconacademy.utils.Content.RESULT_TABLE
import com.example.siliconacademy.utils.Content.RESULT_T_NAME
import com.example.siliconacademy.utils.Content.STUDENT_FATHER_NAME
import com.example.siliconacademy.utils.Content.STUDENT_GROUP_ID
import com.example.siliconacademy.utils.Content.STUDENT_ID
import com.example.siliconacademy.utils.Content.STUDENT_NAME
import com.example.siliconacademy.utils.Content.STUDENT_SURNAME
import com.example.siliconacademy.utils.Content.STUDENT_TABLE
import com.example.siliconacademy.utils.Content.TEACHERS_COURSE_ID
import com.example.siliconacademy.utils.Content.TEACHERS_FATHER
import com.example.siliconacademy.utils.Content.TEACHERS_ID
import com.example.siliconacademy.utils.Content.TEACHERS_NAME
import com.example.siliconacademy.utils.Content.TEACHERS_SURNAME
import com.example.siliconacademy.utils.Content.TEACHERS_TABLE
class CodialDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), DatabaseService {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("PRAGMA foreign_keys = ON;")

        val courseQuery = """
            CREATE TABLE $COURSE_TABLE (
                $COURSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COURSE_TITLE TEXT NOT NULL,
                $COURSE_DESCRIPTION TEXT NOT NULL
            );
        """.trimIndent()

        val teacherQuery = """
            CREATE TABLE $TEACHERS_TABLE (
                $TEACHERS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $TEACHERS_NAME TEXT NOT NULL,
                $TEACHERS_SURNAME TEXT NOT NULL,
                $TEACHERS_FATHER TEXT NOT NULL,
                $TEACHERS_COURSE_ID INTEGER NOT NULL,
                FOREIGN KEY ($TEACHERS_COURSE_ID) REFERENCES $COURSE_TABLE($COURSE_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        val groupQuery = """
            CREATE TABLE $GROUP_TABLE (
                $GROUP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $GROUP_POSITION INTEGER NOT NULL,
                $GROUP_TITLE TEXT NOT NULL,
                $GROUP_TIME TEXT NOT NULL,
                $GROUP_DAY TEXT NOT NULL,
                $GROUP_TEACHER_ID INTEGER NOT NULL,
                $GROUP_COURSE_ID INTEGER NOT NULL,
                FOREIGN KEY ($GROUP_TEACHER_ID) REFERENCES $TEACHERS_TABLE($TEACHERS_ID) ON DELETE CASCADE,
                FOREIGN KEY ($GROUP_COURSE_ID) REFERENCES $COURSE_TABLE($COURSE_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        val studentQuery = """
            CREATE TABLE $STUDENT_TABLE (
                $STUDENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $STUDENT_NAME TEXT NOT NULL,
                $STUDENT_SURNAME TEXT NOT NULL,
                $STUDENT_FATHER_NAME TEXT NOT NULL,
                $STUDENT_GROUP_ID INTEGER NOT NULL,
                FOREIGN KEY ($STUDENT_GROUP_ID) REFERENCES $GROUP_TABLE($GROUP_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        val paymentQuery = """
            CREATE TABLE $PAYMENT_TABLE (
                $PAYMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $STUDENT_ID INTEGER NOT NULL,
                ${Content.PAYMENT_AMOUNT} REAL NOT NULL,
                ${Content.PAYMENT_MONTH} TEXT NOT NULL,
                FOREIGN KEY ($STUDENT_ID) REFERENCES $STUDENT_TABLE($STUDENT_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        val resultQuery = """
            CREATE TABLE $RESULT_TABLE ("
                $RESULT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $RESULT_S_NAME TEXT NOT NULL,
                $RESULT_T_NAME TEXT NOT NULL,
                $RESULT_SUBJECT TEXT NOT NULL,
                $RESULT_DESCC TEXT NOT NULL,
                $RESULT_IMAGE TEXT NOT NULL
            ");
        """.trimIndent()

        db?.execSQL(courseQuery)
        db?.execSQL(teacherQuery)
        db?.execSQL(groupQuery)
        db?.execSQL(studentQuery)
        db?.execSQL(paymentQuery)
        db?.execSQL(resultQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db?.execSQL("DROP TABLE IF EXISTS $PAYMENT_TABLE")
            db?.execSQL("DROP TABLE IF EXISTS $STUDENT_TABLE")
            onCreate(db)
        }
    }

    fun getPaymentsByStudentId(studentId: Int): List<Payment> {
        val db = this.readableDatabase
        val list = mutableListOf<Payment>()
        val cursor = db.rawQuery(
            "SELECT * FROM $PAYMENT_TABLE WHERE $STUDENT_ID = ?", arrayOf(studentId.toString())
        )

        while (cursor.moveToNext()) {
            list.add(
                Payment(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(PAYMENT_ID)),
                    studentId = cursor.getInt(cursor.getColumnIndexOrThrow(STUDENT_ID)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(Content.PAYMENT_AMOUNT)).toString(),
                    month = cursor.getString(cursor.getColumnIndexOrThrow(Content.PAYMENT_MONTH))
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }

    fun addPayment(payment: Payment) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(STUDENT_ID, payment.studentId)
            put(Content.PAYMENT_AMOUNT, payment.amount)
            put(Content.PAYMENT_MONTH, payment.month)
        }
        db.insert(PAYMENT_TABLE, null, contentValues)
        db.close()
    }

    override fun addCourse(course: Course) {
        val database = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COURSE_TITLE, course.title)
            put(COURSE_DESCRIPTION, course.desc)
        }
        database.insert(COURSE_TABLE, null, contentValues)
        database.close()
    }

    override fun getAllCoursesList(): ArrayList<Course> {
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


    override fun addTeacher(teacher: Teacher) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TEACHERS_NAME, teacher.name)
        contentValues.put(TEACHERS_SURNAME, teacher.surname)
        contentValues.put(TEACHERS_FATHER, teacher.fatherName)
        contentValues.put(TEACHERS_COURSE_ID, teacher.courseId?.id)
        database.insert(TEACHERS_TABLE, null, contentValues)
        database.close()
    }

    override fun getAllTeachersList(): ArrayList<Teacher> {
        val teachersList = ArrayList<Teacher>()
        val query: String = "select * from $TEACHERS_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val teacher = Teacher(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    getCourseById(cursor.getInt(4))
                )
                teachersList.add(teacher)
            } while (cursor.moveToNext())
        }

        return teachersList
    }

    override fun getCourseById(id: Int): Course {
        val database = this.readableDatabase
        val cursor: Cursor =
            database.query(
                COURSE_TABLE,
                arrayOf(COURSE_ID, COURSE_TITLE, COURSE_DESCRIPTION),
                "$COURSE_ID = ?",
                arrayOf("$id"),
                null,
                null,
                null
            )
        cursor.moveToFirst()
        return Course(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
    }

    override fun editTeacher(teacher: Teacher): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TEACHERS_ID, teacher.id)
        contentValues.put(TEACHERS_NAME, teacher.name)
        contentValues.put(TEACHERS_SURNAME, teacher.surname)
        contentValues.put(TEACHERS_FATHER, teacher.fatherName)
        contentValues.put(TEACHERS_COURSE_ID, teacher.courseId?.id)
        return database.update(
            TEACHERS_TABLE,
            contentValues,
            "$TEACHERS_ID = ?",
            arrayOf("${teacher.id}")
        )
    }

    override fun deleteTeacher(teacher: Teacher) {
        getGroupByMentorId(teacher)
        val database = this.writableDatabase
        database.delete(TEACHERS_TABLE, "$TEACHERS_ID = ?", arrayOf("${teacher.id}"))
        database.close()
    }

    override fun addGroup(group: Group) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(GROUP_POSITION, group.groupPosition)
        contentValues.put(GROUP_TITLE, group.groupTitle)
        contentValues.put(GROUP_TEACHER_NAME, group.groupTeacherName)
        contentValues.put(GROUP_TIME, group.groupTime)
        contentValues.put(GROUP_DAY, group.groupDay)
        contentValues.put(GROUP_TEACHER_ID, group.teacherId?.id)
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
                    cursor.getString(5),
                    getTeacherById(cursor.getInt(6)),
                    getCourseById(cursor.getInt(7))
                )
                groupsList.add(group)
            } while (cursor.moveToNext())
        }
        //


        return groupsList
    }

    override fun getTeacherById(id: Int): Teacher {
        val database = this.readableDatabase
        val cursor: Cursor =
            database.query(
                TEACHERS_TABLE,
                arrayOf(
                    TEACHERS_ID,
                    TEACHERS_NAME,
                    TEACHERS_SURNAME,
                    TEACHERS_FATHER,
                    TEACHERS_COURSE_ID
                ),
                "$TEACHERS_ID = ?",
                arrayOf("$id"),
                null,
                null,
                null
            )
        cursor.moveToFirst()
        return Teacher(
            cursor.getInt(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3),
            getCourseById(cursor.getInt(4))
        )
    }
    // +

    override fun editGroup(group: Group): Int {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(GROUP_ID, group.id)
        contentValues.put(GROUP_POSITION, group.groupPosition)
        contentValues.put(GROUP_TITLE, group.groupTitle)
        contentValues.put(GROUP_TEACHER_NAME, group.groupTeacherName)
        contentValues.put(GROUP_TIME, group.groupTime)
        contentValues.put(GROUP_DAY, group.groupDay)
        contentValues.put(GROUP_TEACHER_ID, group.teacherId?.id)
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
        database.insert(STUDENT_TABLE, null, contentValues)
        database.close()
    }

    override fun addResult(result: Results) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(RESULT_S_NAME, result.name)
        contentValues.put(RESULT_T_NAME, result.teacherName)
        contentValues.put(RESULT_SUBJECT, result.subject)
        contentValues.put(RESULT_DESCC, result.desc)
        contentValues.put(RESULT_IMAGE, result.image)
        contentValues.put(RESULT_ID, result.id)
        database.insert(RESULT_TABLE, null, contentValues)
        database.close()


    }

    override fun getAllResultsList(): ArrayList<Results> {
        val resultsList: ArrayList<Results> = ArrayList()
        val query: String = "select * from $RESULT_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val result = Results(

                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)

                )
                    resultsList.add(result)
            } while (cursor.moveToNext())
        }

        return resultsList
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
            STUDENT_TABLE,
            contentValues,
            "$STUDENT_ID = ?",
            arrayOf("${student.id}")
        )
    }

    override fun deleteStudent(student: Student) {
        getStudentById(student)
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
                    getGroupById(cursor.getInt(4))
                )
                deleteStudent(studentA)
            } while (cursor.moveToNext())
        }
    }

    override fun getAllStudentsList(): ArrayList<Student> {
        val studentsList: ArrayList<Student> = ArrayList()
        val query: String = "select * from $STUDENT_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    getGroupById(cursor.getInt(4))
                )
                studentsList.add(student)
            } while (cursor.moveToNext())
        }

        return studentsList
    }

    override fun deleteGroup(group: Group) {
        getStudentByGroupId(group)
        val database = this.writableDatabase
        database.delete(GROUP_TABLE, "$GROUP_ID = ?", arrayOf("${group.id}"))
        database.close()
    }

    override fun getGroupById(id: Int): Group {
        val database = this.readableDatabase
        val cursor: Cursor =
            database.query(
                GROUP_TABLE,
                arrayOf(
                    GROUP_ID,
                    GROUP_POSITION,
                    GROUP_TITLE,
                    GROUP_TEACHER_NAME,
                    GROUP_TIME,
                    GROUP_DAY,
                    GROUP_TEACHER_ID,
                    GROUP_COURSE_ID
                ),
                "$GROUP_ID = ?",
                arrayOf("$id"),
                null,
                null,
                null
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
            getCourseById(cursor.getInt(7))
        )
    }


    override fun getGroupByMentorId(teacher: Teacher) {
        val database = this.writableDatabase
        val query: String = "select * from $GROUP_TABLE where $GROUP_TEACHER_ID = ${teacher.id}"
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
                    getCourseById(cursor.getInt(7))
                )
                deleteGroup(group)
            } while (cursor.moveToNext())
        }
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
                    getGroupById(cursor.getInt(4))
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


