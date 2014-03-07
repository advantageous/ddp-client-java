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

package io.advantageous.ddp.rpc;

import io.advantageous.ddp.DDPError;
import io.advantageous.ddp.DDPMessageEndpoint;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a client for Meteor's RPC protocol.
 *
 * @author geoffc@gmail.com
 * @since 1/18/14 at 12:55 AM.
 */
public class RPCClientImpl implements RPCClient {

    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    private static final Map<String, DeferredMethodInvocation> CALLBACK_MAP = new ConcurrentHashMap<>();

    private final DDPMessageEndpoint client;

    @Inject
    public RPCClientImpl(final DDPMessageEndpoint client) {
        this.client = client;
        this.client.registerHandler(ResultMessage.class, result -> {
            final DeferredMethodInvocation invocation = CALLBACK_MAP.get(result.getId());

            // Exit early if this message wasn't for us.
            if (invocation == null) return;
            final DDPError error = result.getError();
            if (error != null) {
                invocation.getFailureHandler().onFailure(error);
                CALLBACK_MAP.remove(result.getId());
                return;
            }
            invocation.setResult(result.getResult());
            invocation.setHasResult(true);
            invokeIfReady(invocation);
        });
        this.client.registerHandler(UpdatedMessage.class, updatedMessage -> {
            for (String thisId : updatedMessage.getMethods()) {
                final DeferredMethodInvocation invocation = CALLBACK_MAP.get(thisId);

                // Exit early if this message wasn't for us.
                if (invocation == null) continue;
                invocation.setHasUpdated(true);
                invokeIfReady(invocation);
            }
        });
    }

    private static void invokeIfReady(final DeferredMethodInvocation invocation) {
        if (invocation.getHasResult() && invocation.getHasUpdated()) {
            invocation.getSuccessHandler().onSuccess(invocation.getResult());
            CALLBACK_MAP.remove(invocation.getId());
        }
    }

    @Override
    public void call(final String methodName,
                     final Object[] params,
                     final SuccessHandler<Object> successHandler,
                     final FailureHandler failureHandler) throws IOException {

        final Long methodId = SEQUENCE.getAndIncrement();
        final String id = methodId.toString();
        CALLBACK_MAP.put(id, new DeferredMethodInvocation(id, successHandler, failureHandler));
        final MethodMessage message = new MethodMessage();
        message.setId(id);
        message.setMethod(methodName);
        message.setParams(params);
        client.send(message);
    }

    private static final class DeferredMethodInvocation {
        private String id;

        private SuccessHandler<Object> successHandler;

        private FailureHandler failureHandler;

        private Object result;

        private Boolean hasResult = false;

        private Boolean hasUpdated = false;

        private DeferredMethodInvocation(String id,
                                         SuccessHandler<Object> successHandler,
                                         FailureHandler failureHandler) {
            this.id = id;
            this.successHandler = successHandler;
            this.failureHandler = failureHandler;
        }

        public String getId() {
            return id;
        }

        public SuccessHandler<Object> getSuccessHandler() {
            return successHandler;
        }

        public FailureHandler getFailureHandler() {
            return failureHandler;
        }

        public Boolean getHasResult() {
            return hasResult;
        }

        public void setHasResult(Boolean hasResult) {
            this.hasResult = hasResult;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public Boolean getHasUpdated() {
            return hasUpdated;
        }

        public void setHasUpdated(Boolean hasUpdated) {
            this.hasUpdated = hasUpdated;
        }
    }

}
