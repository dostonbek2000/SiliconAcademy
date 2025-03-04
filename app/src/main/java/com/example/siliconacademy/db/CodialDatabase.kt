package com.example.siliconacademy.db

import Group
import Student
import Teacher
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
import com.example.siliconacademy.utils.Content.RESULT_NAME
import com.example.siliconacademy.utils.Content.RESULT_SUBJECT
import com.example.siliconacademy.utils.Content.RESULT_TABLE
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

class CodialDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),
    DatabaseService {
    override fun onCreate(db: SQLiteDatabase?) {
        val courseQuery: String =
            "create table $COURSE_TABLE($COURSE_ID integer not null primary key autoincrement unique, $COURSE_TITLE text not null, $COURSE_DESCRIPTION text not null)"
        val teacherQuery: String =
            "create table $TEACHERS_TABLE($TEACHERS_ID integer not null primary key autoincrement unique, $TEACHERS_NAME text not null, $TEACHERS_SURNAME text not null, $TEACHERS_FATHER text not null, $TEACHERS_COURSE_ID integer not null, foreign key($TEACHERS_COURSE_ID) references $COURSE_TABLE($COURSE_ID))"
        val groupQuery: String =
            "create table $GROUP_TABLE($GROUP_ID integer not null primary key autoincrement unique, $GROUP_POSITION integer not null, $GROUP_TITLE text not null, $GROUP_TEACHER_NAME text not null, $GROUP_TIME text not null, $GROUP_DAY text not null, $GROUP_TEACHER_ID integer not null, $GROUP_COURSE_ID integer not null, foreign key($GROUP_TEACHER_ID) references $TEACHERS_TABLE($TEACHERS_ID), foreign key($GROUP_COURSE_ID) references $COURSE_TABLE($COURSE_ID))"
        val studentQuery: String =
            "create table $STUDENT_TABLE($STUDENT_ID integer not null primary key autoincrement unique, $STUDENT_NAME text not null, $STUDENT_SURNAME text not null, $STUDENT_FATHER_NAME text not null, $STUDENT_GROUP_ID integer not null, foreign key($STUDENT_GROUP_ID) references $GROUP_TABLE($GROUP_ID))"
        val createPaymentTable = """
        CREATE TABLE ${Content.PAYMENT_TABLE} (
            ${Content.PAYMENT_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Content.STUDENT_ID} INTEGER,
            ${Content.PAYMENT_AMOUNT} REAL,
            ${Content.PAYMENT_MONTH} TEXT
        );
    """.trimIndent()
        val result:String="create table $RESULT_TABLE($RESULT_ID integer not null primary key autoincrement unique,$RESULT_NAME text not null,$RESULT_SUBJECT text not null, $RESULT_DESCC text not null, $RESULT_IMAGE text not null)"
db?.execSQL(result)
        db?.execSQL(createPaymentTable)
        db?.execSQL(courseQuery)
        db?.execSQL(teacherQuery)
        db?.execSQL(groupQuery)
        db?.execSQL(studentQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) { // Update version number accordingly
            db?.execSQL(
                """
            CREATE TABLE IF NOT EXISTS ${Content.PAYMENT_TABLE} (
                ${Content.PAYMENT_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Content.STUDENT_ID} INTEGER,
                ${Content.PAYMENT_AMOUNT} REAL,
                ${Content.PAYMENT_MONTH} TEXT
            );
        """.trimIndent()
            )
        }
    }

    fun getPaymentsByStudentId(studentId: Int): List<Payment> {
        val db = this.readableDatabase
        val list = ArrayList<Payment>()
        val cursor = db.rawQuery(
            "SELECT * FROM $PAYMENT_TABLE WHERE $PAYMENT_ID = ?",
            arrayOf(studentId.toString())
        )

        while (cursor.moveToNext()) {
            val payment = Payment(
                id = cursor.getInt(0),
                studentId = cursor.getInt(1),
                amount = cursor.getDouble(2).toString(),
                month = cursor.getString(3)
            )
            list.add(payment)
        }
        cursor.close()
        db.close()
        return list
    }

    fun addPayment(payment: Payment) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id", payment.studentId)
        contentValues.put("amount", payment.amount)
        contentValues.put("month", payment.month)

        db.insert("$PAYMENT_TABLE", null, contentValues)
        db.close()
    }


    override fun addCourse(course: Course) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COURSE_TITLE, course.title)
        contentValues.put(COURSE_DESCRIPTION, course.desc)
        database.insert(COURSE_TABLE, null, contentValues)
        database.close()
    }

    override fun getAllCoursesList(): ArrayList<Course> {
        val coursesList = ArrayList<Course>()
        val query: String = "select * from $COURSE_TABLE"
        val database = this.readableDatabase
        val cursor: Cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val course = Course(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
                coursesList.add(course)
            } while (cursor.moveToNext())
        }

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
        val database=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(RESULT_NAME,result.name)
        contentValues.put(RESULT_SUBJECT,result.subject)
        contentValues.put(RESULT_DESCC,result.desc)
        contentValues.put(RESULT_IMAGE,result.image)
        contentValues.put(RESULT_ID,result.studentId)
        database.insert(RESULT_TABLE,null,contentValues)
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


}