package com.example.s1891132.coinz.message

import java.util.*

data class TextMessage(val text: String,
                       val time: Date,
                       val senderId: String)
{
    constructor() : this("", Date(0), "")
}