package hk.edu.polyu.af.bc.badge.states

import com.r3.corda.lib.tokens.contracts.NonFungibleTokenContract
import com.r3.corda.lib.tokens.contracts.states.NonFungibleToken
import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
import com.r3.corda.lib.tokens.contracts.types.TokenPointer
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.util.*

@BelongsToContract(NonFungibleTokenContract::class) // use generic NFT-Contract
class Assertion(
        private val badgeClassPointer: TokenPointer<BadgeClass>,
        override val issuer: Party, // TODO: issuer must be the same as the issuer in the badgeClass. Check this in contract
        private val recipient: AbstractParty,
        private val issuedOn: Long,
        private val revoked: Boolean,
        override val linearId: UniqueIdentifier

): NonFungibleToken(
        IssuedTokenType(issuer, badgeClassPointer),
        recipient,
        linearId
){
    override val participants: List<AbstractParty>
        get() = listOf(issuer, recipient) // default is only recipient
}