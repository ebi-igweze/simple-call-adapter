package com.igweze.ebi.simplecalladapterdemo

data class UserName(val title: String, val first: String, val last: String)
data class User(val name: UserName)
data class Result(val user: User)
data class Response<T>(val results: T)