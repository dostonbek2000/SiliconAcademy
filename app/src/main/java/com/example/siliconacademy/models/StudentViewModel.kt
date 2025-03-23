package com.example.siliconacademy.models

import Group
import Student
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class StudentViewModel : ViewModel() {

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val baseUrl = "https://script.google.com/macros/s/AKfycbxRdG_PA9qwyJY0YtATNgawxB2047tER7h35XNvFSjRnjtJyX8dKERmw5AvQvtc8u6kVw/exec"

    private var groupViewModel: GroupViewModel? = null
    private val TAG = "StudentViewModel"

    fun setGroupViewModel(viewModel: GroupViewModel) {
        groupViewModel = viewModel
        getStudents()
    }

    fun getStudents() {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)
        _message.postValue(null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                groupViewModel?.getGroups()

                val studentList = fetchStudentsFromBackend()
                withContext(Dispatchers.Main) {
                    _students.value = studentList
                    checkAndResetRemovalStatus()
                    _isLoading.value=false
                    _message.value = "O'quvchilar yuklandi"

                }

            } catch (e: Exception) {
                Log.e(TAG, "getStudents error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "O'quvchilarni yuklab bo‘lmadi, qayta urinib ko‘ring"
                    _isLoading.value=false
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }
    private suspend fun fetchStudentsFromBackend(): List<Student> {
        val url = URL("$baseUrl?action=get")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        val responseCode = connection.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Server responded with code: $responseCode")
        }

        val response = connection.inputStream.bufferedReader().readText()
        return parseStudentsWithGroups(response)
    }


    private fun parseStudentsWithGroups(response: String): List<Student> {
        val studentList = mutableListOf<Student>()
        val jsonObject = JSONObject(response)
        val jsonArray = jsonObject.getJSONArray("StudentList")

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val groupIdValue = try {
                if (!obj.isNull("groupId")) obj.getString("groupId").toIntOrNull() ?: 0 else 0
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing groupId: ${e.message}")
                0
            }
            val group = groupViewModel?.getGroupById(groupIdValue)
            val defaultGroup = Group(id = groupIdValue, groupPosition = 1, groupTitle = "", groupTime = "", groupDay = "", courseId = Teacher(), fee = 0.0, groupSubject = "")

            val student = Student(
                id = obj.optInt("id"),
                name = obj.optString("name"),
                surname = obj.optString("surname"),
                fatherName = obj.optString("fatherName"),
                age = obj.optString("age"),
                groupId = group ?: defaultGroup,
                date = obj.optString("date"),
                accountBalance = obj.optDouble("accountBalance", 0.0),
                removedStatus = obj.optString("removedPosition", "not removed")
            )
            studentList.add(student)
        }
        return studentList
    }

    private fun checkAndResetRemovalStatus() {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            resetAllStudentsRemovedStatus()
        }
    }

    fun resetAllStudentsRemovedStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentList = _students.value ?: return@launch
                for (student in currentList) {
                    if (student.removedStatus == "removed") {
                        val updatedStudent = student.copy(removedStatus = "not removed")
                        updateStudentInBackend(updatedStudent)
                    }
                }
                _message.postValue("Statuslar yangilandi")
                getStudents()
            } catch (e: Exception) {
                _errorMessage.postValue("Xatolik: ${e.message}")
            }
        }
    }

    fun deductMonthlyFeeForEligibleStudents() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                groupViewModel?.getGroups()
                val currentList = _students.value ?: emptyList()
                for (student in currentList) {
                    if (student.removedStatus == "not removed") {
                        val group = groupViewModel?.getGroupById(student.groupId?.id ?: 0)
                        val fee = group?.fee ?: continue
                        val newBalance = (student.accountBalance ?: 0.0) - fee
                        val updatedStudent = student.copy(accountBalance = newBalance, removedStatus = "removed", groupId = group)
                        updateStudentInBackend(updatedStudent)
                    }
                }
                _message.postValue("To'lovlar ushlab qolindi")
                getStudents()
            } catch (e: Exception) {
                _errorMessage.postValue("Xatolik: ${e.message}")
            }
        }
    }

    private suspend fun updateStudentInBackend(student: Student) {
        val urlString = "$baseUrl?action=update" +
                "&id=${student.id}" +
                "&name=${student.name}" +
                "&surname=${student.surname}" +
                "&fatherName=${student.fatherName}" +
                "&age=${student.age}" +
                "&groupId=${student.groupId?.id}" +
                "&date=${student.date}" +
                "&accountBalance=${student.accountBalance}" +
                "&removedPosition=${student.removedStatus}"

        val url = URL(urlString)
        val connection = withContext(Dispatchers.IO) { url.openConnection() } as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 25000
        connection.readTimeout = 25000
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            Log.e(TAG, "Failed to update student: Server responded with ${connection.responseCode}")
        }
    }

    fun createStudent(student: Student) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)
        _message.postValue(null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlString = "$baseUrl?action=create" +
                        "&name=${student.name}" +
                        "&surname=${student.surname}" +
                        "&fatherName=${student.fatherName}" +
                        "&age=${student.age}" +
                        "&groupId=${student.groupId?.id}" +
                        "&date=${student.date}" +
                        "&accountBalance=${student.accountBalance}" +
                        "&removedPosition=${student.removedStatus}"

                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned code: ${connection.responseCode}")
                }
                _message.postValue("O'quvchi yaratildi")
                getStudents()

            } catch (e: Exception) {
                _errorMessage.postValue("O'quvchini qo'shilmadi, qayta urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateStudent(student: Student) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)
        _message.postValue(null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateStudentInBackend(student)
                _message.postValue("O'quvchi yangilandi")
                getStudents()
            } catch (e: Exception) {
                _errorMessage.postValue("O'quvchini yangilashda xatolik, qayta urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteStudent(id: Int) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)
        _message.postValue(null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$baseUrl?action=delete&id=$id")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 25000
                connection.readTimeout = 25000
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned code: ${connection.responseCode}")
                }

                _message.postValue("O'quvchi o'chirildi")
                getStudents()
            } catch (e: Exception) {
                _errorMessage.postValue("O'quvchini o‘chirilimadi, qaytadan urinib ko'rin")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
