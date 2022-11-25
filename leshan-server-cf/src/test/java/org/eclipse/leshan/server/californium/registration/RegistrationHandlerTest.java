/*******************************************************************************
 * Copyright (c) 2022 Sierra Wireless and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.server.californium.registration;

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.leshan.core.link.DefaultLinkParser;
import org.eclipse.leshan.core.link.LinkParseException;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.core.request.Identity;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.request.UpdateRequest;
import org.eclipse.leshan.core.request.UplinkRequest;
import org.eclipse.leshan.server.registration.RandomStringRegistrationIdProvider;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationHandler;
import org.eclipse.leshan.server.registration.RegistrationServiceImpl;
import org.eclipse.leshan.server.registration.RegistrationStore;
import org.eclipse.leshan.server.security.Authorization;
import org.eclipse.leshan.server.security.Authorizer;
import org.junit.Before;
import org.junit.Test;

public class RegistrationHandlerTest {

    private RegistrationHandler registrationHandler;
    private RegistrationStore registrationStore;
    private TestAuthorizer authorizer;

    @Before
    public void setUp() throws UnknownHostException {
        authorizer = new TestAuthorizer();
        registrationStore = new InMemoryRegistrationStore();
        registrationHandler = new RegistrationHandler(new RegistrationServiceImpl(registrationStore), authorizer,
                new RandomStringRegistrationIdProvider());
    }

    @Test
    public void test_application_data_from_authorizer() {

        // Prepare authorizer
        Map<String, String> appData = new HashMap<>();
        appData.put("key1", "value1");
        appData.put("key2", "value2");
        authorizer.willReturn(Authorization.approved(appData));

        // handle request
        registrationHandler.register(givenIdenity(), givenRegisterRequestWithEndpoint("myEndpoint"));

        // check result
        Registration registration = registrationStore.getRegistrationByEndpoint("myEndpoint");
        assertEquals(appData, registration.getApplicationData());

        // Prepare authorizer
        Map<String, String> updatedAppData = new HashMap<>();
        updatedAppData.put("key3", "value3");
        authorizer.willReturn(Authorization.approved(updatedAppData));

        // handle request
        registrationHandler.update(givenIdenity(), givenUpdateRequestWithID(registration.getId()));

        // check result
        registration = registrationStore.getRegistrationByEndpoint("myEndpoint");
        assertEquals(updatedAppData, registration.getApplicationData());

    }

    private Identity givenIdenity() {
        return Identity.unsecure(new InetSocketAddress(0));
    }

    private RegisterRequest givenRegisterRequestWithEndpoint(String endpoint) {
        try {
            return new RegisterRequest(endpoint, 3600l, "1.1", EnumSet.of(BindingMode.U), false, null,
                    new DefaultLinkParser().parseCoreLinkFormat("</1/0/1>,</2/1>,</3>".getBytes()), null);
        } catch (LinkParseException e) {
            throw new IllegalStateException(e);
        }
    }

    private UpdateRequest givenUpdateRequestWithID(String registrationID) {
        return new UpdateRequest(registrationID, null, null, null, null, null);
    }

    private static class TestAuthorizer implements Authorizer {

        private Authorization autorization;

        public void willReturn(Authorization authorization) {
            this.autorization = authorization;
        }

        @Override
        public Authorization isAuthorized(UplinkRequest<?> request, Registration registration,
                Identity senderIdentity) {
            return autorization;
        }
    }
}
