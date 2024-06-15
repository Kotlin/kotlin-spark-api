package org.jetbrains.kotlinx.spark.extensions


trait VarargUnwrapperUDT1[T1, R] extends Serializable {
  def apply(v1: T1): R
}

trait VarargUnwrapperUDT2[T1, T2, R] extends Serializable {
  def apply(v1: T1, v2: T2): R
}

/**
 * Allows any simple vararg function reference to be treated as 23 different Scala functions.
 * Used to make vararg UDFs for `ScalaUDF`.
 *
 * @param varargFunc
 * @param newArray
 * @tparam T
 * @tparam Array
 * @tparam R
 */
class VarargUnwrapper[T, Array, R](
    val varargFunc: VarargUnwrapperUDT1[Array, R],
    val newArray: VarargUnwrapperUDT2[Integer, VarargUnwrapperUDT1[Integer, T], Array],
) extends Serializable
  with Function0[R]
  with Function1[T, R]
  with Function2[T, T, R]
  with Function3[T, T, T, R]
  with Function4[T, T, T, T, R]
  with Function5[T, T, T, T, T, R]
  with Function6[T, T, T, T, T, T, R]
  with Function7[T, T, T, T, T, T, T, R]
  with Function8[T, T, T, T, T, T, T, T, R]
  with Function9[T, T, T, T, T, T, T, T, T, R]
  with Function10[T, T, T, T, T, T, T, T, T, T, R]
  with Function11[T, T, T, T, T, T, T, T, T, T, T, R]
  with Function12[T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function13[T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function14[T, T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function15[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function16[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function17[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function18[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function19[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function20[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function21[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R]
  with Function22[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, R] {

  private def vararg(t: T*): R = varargFunc(newArray(t.size, { t(_) }))

  override def curried: Nothing = throw new UnsupportedOperationException()
  override def tupled: Nothing = throw new UnsupportedOperationException()

  override def apply(): R = vararg()

  override def apply(v0: T): R = vararg(v0)

  override def apply(v0: T, v1: T): R = vararg(v0, v1)

  override def apply(v0: T, v1: T, v2: T): R = vararg(v0, v1, v2)

  override def apply(v0: T, v1: T, v2: T, v3: T): R = vararg(v0, v1, v2, v3)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T): R = vararg(v0, v1, v2, v3, v4)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T): R = vararg(v0, v1, v2, v3, v4, v5)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T): R = vararg(v0, v1, v2, v3, v4, v5, v6)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T, v14: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T, v14: T, v15: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T, v14: T, v15: T, v16: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T, v14: T, v15: T, v16: T, v17: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T, v14: T, v15: T, v16: T, v17: T, v18: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T, v14: T, v15: T, v16: T, v17: T, v18: T, v19: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T, v14: T, v15: T, v16: T, v17: T, v18: T, v19: T, v20: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20)

  override def apply(v0: T, v1: T, v2: T, v3: T, v4: T, v5: T, v6: T, v7: T, v8: T, v9: T, v10: T, v11: T, v12: T, v13: T, v14: T, v15: T, v16: T, v17: T, v18: T, v19: T, v20: T, v21: T): R = vararg(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21)
}
