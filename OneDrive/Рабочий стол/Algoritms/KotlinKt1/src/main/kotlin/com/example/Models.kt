// Если ты выбрал Вариант А (с пакетами), оставь эту строку.
// Если Вариант Б (без пакетов), удали строку package com.example
package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String
)


@Serializable
data class ErrorResponse(
    val error: String
)