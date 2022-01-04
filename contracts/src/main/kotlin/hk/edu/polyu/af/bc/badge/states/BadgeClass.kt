package hk.edu.polyu.af.bc.badge.states

import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType
import hk.edu.polyu.af.bc.badge.contracts.BadgeClassContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import java.awt.image.BufferedImage

@BelongsToContract(BadgeClassContract::class)
class BadgeClass(var name: String,
                 var description: String,
                 var image: BufferedImage,
                 private val issuer: Party,
                 override val linearId: UniqueIdentifier) : EvolvableTokenType() {
    override val fractionDigits: Int
        get() = 0
    override val maintainers: List<Party>
        get() = listOf(issuer)
}