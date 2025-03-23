// ✅ Fully Enhanced Kotlin Code - TeacherViewModel.kt with LiveData, Progress & Error Message Handling

package com.example.siliconacademy.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TeacherViewModel : ViewModel() {

    private val _teachers = MutableLiveData<List<Teacher>>()
    val teachers: LiveData<List<Teacher>> get() = _teachers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val baseUrl = "https://script.google.com/macros/s/AKfycbzXXUuTtEclymQFqV5rVGD76VOoU9DJ2tt6FNXrM3Bp142umraC1cOkZR-kOd4vqjwvrQ/exec"

    fun createTeacher(title: String, desc: String, age: String, subject: String,toifa:String,cerA:String,gradeA:String, cerB:String,gradeB: String) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=create&title=$title&desc=$desc&age=$age&subject=$subject&toifa=$toifa&certificate=$cerA&grade=$gradeA&certificateB=$cerB&gradeB=$gradeB")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _message.postValue("Yangi o'qituvchi qoʻshildi")
                    getTeachers()
                } else {
                    _message.postValue("O'qituvchi qo'shilmadi, qayta urinib ko'rin")
                }
            } catch (e: Exception) {
                Log.e("TeacherViewModel", "Create error: ${e.message}")
                _message.postValue("Xatolik yuz berdi")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getTeachers() {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=get")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONObject(response).getJSONArray("TeacherList")
                val list = mutableListOf<Teacher>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        Teacher(
                            id = obj.optString("id").toIntOrNull(),
                            title = obj.optString("title"),
                            desc = obj.optString("desc"),
                            age = obj.optString("age"),
                            subject = obj.optString("subject"),
                            toifa = obj.optString("toifa"),
                            cerA = obj.optString("certificateA"),
                            gradeA = obj.optString("gradeA"),
                            cerB = obj.optString("certificateB"),
                            gradeB = obj.optString("gradeB")
                        )
                    )
                }
                _teachers.postValue(list)
            } catch (e: Exception) {
                Log.e("TeacherViewModel", "Fetch error: ${e.message}")
                _message.postValue("Ma'lumotlarni yuklanmadi, qayta urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateTeacher(id: Int, title: String, desc: String, age: String, subject: String,toifa:String,cerA:String,gradeA:String, cerB:String,gradeB: String) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=update&id=$id&title=$title&desc=$desc&age=$age&subject=$subject&toifa=$toifa&certificate=$cerA&grade=$gradeA&certificateB=$cerB&gradeB=$gradeB")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _message.postValue("O'qituvchi ma'lumotlari yangilandi")
                    getTeachers()
                } else {
                    _message.postValue("Yangilashda xatolik, qayta urinib ko'rin")
                }
            } catch (e: Exception) {
                Log.e("TeacherViewModel", "Update error: ${e.message}")
                _message.postValue("Xatolik yuz berdi")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteTeacher(id: Int) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=delete&id=$id")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _message.postValue("O'qituvchi o'chirildi")
                    getTeachers()
                } else {
                    _message.postValue("O'chirishda xatolik, qayta urinib ko'rin")
                }
            } catch (e: Exception) {
                Log.e("TeacherViewModel", "Delete error: ${e.message}")
                _message.postValue("Xatolik yuz berdi")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
