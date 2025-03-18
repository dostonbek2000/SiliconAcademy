package com.example.siliconacademy.models
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.FormBody
import okhttp3.RequestBody
import org.json.JSONObject

class CourseViewModel : ViewModel() {

    private val baseUrl = "https://script.google.com/macros/s/AKfycbxsFHoLPi8-p6WYk3aGuRgKi3w5qcMWlN6DDX34sMEHuHnzhX_VecbWfEuyJRiVORUgfg/exec"
    private val client = OkHttpClient()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _courseList = MutableStateFlow<List<Course>>(emptyList())
    val courseList: StateFlow<List<Course>> = _courseList

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun getCourses() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val url = "$baseUrl?action=get"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val json = response.body?.string()

                val result = mutableListOf<Course>()
                val jsonObject = json?.let { JSONObject(it) }
                val courseArray = jsonObject?.getJSONArray("ListCourse")
                if (courseArray != null) {
                    for (i in 0 until courseArray.length()) {
                        val item = courseArray.getJSONObject(i)
                        val course = Course(
                            id = item.getInt("id"),
                            title = item.getString("name"),
                            desc = item.getString("desc")
                        )
                        result.add(course)
                    }
                }
                _courseList.value = result
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun createCourse(name: String, desc: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "$baseUrl?action=create&name=${name}&desc=${desc}"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                _message.value = response.body?.string() ?: "No response"
                getCourses()
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun updateCourse(id: Int, name: String, desc: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "$baseUrl?action=update&id=$id&name=${name}&desc=${desc}"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                _message.value = response.body?.string() ?: "No response"
                getCourses()
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteCourse(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "$baseUrl?action=delete&id=$id"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                _message.value = response.body?.string() ?: "No response"
                getCourses()
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            }
        }
    }
}
