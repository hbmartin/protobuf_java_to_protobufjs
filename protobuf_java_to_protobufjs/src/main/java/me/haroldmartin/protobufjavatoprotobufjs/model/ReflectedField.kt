/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.model

data class ReflectedField(
    val id: Int? = null,
    val type: Class<*>,
    val keyClass: Class<*>? = null
)

typealias ReflectedFieldsList = List<ReflectedField>
