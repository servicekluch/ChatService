package com.service_kluch

data class Message(
    /**
     * id сообщения
     */
    val id: Int,
    /**
     * id чата
     */
    val fromId: Int,
    /**
     * текст сообщения
     */
    val text: String = "text",
    /**
     * флаг прочтения
     */
    val readingFlag: Boolean = false
)