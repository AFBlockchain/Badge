package hk.edu.polyu.af.bc.badge;

import hk.edu.polyu.af.bc.badge.flows.assertion.IssueAssertion;
import hk.edu.polyu.af.bc.badge.flows.badgeclass.CreateBadgeClass;
import hk.edu.polyu.af.bc.badge.states.Assertion;
import hk.edu.polyu.af.bc.badge.states.BadgeClass;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Testcontainers
public class DockerizedTest {
    public static final String IMAGE_NAME="corda-four-nodes:0.1.1"; // TODO: change reference to docker hub instead of locally-built image

    private final static Logger logger = LoggerFactory.getLogger(DockerizedTest.class);

    @Container
    @SuppressWarnings("rawtypes")
    public static GenericContainer network = new GenericContainer(DockerImageName.parse(IMAGE_NAME))
            .withFileSystemBind(getCordappPath(), "/nodes/Notary/cordapps", BindMode.READ_WRITE)
            .withFileSystemBind(getCordappPath(), "/nodes/PartyA/cordapps", BindMode.READ_WRITE)
            .withFileSystemBind(getCordappPath(), "/nodes/PartyB/cordapps", BindMode.READ_WRITE)
            .withFileSystemBind(getCordappPath(), "/nodes/PartyC/cordapps", BindMode.READ_WRITE)
            .withExposedPorts(10010, 10011, 10012, 10013)
            .withStartupTimeout(Duration.ofSeconds(180));

    private static String host;
    private static Map<String, Integer> portMap;

    private static Map<String, BadgeClass> badgeClasses = new HashMap<>();

    @BeforeAll
    public static void setUp() {
        host = network.getHost();
        portMap = new HashMap<>();

        portMap.put("Notary", network.getMappedPort(10010));
        portMap.put("PartyA", network.getMappedPort(10011));
        portMap.put("PartyB", network.getMappedPort(10012));
        portMap.put("PartyC", network.getMappedPort(10013));
    }

    @Test
    @Order(1)
    public void flowsShouldBeRegistered() {
        CordaRPCConnection connection = getConnection(host, portMap.get("PartyA"));
        CordaRPCOps proxy = connection.getProxy();
        String flows = proxy.registeredFlows().toString();

        logger.info("Registered flows {}", flows);

        assert flows.contains("IssueAssertion");
        assert flows.contains("CreateBadgeClass");

        connection.close();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PartyA", "PartyB", "PartyC"})
    @Order(2)
    public void canCreateBadgeClass(String node) throws ExecutionException, InterruptedException {
        logger.info("Creating BadgeClass for node: {}", node);

        CordaRPCConnection connection = getConnection(host, portMap.get(node));
        CordaRPCOps proxy = connection.getProxy();

        SignedTransaction tx = proxy.startFlowDynamic(CreateBadgeClass.class, "test", "test").getReturnValue().get();
        logger.info("Transaction: {}", tx.toString());
        BadgeClass badgeClass = tx.getCoreTransaction().outputsOfType(BadgeClass.class).get(0);
        badgeClasses.put(node, badgeClass);
        logger.info("BadgeClass: {}", badgeClass.toString());

        assert proxy.vaultQuery(BadgeClass.class).getStates().stream().anyMatch(badgeClassStateAndRef ->
                badgeClassStateAndRef.getState().getData().getLinearId().equals(badgeClass.getLinearId()));
    }

    @ParameterizedTest
    @CsvSource({"PartyA, PartyB",
            "PartyB, PartyC",
            "PartyC, PartyA"})
    @Order(3)
    public void canIssueAssertion(String issuer, String recipient) throws ExecutionException, InterruptedException {
        logger.info("Issuing Assertion from {} to {}", issuer, recipient);

        CordaRPCConnection issConnection = getConnection(host, portMap.get(issuer));
        CordaRPCConnection recConnection = getConnection(host, portMap.get(recipient));
        CordaRPCOps issProxy = issConnection.getProxy();
        CordaRPCOps recProxy = recConnection.getProxy();

        BadgeClass createdBadgeClass = badgeClasses.get(issuer);
        SignedTransaction tx = issProxy.startFlowDynamic(IssueAssertion.class, createdBadgeClass.toPointer(BadgeClass.class),
                recProxy.nodeInfo().getLegalIdentities().get(0)).getReturnValue().get();
        logger.info("Transaction: {}", tx.toString());
        Assertion assertion = tx.getCoreTransaction().outputsOfType(Assertion.class).get(0);
        logger.info("Assertion: {}", assertion.toString());

        int waitTime = 1;
        logger.info("Waiting {}s for vaults to reflect changes", waitTime);
        TimeUnit.SECONDS.sleep(waitTime);

        List<StateAndRef<Assertion>> issVault = issProxy.vaultQuery(Assertion.class).getStates();
        List<StateAndRef<Assertion>> recVault = recProxy.vaultQuery(Assertion.class).getStates();
        logger.info("Issuer's vault: " + issVault.toString());
        logger.info("Recipient's vault: " + recVault.toString());

        logger.info("Checking that issuer's vault has the Assertion state");
        assert issProxy.vaultQuery(Assertion.class).getStates().stream().anyMatch(assertionStateAndRef ->
                assertionStateAndRef.getState().getData().getLinearId().equals(assertion.getLinearId()));
        logger.info("Checking that recipient's vault has the Assertion state");
        assert recProxy.vaultQuery(Assertion.class).getStates().stream().anyMatch(assertionStateAndRef ->
                assertionStateAndRef.getState().getData().getLinearId().equals(assertion.getLinearId()));
    }


    public static CordaRPCConnection getConnection(String host, int port) {
        NetworkHostAndPort networkHostAndPort = new NetworkHostAndPort(host, port);
        CordaRPCClient client = new CordaRPCClient(networkHostAndPort);

        return client.start("user1", "test");
    }

    public static String getCordappPath() {
        Path projectBase = Paths.get(System.getProperty("user.dir")).getParent();
        Path appRel = Paths.get("cordapps");
        Path appAbs = projectBase.resolve(appRel);

        assert appAbs.toFile().exists();

        return appAbs.toString();
    }
}
