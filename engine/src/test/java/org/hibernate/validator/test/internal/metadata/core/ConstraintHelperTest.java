/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.validator.test.internal.metadata.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.privilegedactions.SetAccessibility;
import org.hibernate.validator.test.internal.metadata.Engine;
import org.hibernate.validator.test.internal.metadata.Order;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Hardy Ferentschik
 */
public class ConstraintHelperTest {

	private static ConstraintHelper constraintHelper;

	@BeforeClass
	public static void init() {
		constraintHelper = new ConstraintHelper();
	}

	@Test
	public void testGetMultiValueConstraints() throws Exception {
		Engine engine = new Engine();
		Field[] fields = engine.getClass().getDeclaredFields();
		assertNotNull( fields );
		assertTrue( fields.length == 1 );
		run( SetAccessibility.action( fields[0] ));

		Annotation annotation = fields[0].getAnnotation( Pattern.List.class );
		assertNotNull( annotation );
		List<Annotation> multiValueConstraintAnnotations = constraintHelper.getMultiValueConstraints( annotation );
		assertTrue( multiValueConstraintAnnotations.size() == 2, "There should be two constraint annotations" );
		assertTrue( multiValueConstraintAnnotations.get( 0 ) instanceof Pattern, "Wrong constraint annotation" );
		assertTrue( multiValueConstraintAnnotations.get( 1 ) instanceof Pattern, "Wrong constraint annotation" );


		Order order = new Order();
		fields = order.getClass().getDeclaredFields();
		assertNotNull( fields );
		assertTrue( fields.length == 1 );
		run( SetAccessibility.action( fields[0] ));

		annotation = fields[0].getAnnotation( NotNull.class );
		assertNotNull( annotation );
		multiValueConstraintAnnotations = constraintHelper.getMultiValueConstraints( annotation );
		assertTrue( multiValueConstraintAnnotations.size() == 0, "There should be no constraint annotations" );
	}

	/**
	 * Runs the given privileged action, using a privileged block if required.
	 * <p>
	 * <b>NOTE:</b> This must never be changed into a publicly available method to avoid execution of arbitrary
	 * privileged actions within HV's protection domain.
	 */
	private static <T> T run(PrivilegedAction<T> action) {
		return System.getSecurityManager() != null ? AccessController.doPrivileged( action ) : action.run();
	}
}
