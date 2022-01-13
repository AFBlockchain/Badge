package hk.edu.polyu.af.bc.badge.contracts

import com.r3.corda.lib.tokens.contracts.commands.Create
import hk.edu.polyu.af.bc.badge.identityA
import hk.edu.polyu.af.bc.badge.ledgerService
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.UniqueIdentifier
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

internal class BadgeClassContractTest {
    @Test
    fun `can read contract class name`() {
        assertNotNull(BadgeClassContract.ID)
    }

    @Test
    fun `PartyA creates a BadgeClass`() {
        ledgerService.ledger {
            transaction {
                command(identityA.publicKey, Create())
                output(BadgeClassContract.ID, BadgeClass("test", "test", ByteArray(1),identityA.party, UniqueIdentifier()))
                verifies()
            }

            verifies()
        }
    }
}