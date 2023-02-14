/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.kafka.security.login;

import com.alibaba.fastjson.JSONObject;

public class User {

    private String username;
    private String password;
    private boolean superUser;

    public User() {
    }

    public User(String username, String password, boolean superUser) {
        this.username = username;
        this.password = password;
        this.superUser = superUser;
    }

    public User(JSONObject json) {
        String username = json.getString("username");
        if (username == null || username.equals("")) {
            throw new IllegalArgumentException("missing username");
        }

        String password = json.getString("password");
        if (password == null || password.equals("")) {
            throw new IllegalArgumentException("missing password");
        }
        Integer userType = json.getInteger("userType");
        if (userType == null) {
            throw new IllegalArgumentException("missing user type");
        }
        this.username = username;
        this.password = password;
        this.superUser = userType != 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSuperUser() {
        return superUser;
    }

    @Override
    public boolean equals(Object obj) {
        User user = (User) obj;
        return username.equals(user.username) &&
                password.equals(user.password) &&
                superUser == user.superUser;
    }

    @Override
    public String toString() {
        return String.format("username: %s, password: %s, superuser: %b", username, password, superUser);
    }
}
