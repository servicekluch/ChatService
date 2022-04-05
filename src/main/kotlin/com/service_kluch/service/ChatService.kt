package com.service_kluch

object ChatService {

    private val messages = mutableListOf<Message>()
    private val chats = mutableListOf<Chat>()

    fun createMessage(fromId: Int, text: String, toIdList: HashSet<Int>): Message {
        val message = Message(
            id = (messages.lastOrNull()?.id ?: -1) + 1,
            fromId = fromId,
            text = text,
            readingFlag = false
        )
        messages += message

        val userIdListSet = (toIdList + fromId) as HashSet<Int>
        val chat: Chat = if (chats.isNotEmpty()) {
            chats.find { it.userIdList.containsAll(userIdListSet) && it.userIdList.size == userIdListSet.size }
                ?: createChat(userIdListSet)
        } else createChat(userIdListSet)

        updateChatMessageList(chat = chat, message = message)

        return message
    }

    private fun createChat(userIdsList: HashSet<Int>): Chat {
        return Chat(
            id = (chats.lastOrNull()?.id ?: -1) + 1,
            userIdList = userIdsList,
            messageIdList = emptyList(),
            unreadMessageIdList = emptyList(),
            readingFlag = false
        )
    }

    fun getRandomChatId(): Int {
        return chats.random().id
    }

    fun editMessage(messageId: Int, newText: String): Message {

        val updateMessage = messages.getItemByPredicate(
            predicate = { it.id == messageId },
            exception = MessageNotFoundException("Сообщение не найдено")
        )

        return messages.replacingElement(
            Item = updateMessage,
            newItem = updateMessage.copy(text = newText, readingFlag = false)
        )
    }

    private fun readChat(chat: Chat): Chat {
        messages.replaceAll { if (chat.messageIdList.contains(it.id)) it.copy(readingFlag = true) else it }
        return chats.replacingElement(
            Item = chat,
            newItem = chat.copy(readingFlag = true)
        )
    }

    fun readChatById(chatId: Int): Chat {
        return readChat(
            chats.getItemByPredicate(
                predicate = { it.id == chatId },
                exception = ChatNotFoundException("Чат не найден")
            )
        )
    }

    fun readChatByMessageId(messageId: Int): Chat {
        return readChat(
            chats.getItemByPredicate(
                predicate = { it.messageIdList.contains(messageId) },
                exception = ChatNotFoundException("Чат не найден")
            )
        )
    }

    fun readChatByMessageCount(messageCount: Int): List<Chat> {
        val filteredChats = chats.filter { it.messageCount == messageCount }
        if (filteredChats.isEmpty()) throw ChatNotFoundException("Чат не найден")
        for (chat in filteredChats) {
            readChat(chat)
        }
        return filteredChats
    }

    private fun updateChatMessageList(chat: Chat, message: Message): Boolean {

        if (chats.contains(chat)) if (chats.indexOf(chat) >= chats.lastIndex) {
            chats.replacingElement(
                Item = chat,
                newItem = chat.copy(messageIdList = chat.messageIdList + message.id, readingFlag = false)
            )
            return true
        }
        chats += chat.copy(messageIdList = chat.messageIdList + message.id)
        return true
    }

    fun deleteMessage(messageId: Int): Boolean {
        messages.getItemByPredicate(
            predicate = { it.id == messageId },
            exception = MessageNotFoundException("Сообщение не найдено")
        )

        val chat = chats.getItemByPredicate(
            predicate = { it.messageIdList.contains(messageId) },
            exception = ChatNotFoundException("Чат не найден")
        )
        val newMessageIdsList = chat.messageIdList.filterNot { it == messageId }

        if (newMessageIdsList.isNotEmpty()) {
            chats.replacingElement(Item = chat, newItem = chat.copy(messageIdList = newMessageIdsList))
        } else chats.remove(chat)
        return true
    }

    fun deleteChat(chatId: Int): Boolean {
        val chat = chats.getItemByPredicate(
            predicate = { it.id == chatId },
            exception = ChatNotFoundException("Чат не найден")
        )

        messages.removeAll { chat.messageIdList.contains(it.id) }
        return chats.remove(chat)
    }

    fun clear() {
        messages.clear()
        chats.clear()
    }

    private fun <T> MutableList<T>.replacingElement(Item: T, newItem: T): T {
        this[this.indexOf(Item)] = newItem
        return newItem
    }

    private fun <T> MutableList<T>.getItemByPredicate(predicate: (T) -> Boolean, exception: RuntimeException): T {
        val result = find(predicate)
        if (result != null) return result
        throw exception
    }
}