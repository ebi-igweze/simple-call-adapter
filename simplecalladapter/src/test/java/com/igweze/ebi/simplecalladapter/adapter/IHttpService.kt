package com.igweze.ebi.simplecalladapter.adapter

import com.igweze.ebi.simplecalladapter.Simple

interface IHttpService {

    fun getName(): Simple<String>

    fun getPlace(): Simple<String>
}