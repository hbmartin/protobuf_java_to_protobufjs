package me.haroldmartin.protobuf_java_to_protobufjs

import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import me.haroldmartin.protobuf_java_to_protobufjs.adapter.GeneratedMessageToReflectedTypes
import me.haroldmartin.protobuf_java_to_protobufjs.adapter.ProtobufDescriptorToMessageModel
import me.haroldmartin.protobuf_java_to_protobufjs.model.RootFullNameAndMessages

object ProtobufGeneratedJavaToProtobufJs {
    operator fun invoke(clazz: Class<*>): RootFullNameAndMessages? {
        return if (clazz.superclass.isAssignableFrom(GeneratedMessageV3::class.java)) {
            (clazz.getMethod("getDescriptor").invoke(null) as? Descriptors.Descriptor)?.let {
                val reflectedTypes = GeneratedMessageToReflectedTypes(clazz)
                ProtobufDescriptorToMessageModel(it, reflectedTypes).convert()
            }
        } else {
            null
        }
    }
}

fun <T : GeneratedMessageV3> Class<T>.toMessages(): RootFullNameAndMessages? =
    ProtobufGeneratedJavaToProtobufJs(this)
