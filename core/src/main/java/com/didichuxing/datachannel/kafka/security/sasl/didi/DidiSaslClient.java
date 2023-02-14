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

import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import java.io.UnsupportedEncodingException;

public class DidiSaslClient implements SaslClient{

    public static final String MECHANISM = "DIDI";

    private boolean completed = false;
    private byte[] pw;
    private String authorizationID;
    private String authenticationID;
    private static byte SEP = 0;

    DidiSaslClient(String var1, String var2, byte[] var3) throws SaslException {
        if (var2 != null && var3 != null) {
            this.authorizationID = var1;
            this.authenticationID = var2;
            this.pw = var3;
        } else {
            throw new SaslException("DIDI: authorization ID and password must be specified");
        }
    }

    public String getMechanismName() {
        return MECHANISM;
    }

    public boolean hasInitialResponse() {
        return true;
    }

    public void dispose() throws SaslException {
        this.clearPassword();
    }

    public byte[] evaluateChallenge(byte[] var1) throws SaslException {
        if (this.completed) {
            throw new IllegalStateException("DIDI authentication already completed");
        } else {
            this.completed = true;

            try {
                byte[] var2 = this.authorizationID != null ? this.authorizationID.getBytes("UTF8") : null;
                byte[] var3 = this.authenticationID.getBytes("UTF8");
                byte[] var4 = new byte[this.pw.length + var3.length + 2 + (var2 == null ? 0 : var2.length)];
                int var5 = 0;
                if (var2 != null) {
                    System.arraycopy(var2, 0, var4, 0, var2.length);
                    var5 = var2.length;
                }

                var4[var5++] = SEP;
                System.arraycopy(var3, 0, var4, var5, var3.length);
                var5 += var3.length;
                var4[var5++] = SEP;
                System.arraycopy(this.pw, 0, var4, var5, this.pw.length);
                this.clearPassword();
                return var4;
            } catch (UnsupportedEncodingException var6) {
                throw new SaslException("Cannot get UTF-8 encoding of ids", var6);
            }
        }
    }

    public boolean isComplete() {
        return this.completed;
    }

    public byte[] unwrap(byte[] var1, int var2, int var3) throws SaslException {
        if (this.completed) {
            throw new SaslException("DIDI supports neither integrity nor privacy");
        } else {
            throw new IllegalStateException("DIDI authentication not completed");
        }
    }

    public byte[] wrap(byte[] var1, int var2, int var3) throws SaslException {
        if (this.completed) {
            throw new SaslException("DIDI supports neither integrity nor privacy");
        } else {
            throw new IllegalStateException("DIDI authentication not completed");
        }
    }

    public Object getNegotiatedProperty(String var1) {
        if (this.completed) {
            return var1.equals("javax.security.sasl.qop") ? "auth" : null;
        } else {
            throw new IllegalStateException("PLAIN authentication not completed");
        }
    }

    private void clearPassword() {
        if (this.pw != null) {
            for(int var1 = 0; var1 < this.pw.length; ++var1) {
                this.pw[var1] = 0;
            }

            this.pw = null;
        }

    }

    protected void finalize() {
        this.clearPassword();
    }
}
