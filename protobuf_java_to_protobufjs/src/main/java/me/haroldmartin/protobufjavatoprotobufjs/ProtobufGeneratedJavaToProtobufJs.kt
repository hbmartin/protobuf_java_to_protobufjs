package me.haroldmartin.protobufjavatoprotobufjs

import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import me.haroldmartin.protobufjavatoprotobufjs.adapter.GeneratedMessageToReflectedTypes
import me.haroldmartin.protobufjavatoprotobufjs.adapter.ProtobufDescriptorAndTypesToMessage
import me.haroldmartin.protobufjavatoprotobufjs.model.RootFullNameAndMessages
import kotlin.reflect.KClass

object ProtobufGeneratedJavaToProtobufJs {
    operator fun invoke(clazz: Class<*>): RootFullNameAndMessages? {
        return if (clazz.superclass.isAssignableFrom(GeneratedMessageV3::class.java)) {
            (clazz.getMethod("getDescriptor").invoke(null) as? Descriptors.Descriptor)?.let {
                val reflectedTypes = GeneratedMessageToReflectedTypes(clazz)
                ProtobufDescriptorAndTypesToMessage(it, reflectedTypes).convert()
            }
        } else {
            null
        }
    }
}

fun <T : GeneratedMessageV3> KClass<T>.toMessages(): RootFullNameAndMessages? =
    ProtobufGeneratedJavaToProtobufJs(this.java)
