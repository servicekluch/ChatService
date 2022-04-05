package com.service_kluch

import org.junit.Test

import org.junit.Assert.*

class ChatServiceTest {

    @Test
    fun chatService_createMessage() {
        val chatService = ChatService
        chatService.clear()
        val result = chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1, 2))
        assertNotNull(result)
    }

    @Test
    fun chatService_editMessage() {
        val chatService = ChatService
        chatService.clear()

        val comparedText = "Hello World"
        val message = chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1))
        val result = chatService.editMessage(messageId = message.id, newText = comparedText).text

        assertEquals(result, comparedText)
    }

    @Test(expected = MessageNotFoundException::class)
    fun chatService_MessageNotFoundException() {
        val chatService = ChatService
        chatService.clear()

        chatService.editMessage(messageId = Int.MAX_VALUE, newText = "Hello  Kotlin")
    }

    @Test
    fun chatService_deleteMessage() {
        val chatService = ChatService
        chatService.clear()

        val message = chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1))
        val result = chatService.deleteMessage(message.id)

        assertTrue(result)
    }

    @Test
    fun chatService_deleteLastMessage() {
        val chatService = ChatService
        chatService.clear()

        val message = chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1))
        val result = chatService.deleteMessage(message.id)

        assertTrue(result)
    }

    @Test
    fun chatService_readChatById() {
        val chatService = ChatService
        chatService.clear()

        chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1))
        val chatId = chatService.getRandomChatId()
        val result = chatService.readChatById(chatId = chatId).readingFlag

        assertTrue(result)
    }

    @Test
    fun chatService_readChatByMessageId() {
        val chatService = ChatService
        chatService.clear()

        val message = chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1)).id
        val result = chatService.readChatByMessageId(message).readingFlag

        assertTrue(result)
    }

    @Test
    fun chatService_readChatByMessageCount() {
        val chatService = ChatService
        chatService.clear()

        chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1))
        chatService.createMessage(fromId = 1, text = "Hello  Kotlin", toIdList = hashSetOf(0, 2))
        chatService.createMessage(fromId = 1, text = "Hello  World", toIdList = hashSetOf(0))
        chatService.createMessage(fromId = 1, text = "Hello  World", toIdList = hashSetOf(2))
        val result = chatService.readChatByMessageCount(1).size

        assertEquals(2, result)
    }

    @Test(expected = ChatNotFoundException::class)
    fun chatService_ChatNotFoundException() {
        val chatService = ChatService
        chatService.clear()

        chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1))
        chatService.createMessage(fromId = 1, text = "Hello  Kotlin", toIdList = hashSetOf(0, 2))
        chatService.createMessage(fromId = 1, text = "Hello  World", toIdList = hashSetOf(0))
        chatService.createMessage(fromId = 1, text = "Hello  World", toIdList = hashSetOf(2))

        chatService.readChatByMessageCount(7)
    }

    @Test
    fun chatService_deleteChat() {
        val chatService = ChatService
        chatService.clear()

        chatService.createMessage(fromId = 0, text = "Hello  Kotlin", toIdList = hashSetOf(1))
        chatService.createMessage(fromId = 1, text = "Hello  Kotlin", toIdList = hashSetOf(0, 2))
        chatService.createMessage(fromId = 1, text = "Hello  World", toIdList = hashSetOf(0))
        chatService.createMessage(fromId = 1, text = "Hello  World", toIdList = hashSetOf(2))

        val result = chatService.deleteChat(chatService.getRandomChatId())

        assertTrue(result)
    }
}
