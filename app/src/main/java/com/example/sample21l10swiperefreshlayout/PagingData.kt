package com.example.sample21l10swiperefreshlayout

sealed class PagingData<out T> {
    data class Item<T>(val data: T) : PagingData<T>()
    object Loading : PagingData<Nothing>()
}