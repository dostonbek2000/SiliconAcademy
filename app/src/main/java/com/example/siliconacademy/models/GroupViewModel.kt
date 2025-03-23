package com.example.siliconacademy.models

import Group
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
import java.net.URLEncoder

class GroupViewModel : ViewModel() {

    private val groupsLiveData = MutableLiveData<List<Group>>()
    val groups: LiveData<List<Group>> = groupsLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val baseUrl =
        "https://script.google.com/macros/s/AKfycbwzKweP2XYDV5Zz6r6xekjfCXjABFA2AB2cupCcvvjuE4M7sbKc6IQlHXLh99O3L-5x4w/exec"

    fun createGroup(group: Group) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlString = "$baseUrl?action=create" +
                        "&groupPosition=${group.groupPosition}" +
                        "&groupTitle=${URLEncoder.encode(group.groupTitle, "UTF-8")}" +
                        "&groupSubject=${URLEncoder.encode(group.groupSubject, "UTF-8")}" +
                        "&groupTime=${URLEncoder.encode(group.groupTime, "UTF-8")}" +
                        "&groupDay=${URLEncoder.encode(group.groupDay, "UTF-8")}" +
                        "&courseId=${group.courseId?.id}" +
                        "&fee=${group.fee}"

                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val responseCode = conn.responseCode
                Log.d("GroupViewModel", "Create group response: $responseCode")

                // Refresh the list after creating a new group
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _message.postValue("Yangi guruh qoʻshildi")
                    getGroups()
                } else {
                    _message.postValue("Xatolik yuz berdi, qayta urinib ko'rin")
                }

            } catch (e: Exception) {
                Log.e("GroupViewModel", "Create error: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getGroupById(groupId: Int): Group? {
        return groups.value?.find { it.id == groupId }
    }

    fun getGroups() {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=get")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 15000
                conn.readTimeout = 15000

                val responseCode = conn.responseCode
                Log.d("GroupViewModel", "Get groups response code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    Log.d("GroupViewModel", "Response: $response")

                    val groups = mutableListOf<Group>()
                    val jsonObject = JSONObject(response.toString())

                    if (jsonObject.has("GroupList")) {
                        val listArray = jsonObject.getJSONArray("GroupList")
                        Log.d("GroupViewModel", "Found ${listArray.length()} groups in response")

                        for (i in 0 until listArray.length()) {
                            val obj = listArray.getJSONObject(i)
                            try {
                                // Safely parse courseId
                                val courseIdValue = if (!obj.isNull("courseId")) {
                                    try {
                                        obj.getString("courseId").toInt()
                                    } catch (e: NumberFormatException) {
                                        Log.d(
                                            "GroupViewModel",
                                            "courseId is not an integer: ${obj.getString("courseId")}"
                                        )
                                        0 // Default value if parsing fails
                                    }
                                } else {
                                    0 // Default value if courseId is null
                                }

                                // Safely parse fee
                                val feeValue = if (!obj.isNull("fee")) {
                                    try {
                                        obj.getDouble("fee")
                                    } catch (e: Exception) {
                                        // If fee isn't a valid double, log and use default
                                        Log.d(
                                            "GroupViewModel",
                                            "Fee is not a valid double: ${obj.getString("fee")}"
                                        )
                                        0.0
                                    }
                                } else {
                                    0.0 // Default value if fee is null
                                }

                                val group = Group(
                                    id = obj.getInt("id"),
                                    groupTitle = obj.getString("groupTitle"),
                                    groupSubject = obj.getString("groupSubject"),
                                    groupTime = obj.getString("groupTime"),
                                    groupDay = obj.getString("groupDay"),
                                    groupPosition = obj.getInt("groupPosition"),
                                    fee = feeValue,
                                    courseId = Teacher(
                                        id = courseIdValue,
                                        title = null,
                                        desc = null,
                                        age = null,
                                        subject = null,
                                        toifa = null,
                                        cerA = null,
                                        gradeA = null,
                                        cerB = null,
                                        gradeB = null
                                    )
                                )
                                groups.add(group)
                                Log.d(
                                    "GroupViewModel",
                                    "Added group: ${group.id}, ${group.groupTitle}, courseId: ${group.courseId?.id}"
                                )
                            } catch (e: Exception) {
                                Log.e(
                                    "GroupViewModel",
                                    "Error parsing group at index $i: ${e.message}"
                                )
                                // Print the JSON object for debugging
                                Log.e("GroupViewModel", "JSON Object: ${obj.toString()}")
                            }
                        }
                    } else {
                        Log.e("GroupViewModel", "No GroupList found in response")
                    }

                    groupsLiveData.postValue(groups)
                } else {
                    Log.e("GroupViewModel", "HTTP Error: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Fetch error: ${e.message}")
                e.printStackTrace()
                _message.postValue("Ma'lumotlarni yuklanmadi, qayta urinib ko'rin")
                // Post empty list to avoid null issues
                groupsLiveData.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateGroup(group: Group) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlString = "$baseUrl?action=update" +
                        "&id=${group.id}" +
                        "&groupTitle=${URLEncoder.encode(group.groupTitle, "UTF-8")}" +
                        "&groupSubject=${URLEncoder.encode(group.groupSubject, "UTF-8")}" +
                        "&groupTime=${URLEncoder.encode(group.groupTime, "UTF-8")}" +
                        "&groupDay=${URLEncoder.encode(group.groupDay, "UTF-8")}" +
                        "&groupPosition=${group.groupPosition}" +
                        "&fee=${group.fee}" +
                        "&courseId=${group.courseId?.id}"

                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val responseCode = conn.responseCode
                Log.d("GroupViewModel", "Update group response: $responseCode")

                // Refresh the list after updating
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _message.postValue("Guruh ma'lumotlari yangilandi")
                    getGroups()
                } else {
                    _message.postValue("Yangilanmadi, qayta urinib ko'rin")
                }
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Update error: ${e.message}")
                _message.postValue("Xatolik yuz berdi, qayta urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteGroup(id: Int) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=delete&id=$id")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    _message.postValue("Guruh o'chirildi")
                    getGroups()
                } else {
                    _message.postValue("Ochirib bo'lmadi, qaytadan urinib ko'rin")
                }


            } catch (e: Exception) {
                Log.e("GroupViewModel", "Delete error: ${e.message}")
                _message.postValue("Xatolik yuz berdi,qaytadan urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}