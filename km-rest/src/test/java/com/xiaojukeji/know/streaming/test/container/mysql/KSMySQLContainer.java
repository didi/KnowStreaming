package com.xiaojukeji.know.streaming.test.container.mysql;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.delegate.DatabaseDelegate;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.utility.DockerImageName;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author richardnorth
 * @see org.testcontainers.containers.MySQLContainer
 */
public class KSMySQLContainer<SELF extends KSMySQLContainer<SELF>> extends JdbcDatabaseContainer<SELF> {
    private static final ILog LOGGER = LogFactory.getLog(KSMySQLContainer.class);

    public static final String NAME = "mysql";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("mysql");

    @Deprecated
    public static final String DEFAULT_TAG = "5.7.34";

    @Deprecated
    public static final String IMAGE = DEFAULT_IMAGE_NAME.getUnversionedPart();

    static final String DEFAULT_USER = "test";

    static final String DEFAULT_PASSWORD = "test";

    private static final String MY_CNF_CONFIG_OVERRIDE_PARAM_NAME = "TC_MY_CNF";

    public static final Integer MYSQL_PORT = 3306;

    private String databaseName = "test";

    private String username = DEFAULT_USER;

    private String password = DEFAULT_PASSWORD;

    private static final String MYSQL_ROOT_USER = "root";

    private List<Tuple<String, String>> initScriptPathAndContentList = new ArrayList<>();

    /**
     * @deprecated use {@link MySQLContainer(DockerImageName)} instead
     */
    @Deprecated
    public KSMySQLContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    public KSMySQLContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    public KSMySQLContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);

        addExposedPort(MYSQL_PORT);
    }

    /**
     * @return the ports on which to check if the container is ready
     * @deprecated use {@link #getLivenessCheckPortNumbers()} instead
     */
    @NotNull
    @Override
    @Deprecated
    protected Set<Integer> getLivenessCheckPorts() {
        return super.getLivenessCheckPorts();
    }

    @Override
    protected void configure() {
        optionallyMapResourceParameterAsVolume(
                MY_CNF_CONFIG_OVERRIDE_PARAM_NAME,
                "/etc/mysql/conf.d",
                "mysql-default-conf"
        );

        addEnv("MYSQL_DATABASE", databaseName);
        if (!MYSQL_ROOT_USER.equalsIgnoreCase(username)) {
            addEnv("MYSQL_USER", username);
        }
        if (password != null && !password.isEmpty()) {
            addEnv("MYSQL_PASSWORD", password);
            addEnv("MYSQL_ROOT_PASSWORD", password);
        } else if (MYSQL_ROOT_USER.equalsIgnoreCase(username)) {
            addEnv("MYSQL_ALLOW_EMPTY_PASSWORD", "yes");
        } else {
            throw new ContainerLaunchException("Empty password can be used only with the root user");
        }
        setStartupAttempts(3);
    }

    @Override
    public String getDriverClassName() {
        // KS改动的地方
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            return "org.mariadb.jdbc.Driver";
        } catch (ClassNotFoundException e) {
            return "org.mariadb.jdbc.Driver";
        }
    }

    @Override
    public String getJdbcUrl() {
        String additionalUrlParams = constructUrlParameters("?", "&");

        // KS改动的地方
        return "jdbc:mariadb://" + getHost() + ":" + getMappedPort(MYSQL_PORT) + "/" + databaseName + additionalUrlParams;
    }

    @Override
    protected String constructUrlForConnection(String queryString) {
        String url = super.constructUrlForConnection(queryString);

        if (!url.contains("useSSL=")) {
            String separator = url.contains("?") ? "&" : "?";
            url = url + separator + "useSSL=false";
        }

        if (!url.contains("allowPublicKeyRetrieval=")) {
            url = url + "&allowPublicKeyRetrieval=true";
        }

        return url;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1";
    }

    public SELF withConfigurationOverride(String s) {
        parameters.put(MY_CNF_CONFIG_OVERRIDE_PARAM_NAME, s);
        return self();
    }

    @Override
    public SELF withDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
        return self();
    }

    @Override
    public SELF withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(final String password) {
        this.password = password;
        return self();
    }

    public SELF addInitScriptPathAndContent(String initScriptPath, String initScriptContent) {
        initScriptPathAndContentList.add(new Tuple<>(initScriptPath, initScriptContent));
        return self();
    }

    // KS改动的地方
    @Override
    public DatabaseDelegate getDatabaseDelegate() {
        return new JdbcDatabaseDelegate(this, "");
    }

    @Override
    protected void runInitScriptIfRequired() {
        if (initScriptPathAndContentList.isEmpty()) {
            return;
        }

        for (Tuple<String, String> elem: initScriptPathAndContentList) {
            try {
                ScriptUtils.executeDatabaseScript(this.getDatabaseDelegate(), elem.getV1(), elem.getV2());
            } catch (ScriptException var5) {
                LOGGER.error("Error while executing init script: {}", elem.getV1(), var5);
                throw new ScriptUtils.UncategorizedScriptException("Error while executing init script: " + elem.getV2(), var5);
            }
        }
    }
}
