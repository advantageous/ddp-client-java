package org.meteor.ddp;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class MessageConverterJsonTest {

    @Test
    public void testConversionToDdp() throws Exception {
        MyTestClass foo = new MyTestClass();
        foo.setName("Hello, world!");
        foo.setDate(new Date());
        MessageConverterJson converter = new MessageConverterJson();
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
