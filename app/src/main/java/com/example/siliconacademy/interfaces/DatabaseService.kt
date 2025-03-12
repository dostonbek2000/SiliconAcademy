package com.example.siliconacademy.interfaces

import Group
import Student
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.Results

interface DatabaseService {

    fun addTeacher(course: Course)
    fun getAllTeachersList(): ArrayList<Course>
    fun getTeacherById(id: Int): Course


    fun addGroup(group: Group)
    fun getAllGroupsList(): ArrayList<Group>
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

}