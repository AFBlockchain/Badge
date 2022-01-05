package hk.edu.polyu.af.bc.badge.states

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.testing.core.TestIdentity
import org.junit.jupiter.api.Test

internal class BadgeClassTest {
    private val me: Party = TestIdentity.fresh("AFBlockchain").party

    @Test
    fun `can create badge class`() {
        val name = "Blockchain Associate"
        val description = "The badge recipient has successfully passed AFBlockchain courses"
        val badgeClass = BadgeClass(name, description, ByteArray(1),me, UniqueIdentifier())

        assertThat(badgeClass.name, equalTo(name))
        assertThat(badgeClass.description, equalTo(description))
    }
}