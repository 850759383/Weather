package com.example.yininghuang.weather.model;

/**
 * Created by Yining Huang on 2016/9/30.
 */

public class AMapGeoCode {

    /**
     * status : 1
     * info : OK
     * infocode : 10000
     * regeocode : {"formatted_address":"浙江省宁波市余姚市梨洲街道巍星路200-5号","addressComponent":{"country":"中国","province":"浙江省","city":"宁波市","citycode":"0574","district":"余姚市","adcode":"330281","township":"梨洲街道","towncode":"330281001000","neighborhood":{"name":[],"type":[]},"building":{"name":[],"type":[]},"streetNumber":{"street":"巍星路","number":"200-5号","location":"121.16021,30.03979","direction":"东南","distance":"30.8865"},"businessAreas":[[]]}}
     */

    private String status;
    private String info;
    private String infocode;
    /**
     * formatted_address : 浙江省宁波市余姚市梨洲街道巍星路200-5号
     * addressComponent : {"country":"中国","province":"浙江省","city":"宁波市","citycode":"0574","district":"余姚市","adcode":"330281","township":"梨洲街道","towncode":"330281001000","neighborhood":{"name":[],"type":[]},"building":{"name":[],"type":[]},"streetNumber":{"street":"巍星路","number":"200-5号","location":"121.16021,30.03979","direction":"东南","distance":"30.8865"},"businessAreas":[[]]}
     */

    private RegeocodeBean regeocode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public RegeocodeBean getRegeocode() {
        return regeocode;
    }

    public void setRegeocode(RegeocodeBean regeocode) {
        this.regeocode = regeocode;
    }

    public static class RegeocodeBean {
        /**
         * country : 中国
         * province : 浙江省
         * city : 宁波市
         * citycode : 0574
         * district : 余姚市
         * adcode : 330281
         * township : 梨洲街道
         * towncode : 330281001000
         */

        private AddressComponentBean addressComponent;

        public AddressComponentBean getAddressComponent() {
            return addressComponent;
        }

        public void setAddressComponent(AddressComponentBean addressComponent) {
            this.addressComponent = addressComponent;
        }

        public static class AddressComponentBean {
            private String country;
            private String province;
            private String city;
            private String citycode;
            private String district;
            private String adcode;
            private String township;
            private String towncode;

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCitycode() {
                return citycode;
            }

            public void setCitycode(String citycode) {
                this.citycode = citycode;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getAdcode() {
                return adcode;
            }

            public void setAdcode(String adcode) {
                this.adcode = adcode;
            }

            public String getTownship() {
                return township;
            }

            public void setTownship(String township) {
                this.township = township;
            }

            public String getTowncode() {
                return towncode;
            }

            public void setTowncode(String towncode) {
                this.towncode = towncode;
            }
        }
    }
}
