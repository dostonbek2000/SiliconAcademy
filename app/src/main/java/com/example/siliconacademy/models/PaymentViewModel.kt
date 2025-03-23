package com.example.siliconacademy.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class PaymentViewModel : ViewModel() {

    private val baseUrl = "https://script.google.com/macros/s/AKfycby3ymtie3n1b7N0FwWD2c2w_gOc2Uj59J_62TXncVi6ld90lfiTWZsgKFrcez9-6pNnAQ/exec"

    private val _payments = MutableLiveData<List<Payment>>()
    val payments: LiveData<List<Payment>> get() = _payments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    // 🔹 GET all payments
    fun getPayments() {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=get")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                val paymentArray = json.getJSONArray("PaymentList")

                val list = mutableListOf<Payment>()
                for (i in 0 until paymentArray.length()) {
                    val item = paymentArray.getJSONObject(i)
                    list.add(
                        Payment(
                            id = item.getInt("id"),
                            fullName = item.getString("fullName"),
                            amount = item.getString("amount"),
                            month = item.getString("month"),
                            date = item.getString("date"),
                            teacher = item.getString("teacher"),
                            group = item.getString("group")
                        )
                    )
                }
                _payments.postValue(list)
                _message.postValue("To'lovlar yuklandi")
            } catch (e: Exception) {
                _message.postValue("To'lovlar yuklanmadi, qayta urinib ko'rin ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // 🔹 CREATE payment
    fun createPayment(payment: Payment) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fullName = URLEncoder.encode(payment.fullName ?: "", "UTF-8")
                val amount = URLEncoder.encode(payment.amount, "UTF-8")
                val month = URLEncoder.encode(payment.month, "UTF-8")
                val date = URLEncoder.encode(payment.date ?: "", "UTF-8")
                val teacher =URLEncoder.encode(payment.teacher?:"", "UTF-8")
                val group =URLEncoder.encode(payment.group?:"","UTF-8")

                val url = "$baseUrl?action=create&fullName=$fullName&amount=$amount&month=$month&date=$date&teacher=$teacher&group=$group"
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val result = conn.inputStream.bufferedReader().readText()

                _message.postValue("To'lov amalga oshdi")
                getPayments()
            } catch (e: Exception) {
                _message.postValue("To'lov amalga oshmadi, qayta urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // 🔹 UPDATE payment
    fun updatePayment(payment: Payment) {
        if (payment.id == null) {
            _message.postValue("Invalid payment ID")
            return
        }

        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val id = payment.id
                val fullName = URLEncoder.encode(payment.fullName ?: "", "UTF-8")
                val amount = URLEncoder.encode(payment.amount, "UTF-8")
                val month = URLEncoder.encode(payment.month, "UTF-8")
                val date = URLEncoder.encode(payment.date ?: "", "UTF-8")
                val teacher =URLEncoder.encode(payment.teacher?:"", "UTF-8")
                val group =URLEncoder.encode(payment.group?:"","UTF-8")
                val url = "$baseUrl?action=update&id=$id&fullName=$fullName&amount=$amount&month=$month&date=$date&teacher=$teacher&group=$group"
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val result = conn.inputStream.bufferedReader().readText()

                _message.postValue("Payment updated successfully")
                getPayments()
            } catch (e: Exception) {
                _message.postValue("Failed to update payment: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // 🔹 DELETE payment
    fun deletePayment(id: Int) {
        _isLoading.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "$baseUrl?action=delete&id=$id"
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val result = conn.inputStream.bufferedReader().readText()

                _message.postValue("To'lov o'chirildi")
                getPayments()
            } catch (e: Exception) {
                _message.postValue("To'lov o'chirilmadi")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
