package com.leonrjg.treball.utils;

    final public class DataHolder {
        public static final String API_BASE_URL = "http://akai.nijiiro.moe:39284/";

        private String city;
        private String category_id;
        private String search;

        public void setCity(String city) {this.city = city;}
        public void setCategoryId(String category_id) { this.category_id = category_id; }
        public void setSearch(String search) { this.search = search; }

        public String getJobsUrl() {
            String API_URL = API_BASE_URL + "jobs";
            if(search != null) {
                API_URL = API_URL + "/search/" + search;
            }
            if(category_id != null) {
                API_URL = API_URL + "/category/" + category_id;
            }
            if(city != null) {
                API_URL = API_URL + "/city/" + city;
            }
            return API_URL;
        }

        private static final DataHolder holder = new DataHolder();
        public static DataHolder getInstance() {return holder;}
    }
