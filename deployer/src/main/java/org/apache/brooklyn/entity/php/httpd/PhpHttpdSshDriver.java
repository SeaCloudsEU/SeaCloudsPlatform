/*
 * Copyright 2015 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.brooklyn.entity.php.httpd;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.brooklyn.entity.php.PhpWebAppSshDriver;
import org.apache.brooklyn.entity.software.base.AbstractSoftwareProcessSshDriver;
import org.apache.brooklyn.entity.software.base.lifecycle.ScriptHelper;
import org.apache.brooklyn.location.ssh.SshMachineLocation;
import org.apache.brooklyn.util.text.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhpHttpdSshDriver extends PhpWebAppSshDriver implements PhpHttpdDriver {

    private static final Logger LOG = LoggerFactory.getLogger(PhpHttpdSshDriver.class);

    public PhpHttpdSshDriver(PhpHttpdServerImpl entity, SshMachineLocation machine) {
        super(entity, machine);
    }

    @Override
    public PhpHttpdServerImpl getEntity() {
        return (PhpHttpdServerImpl) super.getEntity();
    }

    @Override
    protected Map<String, Integer> getPortMap() {
        return ImmutableMap.of("httpPort", getEntity().getHttpPort());
    }

    @Override
    protected String getDeploySubdir() {
        return "";
    }

    @Override
    public Integer getHttpPort() {
        return getEntity().getHttpPort();
    }

    @Override
    public String getRunDir() {
        return getEntity().getConfig(PhpHttpdServer.DEPLOY_RUN_DIR);
    }


    @Override
    public void install() {
        super.install();
        if (!isApacheInstalled()) {
            LOG.info("Apache was not installed in {}, proceeding to install it.", new Object[]{this});
            /*Maybe, we find a old apache configuration file which have to be removed to install the new
            * server without errors.*/
            removeOldConfigFile();
            installApacheServer();
        }
    }

    private boolean isApacheInstalled() {
        boolean apacheIsInstalled = false;
        int result = getMachine().execCommands("apacheInstalled", ImmutableList.of("apache2 -v"));
        if (result == 0)
            apacheIsInstalled = true;
        return apacheIsInstalled;
    }

    private void removeOldConfigFile() {
        String oldApacheConfigurationFilePath = getEntity().getConfigurationDir() + "/" + "apache2.conf";
        getMachine().execCommands("deleteOldApacheConfigFile", ImmutableList.of("rm -f" + oldApacheConfigurationFilePath));
    }

    private int installApacheServer() {
        int result;
        LOG.info("Installing Apache Server {}", new Object[]{getEntity()});
        List<String> commands = ImmutableList.<String>builder().add("sudo apt-get install -y --allow-unauthenticated apache2").build();
        result = newScript(INSTALLING).body.append(commands).execute();
        if (result != 0) {
            log.warn("Problem installing {} for {}: result {}", new Object[]{entity, result});
        } else {
            log.info("Installed {} for {} commands {}", new Object[]{result, entity, commands});
        }
        return result;
    }

    @Override
    public void customize() {
        ScriptHelper customizeScript = newScript(CUSTOMIZING)
                .body.append(
                        disableCurrentDeployRunDir(),
                        removeAllAvailableSites(),
                        createConfigurationFile(),
                        enableAvailableDeploymentRunDir(),
                        enableServerStatusServerModule(),
                        configureHttpPort(),
                        apacheService(ServiceCommands.RELOAD),
                        installPhp(),
                        apacheService(ServiceCommands.RESTART),
                        restartApacheServer()
                ).gatherOutput(true);
        customizeScript.execute();
        LOG.debug("Customizing Apache: \n{}", customizeScript.getResultStdout());
        getEntity().deployInitialApplications();
    }

    private String disableCurrentDeployRunDir() {
        StringBuilder stringBuilder = new StringBuilder();
        String result = String.format(
                "for file in %s%s/*.conf\n" +
                        "do\n" +
                        "FILENAME=$(basename $file)\n" +
                        "exec sudo a2dissite $FILENAME | true\n" +
                        "done\n" +
                        "\n",
                getEntity().getConfigurationDir(),
                getEntity().getSitesAvailableFolder());

        return result;
    }

    private String removeAllAvailableSites() {
        String result = String.format(
                "sudo rm %s%s/*\n",
                getEntity().getConfigurationDir(),
                getEntity().getSitesAvailableFolder());
        return result;
    }

    private String addAvailableSitesConfiguration(String targetName) {
        String result = String.format(
                "sudo bash -c 'cat <<EOT >> %s%s/%s\n" +
                        "#" + targetName + "\n" +
                        "<VirtualHost *:%s>\n" +
                        "DocumentRoot %s/" + targetName + "/\n" +
                        "ErrorLog ${APACHE_LOG_DIR} /error-" + targetName + ".log\n" +
                        "CustomLog ${APACHE_LOG_DIR} /access-" + targetName + ".log combined\n" +
                        "</VirtualHost>\n" +
                        "%s" +
                        "EOT'\n" +
                        "%s\n",
                getEntity().getConfigurationDir(),
                getEntity().getSitesAvailableFolder(),
                getEntity().getSiteConfigurationFile(),
                getEntity().getHttpPort(),
                getEntity().getDeployRunDir(),
                getSetEnvVariablesDeclaration(),
                changePermissionsOfFolder(getEntity().getDeployRunDir() + "/" + targetName));
        return result;
    }

    private String getSetEnvVariablesDeclaration() {
        String vaiablesDeclarations = Strings.EMPTY;
        for (Map.Entry<String, String> envEntry : getEntity().getPhpEnvVariables().entrySet()) {
            String envDeclaration =
                    getSetEnvVariableConfiguration(envEntry.getKey(), envEntry.getValue()) + "\n";
            vaiablesDeclarations = vaiablesDeclarations.concat(envDeclaration);
        }
        return vaiablesDeclarations;
    }

    private String getSetEnvVariableConfiguration(String envName, String envValue) {
        return "SetEnv " + envName + " " + envValue;
    }

    private String createConfigurationFile() {
        String result = String.format(
                "%s\n" + "sudo touch %s%s/%s",
                createFolderDeployRunDir(),
                getEntity().getConfigurationDir(),
                getEntity().getSitesAvailableFolder(),
                resolveConfigName(getEntity().getSiteConfigurationFile()));
        return result;
    }

    private String resolveConfigName(String inputName) {
        String result;
        if (Strings.isEmpty(inputName)) {
            result = getEntity().getAppName() + ".conf";
        } else if (!inputName.endsWith(".conf")) {
            result = inputName + ".conf";
        } else {
            result = inputName;
        }
        getEntity().setSiteConfigurationFile(result); // Updating the sensor
        return result;
    }

    private String createFolderDeployRunDir() {
        return "mkdir -p " + getRunDir() + "\n";
    }

    private String changePermissionsOfFolder(String folder) {
        return String.format("sudo chown -R %s:%s %s\n", getEntity().getDefaultGroup(), getEntity().getDefaultGroup(), folder);
    }

    private String enableAvailableDeploymentRunDir() {
        String result = String.format(
                "for file in %s%s/*.conf\n" +
                        "do\n" +
                        "FILENAME=$(basename $file)\n" +
                        "exec sudo a2ensite $FILENAME | true\n" +
                        "done\n" +
                        "%s\n",
                getEntity().getConfigurationDir(),
                getEntity().getSitesAvailableFolder(),
                apacheService(ServiceCommands.RELOAD));
        return result;
    }

    private String enableServerStatusServerModule() {
        String result;
        result = String.format(
                "sudo bash -c 'cat <<EOT >> %s/apache2.conf\n" +
                        "ExtendedStatus On\n" +
                        "<Location /server-status>\n" +
                        "SetHandler server-status\n" +
                        "Require all granted\n" +
                        "</Location>\n" +
                        "EOT'\n",
                getEntity().getConfigurationDir());
        return result;
    }

    //TODO the port configuration could be modified. So it is needed find Listen and change the port
    private String configureHttpPort() {
        String result;
        result = String.format(
                "sudo sed -i 's@Listen[ 0-9]\\+@Listen %s@g' %s/ports.conf",
                getEntity().getHttpPort(),
                getEntity().getConfigurationDir()
        );
        return result;
    }

    //TODO refactos using the strategy pattern
    private String installPhp() {
        log.debug("Installing PHP v", new Object[]{getEntity().getPhpVersion()});
        if (getEntity().getPhpVersion().equals("5.4")) {
            return instalPhp54v();
        } else {
            return installPhpSuggestedVersionByDefault();
        }
    }

    private String instalPhp54v() {
        String result = String.format(
                "sudo add-apt-repository -y ppa:ondrej/php5-oldstable" + "\n" +
                        "sudo apt-get update" + "\n" +
                        "%s",
                installPhpSuggestedVersionByDefault());
        return result;
    }


    private String installPhpSuggestedVersionByDefault() {
        String result = String.format(
                "sudo apt-get -y install php5" + "\n" +
                        "sudo apt-get -y install php5-mysql" + "\n");
        return result;
    }


    //TODO
    @Override
    public void launch() {
        //Now this method does not return any value but It should run the app using
        //the startup files passed in the server of the application
    }

    @Override
    public String deployGitResource(String url, String targetName) {
        super.deployGitResource(url, targetName);
        newScript(CUSTOMIZING)
                .body.append(
                addAvailableSitesConfiguration(targetName),
                apacheService(ServiceCommands.RELOAD),
                enableAvailableDeploymentRunDir())
                .execute();
        postDeploymentConfiguration(getEntity().getDeployRunDir() + "/" + targetName);
        return targetName;
    }

    @Override
    public String deployTarballResource(String url, String targetName) {
        super.deployTarballResource(url, targetName);
        newScript(CUSTOMIZING)
                .body.append(
                addAvailableSitesConfiguration(targetName),
                apacheService(ServiceCommands.RELOAD),
                enableAvailableDeploymentRunDir())
                .execute();
        postDeploymentConfiguration(getEntity().getDeployRunDir() + "/" + targetName);
        return targetName;
    }

    @Override
    public boolean isRunning() {
        boolean isApacheRunning = false;
        //int resultOfCommand = getMachine().execCommands("apacheIsRunning", ImmutableList.of("service apache2 status"));
        String command = "sudo service apache2 status";
        int resultOfCommand = newScript(AbstractSoftwareProcessSshDriver.CHECK_RUNNING)
                .body.append(command).execute();
        if (resultOfCommand == 0)
            isApacheRunning = true;
        return isApacheRunning;
    }

    //TODO merge with the stopApacheMethod in any way
    @Override
    public void stop() {
        String command = "sudo service apache2 stop";
        newScript(STOPPING)
                .body.append(command).execute();
    }

    @Override
    public void kill() {
        newScript(KILLING).execute();
    }

    private void postDeploymentConfiguration(String targetNameApplication) {
        String configFile = targetNameApplication + "/" + getEntity().getConfigurationFile();
        String configTemplate = targetNameApplication + "/" + getEntity().getConfigTemplate();
        if (!Strings.isEmpty(configTemplate)) {
            copyTemplateToFile(configTemplate, configFile);
            processPhpTemplate(configFile);
        } else if (!Strings.isEmpty(getEntity().getConfigurationFile())) {
            // If no template specified, we assume the target file to be modified directly
            processPhpTemplate(configFile);
        }
    }

    private void processPhpTemplate(String pathFile) {
        Map<String, String> databaseParameters = getEntity().getDbConnectionConfigParams();
        String command;
        Set<String> configurationParameters;
        if (databaseParameters != null) {
            configurationParameters = databaseParameters.keySet();
            log.debug("Template path {} ", new Object[]{pathFile});
            log.debug("Using {} parameters ", new Object[]{configurationParameters.size()});
            for (String configurationParameter : configurationParameters) {
                command = String.format(
                        "sudo sed -i.tmp 's/define([ ]*'\\''%s'\\''[ ]*,[ ]*'\\''[^']*'\\''[ ]*);/define('\\''%s'\\'' , '\\''%s'\\'');/g' %s",
                        configurationParameter,
                        configurationParameter,
                        escapeString(databaseParameters.get(configurationParameter)),
                        pathFile);
                log.debug("Executing replacing command: " + command);
                getMachine().execCommands("processingPhpConfigTemplate", ImmutableList.of(command));
            }
        }
    }

    private void copyTemplateToFile(String from, String to) {
        getMachine().execCommands("copying config template to file", ImmutableList.of("sudo cp " + from + " " + to));
    }

    private String escapeString(String s) {
        return s.replace("/", "\\/");
    }

    private String apacheService(String command) {
        return "sudo service apache2 " + command;
    }

    private String restartApacheServer() {
        return "/etc/init.d/apache2 restart";
    }

    public static class ServiceCommands {
        static String START = "start";
        static String STOP = "stop";
        static String RELOAD = "reload";
        static String RESTART = "restart";
    }
}