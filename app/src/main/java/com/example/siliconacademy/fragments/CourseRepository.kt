package com.example.siliconacademy.fragments

import android.content.Context
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class CourseRepository {

    private val client = OkHttpClient()
    private val BASE_URL = "https://script.google.com/macros/s/AKfycbxsFHoLPi8-p6WYk3aGuRgKi3w5qcMWlN6DDX34sMEHuHnzhX_VecbWfEuyJRiVORUgfg/exec?"

    suspend fun fetchCoursesFromGoogleSheetsAndSaveToDb(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$BASE_URL?action=get")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonArray = JSONArray(responseBody)

                    val database = CodialDatabase(context)
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val course = Course(
                            id = item.getInt("id"),
                            title = item.getString("title"),
                            desc = item.getString("desc")
                        )
                        database.addCourse(course)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addCourseToDbAndGoogleSheets(context: Context, course: Course) {
        withContext(Dispatchers.IO) {
            CodialDatabase(context).addCourse(course)
            addCourseToGoogleSheets(course)
        }
    }

    suspend fun addCourseToGoogleSheets(course: Course) {
        val json = JSONObject()
        json.put("action", "create")
        json.put("title", course.title)
        json.put("desc", course.description)

        val request = Request.Builder()
            .url(BASE_URL)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute()
    }

    suspend fun editCourseInDbAndGoogleSheets(context: Context, course: Course) {
        withContext(Dispatchers.IO) {
            CodialDatabase(context).editCourse(course)
            editCourseInGoogleSheets(course)
        }
    }

    suspend fun editCourseInGoogleSheets(course: Course) {
        val json = JSONObject()
        json.put("action", "update")
        json.put("id", course.id)
        json.put("title", course.title)
        json.put("desc", course.description)

        val request = Request.Builder()
            .url(BASE_URL)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute()
    }

    suspend fun deleteCourseFromDbAndGoogleSheets(context: Context, course: Course) {
        withContext(Dispatchers.IO) {
            CodialDatabase(context).deleteCourse(course)
            course.id?.let { deleteCourseFromGoogleSheets(it) }
        }
    }

    suspend fun deleteCourseFromGoogleSheets(id: Int) {
        val json = JSONObject()
        json.put("action", "deleteCourse")
        json.put("id", id)

        val request = Request.Builder()
            .url(BASE_URL)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute()
    }

    suspend fun getAllCoursesFromDb(context: Context): List<Course> {
        return withContext(Dispatchers.IO) {
            CodialDatabase(context).getAllCourseList()
        }
    }
}
