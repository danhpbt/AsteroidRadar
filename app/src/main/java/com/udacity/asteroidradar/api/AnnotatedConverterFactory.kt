package com.udacity.asteroidradar.api

import retrofit2.Converter
import kotlin.reflect.KClass
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.lang.annotation.RetentionPolicy
import java.lang.reflect.Type
import okhttp3.RequestBody




//https://stackoverflow.com/questions/40824122/android-retrofit-2-multiple-converters-gson-simplexml-error
class AnnotatedConverterFactory(val factories: Map<KClass<*>, Converter.Factory>) : Converter.Factory(){
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Json {}

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Scalar {}

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {

        for (annotation in annotations) {
            var factory: Converter.Factory? = factories[annotation.annotationClass]
            if (factory != null) {
                return factory.responseBodyConverter(type, annotations, retrofit)
            }
        }
        //try to default to json in case no annotation on current method was found
        val jsonFactory: Converter.Factory? = factories.get(Json::class)
        if (jsonFactory != null) {
            return jsonFactory.responseBodyConverter(type, annotations, retrofit)
        }

        return null
    }

    class Builder {
        val factories: LinkedHashMap<KClass<*>, Converter.Factory> = LinkedHashMap()

        fun add(cls: KClass<*>, factory: Converter.Factory): Builder {
            if (cls == null) {
                throw NullPointerException("cls")
            }

            if (factory == null) {
                throw NullPointerException("factory")
            }

            factories[cls] = factory
            return this
        }

        fun build(): AnnotatedConverterFactory {
            return AnnotatedConverterFactory(factories)
        }
    }
}