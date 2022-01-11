package hk.edu.polyu.af.bc.badge.states

import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.testing.core.TestIdentity
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class AssertionTest {
    private val me: Party = TestIdentity.fresh("AFBlockchain").party
    private val badgeClass = BadgeClass("A Badge", "Cool", ByteArray(1),me, UniqueIdentifier())

    @Test
    fun `can create an assertion`() {
        val assertion = Assertion(badgeClass.toPointer(), me, me, 1243123,true,UniqueIdentifier())

        assertEquals(assertion.holder, me)
        assertEquals(assertion.issuer, me)
        assertEquals(assertion.issuedTokenType.tokenType.tokenClass, BadgeClass::class.java)
    }
}