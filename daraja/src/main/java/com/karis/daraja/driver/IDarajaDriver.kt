package com.karis.daraja.driver

import com.karis.daraja.model.requests.STKPushRequest
import com.karis.daraja.utils.DarajaStates
import com.karis.daraja.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IDarajaDriver {
    fun performStkPush(stkPushRequest: STKPushRequest): Flow<DarajaStates<out Resource<out Any>>>
}
