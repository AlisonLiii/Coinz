package com.example.s1891132.coinz.message

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}