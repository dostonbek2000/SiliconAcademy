package com.example.siliconacademy.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL

class AttendanceViewModel : ViewModel() {

    private val baseUrl = "https://script.google.com/macros/s/AKfycbxjFuMAMPS2KUEFZNGg07iAkszcjTgxfs0vbfIQxjWNrqLgtsU8qLvCNLt11P9LNG6O/exec"

    private val _attendanceGroups = MutableLiveData<List<Pair<Int, String>>>()
    val attendanceGroups: LiveData<List<Pair<Int, String>>> get() = _attendanceGroups

    private val _attendanceRecords = MutableLiveData<List<AttendanceRecord>>()
    val attendanceRecords: LiveData<List<AttendanceRecord>> get() = _attendanceRecords

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun createGroupWithStudents(date: String, groupId: Int, students: List<AttendanceRecord>) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val studentsJson = JSONArray().apply {
                    for (student in students) {
                        val obj = JSONObject().apply {
                            put("studentFullName", student.studentFullName)
                            put("isPresent", student.isPresent)
                        }
                        put(obj)
                    }
                }

                val urlString = "$baseUrl?action=createGroupWithStudents" +
                        "&date=${URLEncoder.encode(date, "UTF-8")}" +
                        "&groupId=$groupId" +
                        "&studentsJson=${URLEncoder.encode(studentsJson.toString(), "UTF-8")}"

                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.connect()

                val responseCode = conn.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned $responseCode")
                }

                val result = conn.inputStream.bufferedReader().readText()
                _message.postValue(result.ifEmpty { "Davomat olindi" })

            } catch (e: Exception) {
                _errorMessage.postValue("Davomat olinmadi")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getAttendanceGroupsByGroupId(groupId: Int) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=getAttendanceGroupsByGroupId&groupId=$groupId")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connect()

                val response = conn.inputStream.bufferedReader().readText()
                val groupList = mutableListOf<Pair<Int, String>>()

                val json = JSONObject(response)
                val groups = json.getJSONArray("groupList")

                for (i in 0 until groups.length()) {
                    val obj = groups.getJSONObject(i)
                    val id = obj.getInt("id")
                    val date = obj.getString("date")
                    groupList.add(Pair(id, date))
                }

                _attendanceGroups.postValue(groupList)
                _message.postValue("Malumotlar yuklandi")
            } catch (e: Exception) {
                _errorMessage.postValue("Malumotlar yuklanmadi, qayta urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getAttendanceByAttendanceGroupId(attendanceGroupId: Int) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=getAttendanceByGroupId&attendanceGroupId=$attendanceGroupId")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connect()

                val response = conn.inputStream.bufferedReader().readText()
                val recordList = mutableListOf<AttendanceRecord>()

                val json = JSONObject(response)
                val records = json.getJSONArray("attendanceList")

                for (i in 0 until records.length()) {
                    val obj = records.getJSONObject(i)
                    val name = obj.getString("studentFullName")
                    val present = obj.getBoolean("isPresent")
                    recordList.add(AttendanceRecord(name, present))
                }

                _attendanceRecords.postValue(recordList)
                _message.postValue(" Malumotlar Yuklandi")
            } catch (e: Exception) {
                _errorMessage.postValue("Malumotlar yuklanmadi, qayta urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
