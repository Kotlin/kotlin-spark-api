/*-
 * =LICENSE=
 * Kotlin Spark API: Examples for Spark 3.2+ (Scala 2.12)
 * ----------
 * Copyright (C) 2019 - 2022 JetBrains
 * ----------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =LICENSEEND=
 */
package org.jetbrains.kotlinx.spark.examples

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow
import org.apache.spark.sql.types.*
import org.apache.spark.unsafe.types.UTF8String
import org.glassfish.jersey.internal.guava.MoreObjects
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.tuples.tupleOf

class CityUserDefinedType : UserDefinedType<City>() {

    override fun sqlType(): DataType = DATA_TYPE

    override fun serialize(city: City): InternalRow = GenericInternalRow(2).apply {
        setInt(DEPT_NUMBER_INDEX, city.departmentNumber)
        update(NAME_INDEX, UTF8String.fromString(city.name))
    }

    override fun deserialize(datum: Any): City =
        if (datum is InternalRow)
            City(
                name = datum.getString(NAME_INDEX),
                departmentNumber = datum.getInt(DEPT_NUMBER_INDEX),
            )
        else throw IllegalStateException("Unsupported conversion")

    override fun userClass(): Class<City> = City::class.java

    companion object {
        private const val DEPT_NUMBER_INDEX = 0
        private const val NAME_INDEX = 1
        private val DATA_TYPE = DataTypes.createStructType(
            arrayOf(
                DataTypes.createStructField(
                    "departmentNumber",
                    DataTypes.IntegerType,
                    false,
                    MetadataBuilder().putLong("maxNumber", 99).build(),
                ),
                DataTypes.createStructField("name", DataTypes.StringType, false)
            )
        )
    }
}

@SQLUserDefinedType(udt = CityUserDefinedType::class)
class City(val name: String, val departmentNumber: Int) {

    override fun toString(): String =
        MoreObjects
            .toStringHelper(this)
            .add("name", name)
            .add("departmentNumber", departmentNumber)
            .toString()
}

fun main() = withSpark {

//  Either use @SQLUserDefinedType or:
//  UDTRegistration.register(org.jetbrains.kotlinx.spark.examples.City::class.jvmName, org.jetbrains.kotlinx.spark.examples.CityUserDefinedType::class.jvmName)

    val items = listOf(
        City("Amsterdam", 1),
        City("Breda", 2),
        City("Oosterhout", 3),
    )

    val ds = items.map(::tupleOf).toDS()
    ds.showDS()

    // Unlike in Scala, you can also directly encode UDT registered types to a Dataset!
    val ds2 = items.toDS()
    ds2.showDS()
}
