package com.demo

class Customer  (
    var id: Int?,
    var firstname: String?,
    var lastname: String?
) : AbstractJpaPersistable<Int>()
