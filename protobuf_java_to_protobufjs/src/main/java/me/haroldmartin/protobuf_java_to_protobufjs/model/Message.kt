/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobuf_java_to_protobufjs.model

typealias Message = List<Field>

typealias FullNamedMessages = Map<String, Message>

typealias NestedMessages = Map<String, Any>

data class RootFullNameAndMessages(
    val rootFullName: String,
    val messages: FullNamedMessages
)
