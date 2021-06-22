/*
 * Copyright (c) Harold Martin
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package me.haroldmartin.protobufjavatoprotobufjs.model

sealed class ConversionExceptions(message: String) : Exception(message) {
    class UnknownMapValueType(type: Class<*>) : ConversionExceptions("Unknown map value class type: $type")
}
