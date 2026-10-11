// ВАЖНО: Если ты делал "Вариант Б" (без пакетов), удали следующую строку:
package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    // Запуск сервера на порту 8080
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // In-memory хранилище для задач (имитация базы данных)
    val tasks = mutableListOf(
        Task(1, "Сделать КТ1", "Написать сервер на Ktor", false),
        Task(2, "Загрузить на GitHub", "Создать публичный репозиторий", false)
    )
    var nextId = 3

    // 2. Настройка маршрутов (Routing)
    routing {
        route("/tasks") {

            // --- GET /tasks?completed=true|false ---
            get {
                // Обработка Query-параметра
                val completedParam = call.request.queryParameters["completed"]

                val filteredTasks = if (completedParam != null) {
                    val isCompleted = completedParam.toBooleanStrictOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Параметр 'completed' должен быть true или false")
                        )
                    tasks.filter { it.isCompleted == isCompleted }
                } else {
                    tasks
                }

                // 200 OK: возвращаем список задач напрямую
                call.respond(HttpStatusCode.OK, filteredTasks)
            }

            // --- GET /tasks/{id} ---
            get("/{id}") {
                // Обработка Path-параметра
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Неверный формат ID. Ожидается число.")
                    )

                val task = tasks.find { it.id == id }
                if (task != null) {
                    call.respond(HttpStatusCode.OK, task) // 200 OK
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Задача с ID $id не найдена")) // 404 Not Found
                }
            }

            // --- POST /tasks ---
            post {
                try {
                    // Сериализация входящего JSON в объект CreateTaskRequest
                    val request = call.receive<CreateTaskRequest>()

                    // Простая валидация
                    if (request.title.isBlank()) {
                        return@post call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Название задачи не может быть пустым")
                        )
                    }

                    val newTask = Task(
                        id = nextId++,
                        title = request.title,
                        description = request.description
                    )
                    tasks.add(newTask)

                    // 201 Created: стандартный код для успешного создания ресурса
                    call.respond(HttpStatusCode.Created, newTask)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Ошибка парсинга JSON: ${e.message}"))
                }
            }


            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Неверный формат ID. Ожидается число.")
                    )

                val isRemoved = tasks.removeAll { it.id == id }

                if (isRemoved) {
                    // 204 No Content: стандартный код для успешного удаления (тело ответа пустое)
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Задача с ID $id не найдена")) // 404 Not Found
                }
            }
        }
    }
}