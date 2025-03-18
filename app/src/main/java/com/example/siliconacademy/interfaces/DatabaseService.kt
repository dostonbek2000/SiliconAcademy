package com.example.siliconacademy.interfaces

import Group
import Student
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.Payment
import com.example.siliconacademy.models.Teacher
import com.example.siliconacademy.models.Results

interface DatabaseService {

    fun addTeacher(teacher: Teacher)
    fun getAllTeachersList(): ArrayList<Teacher>
    fun getTeacherById(id: Int): Teacher
    fun deleteTeacher(teacher: Teacher)
    fun editTeacher(teacher: Teacher):Int

    fun addCourse(course: Course)
    fun getAllCourseList():ArrayList<Course>
    fun deleteCourse(course: Course)
    fun editCourse(course: Course):Int


    fun addGroup(group: Group)
  //  fun getAllGroupsList(): ArrayList<Group>
    fun editGroup(group: Group): Int
    fun deleteGroup(group: Group)
    fun getGroupById(id: Int): Group

    fun addStudent(student: Student)
    fun editStudent(student: Student):Int
    fun getAllStudentsList(): ArrayList<Student>
    fun deleteStudent(student: Student)
    fun getStudentById(student: Student)
    fun getStudentByGroupId(group: Group)

    fun addResult(result: Results)
    fun getAllResultsList():ArrayList<Results>
    fun editResult(results: Results):Int
    fun deleteResult(results: Results)

    fun addPayment(payment: Payment)
    fun editPayment(payment: Payment):Int
    fun deletePayment(payment: Payment)
    fun getAllPaymentList():ArrayList<Payment>
}