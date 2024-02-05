package mtest

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.spyk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

object A {
  fun test() {
    println("A test")
  }
}

fun getA() = A

class B(
  private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
  fun test() {
    println("B test")
    coroutineScope.launch {
      getA().test()
    }
  }
}

class TestM {

  @Test
  fun test() {
    runTest {
      mockkObject(A)
      every { A.test() } returns Unit
      spyk(B(this)).test()
    }
  }

}