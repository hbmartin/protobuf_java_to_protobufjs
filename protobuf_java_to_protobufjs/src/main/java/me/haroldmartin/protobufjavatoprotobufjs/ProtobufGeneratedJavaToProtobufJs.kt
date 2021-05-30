/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs

import com.google.protobuf.GeneratedMessageV3
import me.haroldmartin.protobufjavatoprotobufjs.adapter.ClassToRootAndDescriptors
import me.haroldmartin.protobufjavatoprotobufjs.adapter.DescriptorMapToJs
import me.haroldmartin.protobufjavatoprotobufjs.adapter.JsDescriptors
import kotlin.reflect.KClass

object ProtobufGeneratedJavaToProtobufJs {
    operator fun invoke(clazz: Class<*>): RootNameAndJsDescriptors? {
        return ClassToRootAndDescriptors(clazz)?.let {
            RootNameAndJsDescriptors(
                rootFullName = it.rootFullName,
                descriptors = DescriptorMapToJs(it.descriptorMap)
            )
        }
    }
}

fun <T : GeneratedMessageV3> KClass<T>.toJsDescriptors(): RootNameAndJsDescriptors? =
    ProtobufGeneratedJavaToProtobufJs(this.java)

data class RootNameAndJsDescriptors(
    val rootFullName: String,
    val descriptors: JsDescriptors
)
