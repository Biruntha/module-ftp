/*
 * Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.ftp.client.actions;

import org.ballerinalang.bre.Context;
import org.ballerinalang.ftp.util.FTPUtil;
import org.ballerinalang.ftp.util.FtpConstants;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.remotefilesystem.RemoteFileSystemConnectorFactory;
import org.wso2.transport.remotefilesystem.client.connector.contract.FtpAction;
import org.wso2.transport.remotefilesystem.client.connector.contract.VFSClientConnector;
import org.wso2.transport.remotefilesystem.exception.RemoteFileSystemConnectorException;
import org.wso2.transport.remotefilesystem.impl.RemoteFileSystemConnectorFactoryImpl;

import java.util.HashMap;
import java.util.Map;

import static org.ballerinalang.ftp.util.FtpConstants.FTP_PACKAGE_NAME;

/**
 * FTP create directory operation.
 */
@BallerinaFunction(
        orgName = "wso2",
        packageName = "ftp:0.0.0",
        functionName = "mkdir",
        receiver = @Receiver(type = TypeKind.OBJECT, structType = "Client", structPackage = FTP_PACKAGE_NAME)
)
public class Mkdir extends AbstractFtpAction {

    private static final Logger log = LoggerFactory.getLogger(Mkdir.class);

    @Override
    public void execute(Context context) {
        BMap<String, BValue> clientConnector = (BMap<String, BValue>) context.getRefArgument(0);
        String url = (String) clientConnector.getNativeData(FtpConstants.URL);
        String path = context.getStringArgument(0);
        Map<String, String> prop = (Map<String, String>) clientConnector.getNativeData(FtpConstants.PROPERTY_MAP);

        //Create property map to send to transport.
        Map<String, String> propertyMap = new HashMap<>(prop);
        propertyMap.put(FtpConstants.PROPERTY_URI, url + path);

        FTPClientConnectorListener connectorListener = new FTPClientConnectorListener(context);
        RemoteFileSystemConnectorFactory fileSystemConnectorFactory = new RemoteFileSystemConnectorFactoryImpl();
        VFSClientConnector connector;
        try {
            connector = fileSystemConnectorFactory.createVFSClientConnector(propertyMap, connectorListener);
        } catch (RemoteFileSystemConnectorException e) {
            context.setReturnValues(FTPUtil.createError(context, e.getMessage()));
            log.error(e.getMessage(), e);
            return;
        }
        connector.send(null, FtpAction.MKDIR);
    }
}
