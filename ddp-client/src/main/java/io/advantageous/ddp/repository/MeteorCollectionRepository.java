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

import java.io.IOException;
import java.util.Map;

/**
 * description
 *
 * @author geoffc@gmail.com
 * @since 4/30/14 at 5:24 PM.
 */
public interface MeteorCollectionRepository {
    void delete(String collectionName,
                String docId) throws IOException;

    void delete(String collectionName,
                String docId,
                RPCClient.SuccessHandler<Object> onSuccess,
                RPCClient.FailureHandler onFailure) throws IOException;

    void insert(String collectionName,
                Map<String, Object> insertParams,
                RPCClient.SuccessHandler<Object> onSuccess,
                RPCClient.FailureHandler onFailure) throws IOException;

    void update(String collectionName, String docId,
                Map<String, Object> updateParams,
                RPCClient.SuccessHandler<Object> onSuccess,
                RPCClient.FailureHandler onFailure) throws IOException;
}
