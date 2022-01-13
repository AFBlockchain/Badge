package hk.edu.polyu.af.bc.badge.contracts

import com.r3.corda.lib.tokens.contracts.NonFungibleTokenContract
import com.r3.corda.lib.tokens.contracts.commands.Create
import com.r3.corda.lib.tokens.contracts.commands.IssueTokenCommand
import hk.edu.polyu.af.bc.badge.identityA
import hk.edu.polyu.af.bc.badge.identityB
import hk.edu.polyu.af.bc.badge.ledgerService
import hk.edu.polyu.af.bc.badge.states.Assertion
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.UniqueIdentifier
import net.corda.testing.node.ledger
import org.junit.jupiter.api.Test
import java.util.*

internal class AssertionContractTest {
    @Test
    fun `PartyA issues an assertion to PartyB`() {
        ledgerService.ledger {
            // create a BadgeClass first
            val badgeClass = transaction {
                command(identityA.publicKey, Create())
                output( BadgeClassContract.ID, "badgeClass", BadgeClass("test", "test",ByteArray(1), identityA.party, UniqueIdentifier()))
                verifies()

            }.outputStates[0] as BadgeClass
            val calendar:Calendar=Calendar.getInstance()

            val assertion = Assertion(badgeClass.toPointer(), identityA.party, identityB.party, Date(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.DATE)),true,UniqueIdentifier())

            transaction {
                command(identityA.publicKey, IssueTokenCommand(assertion.issuedTokenType, listOf(0)))
                output(NonFungibleTokenContract.contractId, assertion)
                reference("badgeClass") // explicitly add the pointed-to state as a reference to avoid resolving during validation
                verifies()
            }

            verifies()
        }
    }
}