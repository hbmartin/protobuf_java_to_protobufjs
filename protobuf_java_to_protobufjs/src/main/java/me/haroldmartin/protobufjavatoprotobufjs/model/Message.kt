/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.model

typealias Message = List<Field>

typealias FullNamedMessages = Map<String, Descriptor>

typealias NestedMessages = Map<String, Any>

typealias ReflectedTypes = Map<Int, Class<*>>

data class RootFullNameAndMessages(
    val rootFullName: String,
    val messages: FullNamedMessages
)
