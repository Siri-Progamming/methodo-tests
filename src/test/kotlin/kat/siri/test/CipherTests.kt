package kat.siri.test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.char.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class CipherTests : FunSpec({

    test("should return C"){
        val char = Cipher().cipher('A',2)
        char shouldBe 'C'
    }

    test("should throw Exception because char isnt good"){
        shouldThrow<Exception> {
            Cipher().cipher('-',2)
        }
    }

    test("should throw Exception because char isnt good (lowercase)"){
        shouldThrow<Exception> {
            Cipher().cipher('a',2)
        }
    }

    test("should throw Exception because key isn't good"){
        shouldThrow<Exception> {
            Cipher().cipher('A',-2)
        }
    }
    test("should return B because loop"){
        val char = Cipher().cipher('Z',2)
        char shouldBe 'B'
    }

//    test("cipher should always return a valid uppercase letter") {
//        checkAll<Char, Int> { a, b ->
//                val limitedB = b.coerceIn(-5, 50)
//            val char = Cipher().cipher(a, limitedB)
//            char shouldBeInRange 'A'..'Z'
//        }
//    }
})