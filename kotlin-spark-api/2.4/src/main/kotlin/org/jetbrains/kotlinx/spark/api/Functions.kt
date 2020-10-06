@file:Suppress("FunctionName", "PackageDirectoryMismatch")

package scala

import scala.runtime.*

/**
 * Hides the functional interface [scala.Function0]
 * @see scala.Function0
 */
inline fun <R> Function0(crossinline block: ()->R): Function0<R> =
        object : AbstractFunction0<R>() {
            override fun apply(): R = block()
        }

/**
 * Hides the functional interface [scala.Function1]
 * @see scala.Function1
 */
inline fun <T0, R> Function1(crossinline block: (T0)->R): Function1<T0, R> =
        object : AbstractFunction1<T0, R>() {
            override fun apply(p0: T0): R = block(p0)
        }

/**
 * Hides the functional interface [scala.Function2]
 * @see scala.Function2
 */
inline fun <T0, T1, R> Function2(crossinline block: (T0, T1)->R): Function2<T0, T1, R> =
        object : AbstractFunction2<T0, T1, R>() {
            override fun apply(p0: T0, p1: T1): R = block(p0, p1)
        }

/**
 * Hides the functional interface [scala.Function3]
 * @see scala.Function3
 */
inline fun <T0, T1, T2, R> Function3(crossinline block: (T0, T1, T2)->R): Function3<T0, T1, T2, R> =
        object : AbstractFunction3<T0, T1, T2, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2): R = block(p0, p1, p2)
        }

/**
 * Hides the functional interface [scala.Function4]
 * @see scala.Function4
 */
inline fun <T0, T1, T2, T3, R> Function4(crossinline block: (T0, T1, T2, T3)->R): Function4<T0, T1, T2, T3, R> =
        object : AbstractFunction4<T0, T1, T2, T3, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3): R = block(p0, p1, p2, p3)
        }

/**
 * Hides the functional interface [scala.Function5]
 * @see scala.Function5
 */
inline fun <T0, T1, T2, T3, T4, R> Function5(crossinline block: (T0, T1, T2, T3, T4)->R): Function5<T0, T1, T2, T3, T4, R> =
        object : AbstractFunction5<T0, T1, T2, T3, T4, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4): R = block(p0, p1, p2, p3, p4)
        }

/**
 * Hides the functional interface [scala.Function6]
 * @see scala.Function6
 */
inline fun <T0, T1, T2, T3, T4, T5, R> Function6(crossinline block: (T0, T1, T2, T3, T4, T5)->R): Function6<T0, T1, T2, T3, T4, T5, R> =
        object : AbstractFunction6<T0, T1, T2, T3, T4, T5, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): R = block(p0, p1, p2, p3, p4, p5)
        }

/**
 * Hides the functional interface [scala.Function7]
 * @see scala.Function7
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, R> Function7(crossinline block: (T0, T1, T2, T3, T4, T5, T6)->R): Function7<T0, T1, T2, T3, T4, T5, T6, R> =
        object : AbstractFunction7<T0, T1, T2, T3, T4, T5, T6, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6): R = block(p0, p1, p2, p3, p4, p5, p6)
        }

/**
 * Hides the functional interface [scala.Function8]
 * @see scala.Function8
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, R> Function8(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7)->R): Function8<T0, T1, T2, T3, T4, T5, T6, T7, R> =
        object : AbstractFunction8<T0, T1, T2, T3, T4, T5, T6, T7, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7): R = block(p0, p1, p2, p3, p4, p5, p6, p7)
        }

/**
 * Hides the functional interface [scala.Function9]
 * @see scala.Function9
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, R> Function9(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8)->R): Function9<T0, T1, T2, T3, T4, T5, T6, T7, T8, R> =
        object : AbstractFunction9<T0, T1, T2, T3, T4, T5, T6, T7, T8, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8)
        }

/**
 * Hides the functional interface [scala.Function10]
 * @see scala.Function10
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Function10(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9)->R): Function10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> =
        object : AbstractFunction10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9)
        }

/**
 * Hides the functional interface [scala.Function11]
 * @see scala.Function11
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> Function11(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)->R): Function11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> =
        object : AbstractFunction11<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
        }

/**
 * Hides the functional interface [scala.Function12]
 * @see scala.Function12
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> Function12(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)->R): Function12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> =
        object : AbstractFunction12<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11)
        }

/**
 * Hides the functional interface [scala.Function13]
 * @see scala.Function13
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> Function13(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)->R): Function13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> =
        object : AbstractFunction13<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
        }

/**
 * Hides the functional interface [scala.Function14]
 * @see scala.Function14
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> Function14(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)->R): Function14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> =
        object : AbstractFunction14<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)
        }

/**
 * Hides the functional interface [scala.Function15]
 * @see scala.Function15
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> Function15(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)->R): Function15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> =
        object : AbstractFunction15<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13, p14: T14): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)
        }

/**
 * Hides the functional interface [scala.Function16]
 * @see scala.Function16
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> Function16(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)->R): Function16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> =
        object : AbstractFunction16<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13, p14: T14, p15: T15): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)
        }

/**
 * Hides the functional interface [scala.Function17]
 * @see scala.Function17
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> Function17(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)->R): Function17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> =
        object : AbstractFunction17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13, p14: T14, p15: T15, p16: T16): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16)
        }

/**
 * Hides the functional interface [scala.Function18]
 * @see scala.Function18
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> Function18(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)->R): Function18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> =
        object : AbstractFunction18<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13, p14: T14, p15: T15, p16: T16, p17: T17): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17)
        }

/**
 * Hides the functional interface [scala.Function19]
 * @see scala.Function19
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> Function19(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)->R): Function19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> =
        object : AbstractFunction19<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13, p14: T14, p15: T15, p16: T16, p17: T17, p18: T18): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18)
        }

/**
 * Hides the functional interface [scala.Function20]
 * @see scala.Function20
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> Function20(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)->R): Function20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> =
        object : AbstractFunction20<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13, p14: T14, p15: T15, p16: T16, p17: T17, p18: T18, p19: T19): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19)
        }

/**
 * Hides the functional interface [scala.Function21]
 * @see scala.Function21
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> Function21(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)->R): Function21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> =
        object : AbstractFunction21<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13, p14: T14, p15: T15, p16: T16, p17: T17, p18: T18, p19: T19, p20: T20): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20)
        }

/**
 * Hides the functional interface [scala.Function22]
 * @see scala.Function22
 */
inline fun <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> Function22(crossinline block: (T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)->R): Function22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> =
        object : AbstractFunction22<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R>() {
            override fun apply(p0: T0, p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7, p8: T8, p9: T9, p10: T10, p11: T11, p12: T12, p13: T13, p14: T14, p15: T15, p16: T16, p17: T17, p18: T18, p19: T19, p20: T20, p21: T21): R = block(p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21)
        }


