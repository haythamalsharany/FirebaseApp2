package com.alsharany.firebaseapp

import com.google.firebase.firestore.PropertyName

data class NewsModel(@JvmField @PropertyName("title")var newsTitle:String="",
                     @JvmField @PropertyName("detailse")var newsDetails:String=""){
    fun toMap(): MutableMap<String, Any>{
        val news: MutableMap<String, Any> = HashMap()
        news.put("title",newsTitle)
        news.put("detailse",newsDetails)
        return news
    }
}