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

        val userIdList = (toIdList + fromId) as HashSet<Int>
        val chat: Chat = chats
            .ifEmpty { createChat(userIdList) }
            .let {
                chats.find { it.userIdList.containsAll(userIdList) && it.userIdList.size == userIdList.size }
                    ?: createChat(userIdList) }

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

        val updateMessage = messages.find { it.id == messageId } ?: throw MessageNotFoundException("Сообщение не найдено")

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
        return readChat(chats.find { it.id == chatId } ?: throw ChatNotFoundException("Чат не найден"))
    }

    fun readChatByMessageId(messageId: Int): Chat {
        return readChat(chats.find { it.messageIdList.contains(messageId) } ?: throw ChatNotFoundException("Чат не найден"))
    }

    fun readChatByMessageCount(messageCount: Int): List<Chat> {
        val filteredChats = chats
            .filter { it.messageCount == messageCount }
        filteredChats.asSequence()
            .ifEmpty { throw ChatNotFoundException("Чат не найден") }
            .forEach { readChat(it) }

        return filteredChats
    }

    private fun updateChatMessageList(chat: Chat, message: Message): Boolean {

        chats
            .also { if (!it.contains(chat) || it.indexOf(chat) > it.lastIndex) it += chat }
            .let {
                it.set(
                    index = it.indexOf(chat),
                    element = chat.copy(messageIdList = chat.messageIdList + message.id, readingFlag = false)
                )
            }

        return true
    }

    fun deleteMessage(messageId: Int): Boolean {
        messages.find { it.id == messageId } ?: throw MessageNotFoundException("Сообщение не найдено")

        val chat = chats.find { it.messageIdList.contains(messageId) } ?: throw ChatNotFoundException("Чат не найден")

        chat.messageIdList
            .filterNot { it == messageId }
            .ifEmpty {
                chats.remove(chat)
                return true
            }
            .let { chats.replacingElement(
                Item = chat,
                newItem = chat.copy(messageIdList = it))
            }
            .let { return true }
    }

    fun deleteChat(chatId: Int): Boolean {
        val chat = chats.find { it.id == chatId } ?: throw ChatNotFoundException("Чат не найден")

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
}