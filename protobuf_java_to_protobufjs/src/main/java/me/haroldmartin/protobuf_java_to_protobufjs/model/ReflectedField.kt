/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobuf_java_to_protobufjs.model

data class ReflectedField(
    val type: Class<*>,
    val id: Int? = null
)
