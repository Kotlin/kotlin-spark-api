import org.apache.hadoop.shaded.com.google.common.base.MoreObjects
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.GenericInternalRow
import org.apache.spark.sql.types.*
import org.apache.spark.unsafe.types.UTF8String
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.tuples.tupleOf
import java.io.Serializable
import kotlin.reflect.jvm.jvmName

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
class City(val name: String, val departmentNumber: Int) : Serializable {

    override fun toString(): String =
        MoreObjects
            .toStringHelper(this)
            .add("name", name)
            .add("departmentNumber", departmentNumber)
            .toString()
}

fun main() = withSpark {

//    UDTRegistration.register(City::class.jvmName, CityUserDefinedType::class.jvmName)

    val items = listOf(
        City("Amsterdam", 1),
        City("Breda", 2),
        City("Oosterhout", 3),
    ).map(::tupleOf)

    val ds = items.toDS()
    ds.showDS()
}