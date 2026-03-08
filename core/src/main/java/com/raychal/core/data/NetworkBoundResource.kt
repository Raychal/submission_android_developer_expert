package com.raychal.core.data

import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource<ResultType, RequestType> {

    private var result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        val dbSource = loadFromDB().first()
        if (shouldFetch(dbSource)) {
            emit(Resource.Loading(dbSource))
            try {
                val response = createCall().first()
                saveCallResult(response)
                emitAll(loadFromDB().map { Resource.Success(it) })
            } catch (e: Exception) {
                onFetchFailed()
                emitAll(loadFromDB().map { Resource.Error(e.message ?: "Unknown Error", it) })
            }
        } else {
            emitAll(loadFromDB().map { Resource.Success(it) })
        }
    }

    protected abstract fun loadFromDB(): Flow<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun createCall(): Flow<RequestType>

    protected abstract suspend fun saveCallResult(data: RequestType)

    protected open fun onFetchFailed() {}

    fun asFlow(): Flow<Resource<ResultType>> = result
}
