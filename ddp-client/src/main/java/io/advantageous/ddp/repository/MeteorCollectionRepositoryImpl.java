/*
 * Copyright (C) 2014. Geoffrey Chandler.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.advantageous.ddp.repository;

import io.advantageous.ddp.rpc.RPCClient;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides mutating methods for Meteor collections.
 *
 * @author geoffc@gmail.com
 * @since 3/15/14 at 3:26 PM.
 */
public class MeteorCollectionRepositoryImpl implements MeteorCollectionRepository {

    private RPCClient rpcClient;

    @Inject
    public MeteorCollectionRepositoryImpl(final RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void delete(final String collectionName,
                       final String docId) throws IOException {
        this.delete(collectionName, docId, null, null);
    }

    @Override
    public void delete(final String collectionName,
                       final String docId,
                       final RPCClient.SuccessHandler<Object> onSuccess,
                       final RPCClient.FailureHandler onFailure) throws IOException {

        final Map<String, Object> selector = new HashMap<>(1);
        selector.put("_id", docId);
        this.rpcClient.call("/" + collectionName + "/remove", new Object[]{selector}, onSuccess, onFailure);
    }

    @Override
    public void insert(final String collectionName,
                       final Map<String, Object> insertParams,
                       final RPCClient.SuccessHandler<Object> onSuccess,
                       final RPCClient.FailureHandler onFailure) throws IOException {

        this.rpcClient.call("/" + collectionName + "/insert", new Object[]{insertParams}, onSuccess, onFailure);
    }

    @Override
    public void update(final String collectionName, String docId,
                       final Map<String, Object> updateParams,
                       final RPCClient.SuccessHandler<Object> onSuccess,
                       final RPCClient.FailureHandler onFailure) throws IOException {

        final Map<String, Object> selector = new HashMap<>();
        selector.put("_id", docId);
        this.rpcClient.call("/" + collectionName + "/update", new Object[]{selector, updateParams}, onSuccess, onFailure);
    }
}
