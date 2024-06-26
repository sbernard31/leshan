/*******************************************************************************
 * Copyright (c) 2022    Sierra Wireless and others.
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
package org.eclipse.leshan.core.link.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.leshan.core.LwM2m.Version;
import org.eclipse.leshan.core.link.lwm2m.attributes.Attachment;
import org.eclipse.leshan.core.link.lwm2m.attributes.LwM2mAttribute;
import org.eclipse.leshan.core.link.lwm2m.attributes.LwM2mAttributes;
import org.junit.jupiter.api.Test;

public class AttributeTest {

    @Test
    public void should_pick_correct_model() {
        LwM2mAttribute<Version> verAttribute = LwM2mAttributes.create(LwM2mAttributes.OBJECT_VERSION,
                new Version("1.0"));
        assertEquals("ver", verAttribute.getName());
        assertEquals(new Version("1.0"), verAttribute.getValue());
        assertTrue(verAttribute.canBeAttachedTo(Attachment.OBJECT));
        assertFalse(verAttribute.isWritable());
    }

    @Test
    public void should_throw_on_pmin_lesser_than_zero() {
        // The minimum period MUST be greater than zero
        // https://datatracker.ietf.org/doc/html/draft-ietf-core-dynlink-07#section-4.1

        assertThrowsExactly(IllegalArgumentException.class, () -> {
            LwM2mAttributes.create(LwM2mAttributes.MINIMUM_PERIOD, -1L);
        });
    }

    @Test
    public void should_throw_on_pmax_lesser_than_zero() {
        // The maximum period MUST be greater than zero
        // https://datatracker.ietf.org/doc/html/draft-ietf-core-dynlink-07#section-4.2
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            LwM2mAttributes.create(LwM2mAttributes.MAXIMUM_PERIOD, -1L);
        });
    }

    @Test
    public void should_throw_on_epmin_lesser_than_zero() {
        // The Minimum Evaluation Period MUST be greater than zero
        // https://datatracker.ietf.org/doc/html/draft-ietf-core-conditional-attributes-06#section-3.2.3
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            LwM2mAttributes.create(LwM2mAttributes.EVALUATE_MINIMUM_PERIOD, -1L);
        });
    }

    @Test
    public void should_throw_on_epmax_lesser_than_zero() {
        // The Maximum Evaluation Period MUST be greater than zero
        // https://datatracker.ietf.org/doc/html/draft-ietf-core-conditional-attributes-06#section-3.2.4
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            LwM2mAttributes.create(LwM2mAttributes.EVALUATE_MAXIMUM_PERIOD, -1L);
        });
    }

    @Test
    public void should_throw_on_step_lesser_than_zero() {
        // The Maximum Evaluation Period MUST be greater than zero
        // https://datatracker.ietf.org/doc/html/draft-ietf-core-conditional-attributes-06#section-3.2.4
        assertThrowsExactly(IllegalArgumentException.class, () -> {
            LwM2mAttributes.create(LwM2mAttributes.STEP, -1D);
        });
    }
}
