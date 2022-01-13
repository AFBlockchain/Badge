package hk.edu.polyu.af.bc.badge.flows

import hk.edu.polyu.af.bc.badge.states.Assertion
import net.corda.core.flows.FlowLogic

class RevokeAssertionFlow (val assertion: Assertion): FlowLogic<Unit>() {
    override fun call(): Unit {
        assertion.revoked=false
    }
}
