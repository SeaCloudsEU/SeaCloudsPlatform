/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package brooklyn.entity.cloudfoundry.services.sql.cleardb;


import brooklyn.entity.cloudfoundry.services.CloudFoundryServiceImpl;
import brooklyn.entity.cloudfoundry.services.PaasServiceCloudFoundryDriver;
import brooklyn.entity.cloudfoundry.webapp.CloudFoundryWebAppImpl;
import brooklyn.location.cloudfoundry.CloudFoundryPaasLocation;
import brooklyn.util.ResourceUtils;
import brooklyn.util.text.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

public class ClearDbServiceCloudFoundryDriver extends PaasServiceCloudFoundryDriver
        implements ClearDbServiceDriver{

    public static final Logger log = LoggerFactory
            .getLogger(ClearDbServiceCloudFoundryDriver.class);

    public ClearDbServiceCloudFoundryDriver(CloudFoundryServiceImpl entity,
                                            CloudFoundryPaasLocation location) {
        super(entity, location);
    }

    @Override
    public ClearDbServiceImpl getEntity() {
        return (ClearDbServiceImpl) super.getEntity();
    }

    @Override
    public void operation(CloudFoundryWebAppImpl app) {
        if(!Strings.isBlank(getEntity().getCreationScriptUrl())){
            executeCreationScript(app);
        }
    }

    public void executeCreationScript(CloudFoundryWebAppImpl app) {
        Connection con;
        Statement stmt;
        String DRIVER = "com.mysql.jdbc.Driver";

        try {
            Class.forName(DRIVER).newInstance();

            con = DriverManager.getConnection(createJDBCStringConnection(app));
            stmt = con.createStatement();
            String sqlContent;

            sqlContent = getContentResourceFromUrl(getEntity().getCreationScriptUrl());

            stmt.execute(sqlContent);
            stmt.close();
            con.close();
        } catch (Exception e) {
            throw new RuntimeException("Error during database creation in driver" + this +
                    " deploying service "+getEntity().getId());
        }
    }

    private String createJDBCStringConnection(CloudFoundryWebAppImpl app){
        Map<String, String > credentials = getEntity().getServiceCredentialsFromApp(app);
        return generateJDBCFromCredentials(credentials);
    }

    private String generateJDBCFromCredentials(Map<String, String> credentials){
        String hostName = credentials.get("hostname");
        String username = credentials.get("username");
        String port= credentials.get("port");
        String password = credentials.get("password");
        String name = credentials.get("name");

        String url = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s",
                hostName,
                port,
                name,
                username,
                password);
        return url;
    }

    private String getContentResourceFromUrl(String url){
        return new ResourceUtils(getEntity())
                .getResourceAsString(url);
    }


}
