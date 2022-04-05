package com.service_kluch

data class Chat(
    /**
     * id чата
     */
    val id: Int,
    /**
     * список идентификаторов пользователей
     */
    val userIdList: HashSet<Int>,
    /**
    * список идентификаторов сообщений
    */
    val messageIdList: List<Int>,
    /**
     * Список идентификаторов непрочитанных сообщений
     */
    val unreadMessageIdList: List<Int>,
    /**
     * флаг прочтения
     */
    val readingFlag: Boolean = false
){
    /**
     * Колво сообщений
     */
    val messageCount = messageIdList.size
}