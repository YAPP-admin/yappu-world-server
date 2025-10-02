package co.yappuworld.external.messenger

import co.yappuworld.external.messenger.dto.MessageContent

interface MessengerClient {

    fun send(content: MessageContent)
}
