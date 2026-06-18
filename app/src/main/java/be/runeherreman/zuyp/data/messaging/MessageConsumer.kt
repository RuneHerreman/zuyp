package be.runeherreman.zuyp.data.messaging

interface MessageConsumer {
    fun startConsuming(userId: String)
    fun stopConsuming()
    var onMessageReceived: ((String) -> Unit)
}
