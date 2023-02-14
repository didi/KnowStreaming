/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.didichuxing.datachannel.kafka.security.sasl.didi;

import javax.security.auth.callback.*;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import java.io.IOException;
import java.util.Map;


public class DidiSaslClientFactory implements SaslClientFactory {

    @Override
    public SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName, Map<String, ?> props, CallbackHandler cbh) throws SaslException {
        if (mechanisms.length == 1 && mechanisms[0].equals("DIDI")) {
            Object[] paras = this.getUserInfo("DIDI", authorizationId, cbh);
            return new DidiSaslClient(authorizationId, (String)paras[0], ((String)paras[1]).getBytes());
        } else {
            throw new SaslException("DidiSaslClientFactory only support mechanism:DIDI");
        }
    }

    @Override
    public String[] getMechanismNames(Map<String, ?> props) {
        return new String[]{"DIDI"};
    }

    private Object[] getUserInfo(String mechanism, String authorizationID, CallbackHandler callback) throws SaslException {
        if (callback == null) {
            throw new SaslException("Callback handler to get username/password required");
        } else {
            try {
                String namePrompt = mechanism + " authentication id: ";
                String passwordPrompt = mechanism + " password: ";
                NameCallback nameCallBack = authorizationID == null ? new NameCallback(namePrompt) : new NameCallback(namePrompt, authorizationID);
                PasswordCallback passwdCallBack = new PasswordCallback(passwordPrompt, false);
                callback.handle(new Callback[]{nameCallBack, passwdCallBack});
                char[] pwdBytes = passwdCallBack.getPassword();
                String password = null;
                if (pwdBytes != null) {
                    password = new String(pwdBytes);
                    passwdCallBack.clearPassword();
                }

                String username = nameCallBack.getName();
                return new Object[]{username, password};
            } catch (IOException var11) {
                throw new SaslException("Cannot get password", var11);
            } catch (UnsupportedCallbackException var12) {
                throw new SaslException("Cannot get userid/password", var12);
            }
        }
    }
}
