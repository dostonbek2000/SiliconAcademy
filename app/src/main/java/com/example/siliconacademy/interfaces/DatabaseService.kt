package com.example.siliconacademy.interfaces

import Group
import Student
import Teacher
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.Results

interface DatabaseService {

    fun addCourse(course: Course)
    fun getAllCoursesList(): ArrayList<Course>
    fun getCourseById(id: Int): Course

    fun addTeacher(teacher: Teacher)
    fun getAllTeachersList(): ArrayList<Teacher>
    fun editTeacher(teacher: Teacher): Int
    fun deleteTeacher(teacher: Teacher)
    fun getTeacherById(id: Int): Teacher

    fun addGroup(group: Group)
    fun getAllGroupsList(): ArrayList<Group>
    fun editGroup(group: Group): Int
    fun deleteGroup(group: Group)
    fun getGroupById(id: Int): Group
    fun getGroupByMentorId(teacher: Teacher)

    fun addStudent(student: Student)
    fun editStudent(student: Student):Int
    fun getAllStudentsList(): ArrayList<Student>
    fun deleteStudent(student: Student)
    fun getStudentById(student: Student)
    fun getStudentByGroupId(group: Group)
    fun addResult(result: Results)
    fun getAllResultsList():ArrayList<Results>



}