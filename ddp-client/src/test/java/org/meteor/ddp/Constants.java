package org.meteor.ddp;

import java.util.List;

public class Constants {

    public static final String SERVER_ADDRESS = "ws://192.168.1.104:3200/websocket";

    public static class Tab {
        private String name;
        private Object createdAt;
        private Number total;
        private List<Customer> customers;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Object createdAt) {
            this.createdAt = createdAt;
        }

        public Number getTotal() {
            return total;
        }

        public void setTotal(Number total) {
            this.total = total;
        }

        public List<Customer> getCustomers() {
            return customers;
        }

        public void setCustomers(List<Customer> customers) {
            this.customers = customers;
        }
    }

    public static class Customer {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
