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

package io.advantageous.ddp;

import io.advantageous.ddp.JsonMessageConverter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class JsonMessageConverterTest {

    @Test
    public void testConversionToDdp() throws Exception {
        MyTestClass foo = new MyTestClass();
        foo.setName("Hello, world!");
        foo.setDate(new Date());
        JsonMessageConverter converter = new JsonMessageConverter();
        String result = converter.toDDP(foo);
        System.out.println(result);
        Assert.assertTrue(result.contains("Hello, world!"));
    }

    class MyTestClass {

        private String name;

        private Date date;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
