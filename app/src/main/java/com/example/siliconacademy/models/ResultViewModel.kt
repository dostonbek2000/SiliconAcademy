package com.example.siliconacademy.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException

class ResultViewModel : ViewModel() {

    private val _resultsLiveData = MutableLiveData<List<Results>>()
    val resultsLiveData: LiveData<List<Results>> get() = _resultsLiveData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    private val client = OkHttpClient()
    private val baseUrl =
        "https://script.google.com/macros/s/AKfycbzfZQlU7NsUyBWhyysBlkDH9Oc3jhYlB4JDEYLlcXZj1NEl4lA3ZhIQtnJYLiUzVcqXXw/exec"

    fun fetchResults() {
        _loading.postValue(true)

        val url = "$baseUrl?action=get"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _loading.postValue(false)
                val errorMessage = "Yuklashda xatolik yuz berdi, qayta urining"
                Log.e("ResultViewModel", errorMessage, e)
                _statusMessage.postValue(errorMessage)
                _success.postValue(false)
            }

            override fun onResponse(call: Call, response: Response) {
                _loading.postValue(false)
                if (!response.isSuccessful) {
                    _statusMessage.postValue("Yuklab bo'lmadi,qayta yuklab ko'rin")
                    _success.postValue(false)
                    return
                }

                response.body?.let { responseBody ->
                    try {
                        val jsonString = responseBody.string()
                        val json = JSONObject(jsonString)
                        val resultsArray = json.getJSONArray("ListResults")
                        val results = mutableListOf<Results>()

                        for (i in 0 until resultsArray.length()) {
                            val item = resultsArray.getJSONObject(i)
                            results.add(
                                Results(
                                    id = item.getInt("id"),
                                    resultPosition = item.optInt("resultPosition"),
                                    name = item.getString("name"),
                                    age = item.getString("age"),
                                    testType = item.getString("testType"),
                                    teacherName = item.getString("teacherName"),
                                    subject = item.getString("subject"),
                                    imageUri = item.optString("imageUri"),
                                    fileUri = item.optString("fileUri")
                                )
                            )
                        }
                        _resultsLiveData.postValue(results)
                        _statusMessage.postValue("Natijalar yuklandi.")
                        _success.postValue(true)
                    } catch (e: Exception) {
                        _statusMessage.postValue("Xatolik yuz berdi, qayta urinib ko'rin")
                        _success.postValue(false)
                    }
                } ?: run {
                    _statusMessage.postValue("Response body is null")
                    _success.postValue(false)
                }
            }
        })
    }

    fun createResult(result: Results) {
        _loading.postValue(true)

        val urlBuilder = baseUrl.toHttpUrlOrNull()?.newBuilder()
            ?.addQueryParameter("action", "create")
            ?.addQueryParameter("resultPosition", result.resultPosition.toString())
            ?.addQueryParameter("name", result.name)
            ?.addQueryParameter("age", result.age)
            ?.addQueryParameter("testType", result.testType)
            ?.addQueryParameter("teacherName", result.teacherName)
            ?.addQueryParameter("subject", result.subject)
            ?.addQueryParameter("imageUri", result.imageUri ?: "")
            ?.addQueryParameter("fileUri", result.fileUri ?: "")

        val request = Request.Builder().url(urlBuilder.toString()).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _loading.postValue(false)
                _success.postValue(false)
                _statusMessage.postValue("Natija qo'shilmadi, qayta urinib ko'rin")
            }

            override fun onResponse(call: Call, response: Response) {
                _loading.postValue(false)
                val responseText = response.body?.string() ?: "Natija qo'shildi"
                _statusMessage.postValue(responseText)
                _success.postValue(true)
                fetchResults()
            }
        })
    }

    fun updateResult(result: Results) {
        _loading.postValue(true)

        val urlBuilder = baseUrl.toHttpUrlOrNull()?.newBuilder()
            ?.addQueryParameter("action", "update")
            ?.addQueryParameter("id", result.id.toString())
            ?.addQueryParameter("resultPosition", result.resultPosition.toString())
            ?.addQueryParameter("name", result.name)
            ?.addQueryParameter("age", result.age)
            ?.addQueryParameter("testType", result.testType)
            ?.addQueryParameter("teacherName", result.teacherName)
            ?.addQueryParameter("subject", result.subject)
            ?.addQueryParameter("imageUri", result.imageUri ?: "")
            ?.addQueryParameter("fileUri", result.fileUri ?: "")

        val request = Request.Builder().url(urlBuilder.toString()).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _loading.postValue(false)
                _statusMessage.postValue("yangilanmadi, qayta urinib ko'rin")
                _success.postValue(false)
            }

            override fun onResponse(call: Call, response: Response) {
                _loading.postValue(false)
                val responseText = response.body?.string() ?: "Natija yangilandi"
                _statusMessage.postValue(responseText)
                _success.postValue(true)
                fetchResults()
            }
        })
    }

    fun deleteResult(id: Int) {
        _loading.postValue(true)

        val url = "$baseUrl?action=delete&id=$id"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _loading.postValue(false)
                _statusMessage.postValue("o'chirilmadi, qayta urinib ko'rin")
                _success.postValue(false)
            }

            override fun onResponse(call: Call, response: Response) {
                _loading.postValue(false)
                val responseText = response.body?.string() ?: "Natija o'chirildi"
                _statusMessage.postValue(responseText)
                _success.postValue(true)
                fetchResults()
            }
        })
    }




override fun onCleared() {
        super.onCleared()
        Log.d("ResultViewModel", "ViewModel cleared, canceling all network calls")
        client.dispatcher.cancelAll()
    }
}
