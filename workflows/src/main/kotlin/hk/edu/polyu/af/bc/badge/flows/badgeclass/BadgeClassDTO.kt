package hk.edu.polyu.af.bc.badge.flows.badgeclass

import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party


/**
 * This DTO Class is used to store the imformation of a BadgeClass.
 * For not changed data it contains null.
 *
 */
class BadgeClassDTO (
    var name: String?,
    var description: String?,
    var image: ByteArray?,
    val issuer: Party,
    val linearId: UniqueIdentifier)