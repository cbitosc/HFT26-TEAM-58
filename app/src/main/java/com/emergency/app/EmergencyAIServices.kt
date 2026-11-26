package com.emergency.app

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface EmergencyAIService {

    @POST("test-ai")
    fun summarize(
        @Body request: MessageRequest
    ): Call<SummaryResponse>

    @POST("classify-emergency")
    fun classify(
        @Body request: MessageRequest
    ): Call<ClassificationResponse>

    @POST("translate")
    fun translate(
        @Body request: TranslationRequest
    ): Call<TranslationResponse>
}