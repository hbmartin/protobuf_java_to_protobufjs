package me.haroldmartin.protobufjavatoprotobufjs

import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.ProtocolMessageEnum
import me.haroldmartin.protobufjavatoprotobufjs.adapter.GeneratedMessageToReflectedTypes
import me.haroldmartin.protobufjavatoprotobufjs.adapter.ProtobufDescriptorAndTypesToMessage
import me.haroldmartin.protobufjavatoprotobufjs.model.RootFullNameAndMessages
import kotlin.reflect.KClass

object ProtobufGeneratedJavaToProtobufJs {
    operator fun invoke(clazz: Class<*>): RootFullNameAndMessages? {
        return if (clazz.isGeneratedMessageV3Subclass) {
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

internal val Class<*>.isGeneratedMessageV3Subclass: Boolean
    get() = superclass?.isAssignableFrom(GeneratedMessageV3::class.java) == true

internal val Class<*>.isMessageEnumSubclass: Boolean
    get() = ProtocolMessageEnum::class.java.isAssignableFrom(this)
