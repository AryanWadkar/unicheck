package com.example.testapp.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UniData (
    @SerializedName("state-province") val state:String?,
    @SerializedName("name") val name:String,
    @SerializedName("domains") val domains:Array<String>,
    @SerializedName("web_pages") val web:Array<String>,
    @SerializedName("country") val country:String,
    @SerializedName("alpha_two_code") val code:String,
    ): Serializable {

}
