/*
 * Copyright (C) 2004 - 2013 Brian McCallister
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
package org.jdbi.v3;

import org.jdbi.v3.Foreman;
import org.jdbi.v3.LongArgument;
import org.jdbi.v3.ShortArgument;
import org.jdbi.v3.StringArgument;
import org.jdbi.v3.tweak.Argument;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class TestForeman
{
    @Test
    public void testWaffling()
    {
        final Foreman foreman = new Foreman();

        final Argument longArgument = foreman.waffle(Object.class, new Long(3L), null);
        assertSame(LongArgument.class, longArgument.getClass());

        final Argument shortArgument = foreman.waffle(Object.class, (short) 2000, null);
        assertSame(ShortArgument.class, shortArgument.getClass());

        final Argument stringArgument = foreman.waffle(Object.class, "I am a String!", null);
        assertSame(StringArgument.class, stringArgument.getClass());
    }

    @Test
    public void testExplicitWaffling()
    {
        final Foreman foreman = new Foreman();

        final Argument longArgument = foreman.waffle(Long.class, new Long(3L), null);
        assertSame(LongArgument.class, longArgument.getClass());

        final Argument shortArgument = foreman.waffle(short.class, (short) 2000, null);
        assertSame(ShortArgument.class, shortArgument.getClass());

        final Argument stringArgument = foreman.waffle(String.class, "I am a String!", null);
        assertSame(StringArgument.class, stringArgument.getClass());
    }

}