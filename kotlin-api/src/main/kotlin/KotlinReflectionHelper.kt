import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

object KotlinReflectionHelper {
    @JvmStatic
    fun isDataClass(c: Class<*>) = c.kotlin.isData

    @JvmStatic
    fun asKClass(c:Class<*>) = c.kotlin

    @JvmStatic
    fun dataClassProps(c: KClass<*>) = c.primaryConstructor!!.parameters.map {
        KStructField(it.name!!, it.type.jvmErasure, it.type.isMarkedNullable)
    }
}

data class KStructField(val name: String, val c: KClass<*>, val nullable: Boolean)