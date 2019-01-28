package com.igweze.ebi.simplecalladapterdemo

data class User(val name: String)
data class Response<T>(val results: T)