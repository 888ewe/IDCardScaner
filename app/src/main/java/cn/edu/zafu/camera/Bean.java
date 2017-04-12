package cn.edu.zafu.camera;

import java.util.List;

/**
 * 作者：沉默
 * 日期：2017/4/1
 * QQ:823925783
 */

public class Bean {

    /**
     * image_id : CRPf/ZomAa/CM9D2x8QSBg==
     * request_id : 1491014817,930919ae-3d0e-4036-8c38-cd46d7ea15ac
     * cards : [{"name":"方礼祥","gender":"男","id_card_number":"32118219740926291X","birthday":"1974-09-26","race":"汉","address":"江苏省扬中市油坊镇邻丰村240号","type":1,"side":"front"}]
     * time_used : 943
     */

    private String image_id;
    private String request_id;
    private int time_used;
    private List<CardsBean> cards;

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public List<CardsBean> getCards() {
        return cards;
    }

    public void setCards(List<CardsBean> cards) {
        this.cards = cards;
    }

    public static class CardsBean {
        /**
         * name : 方礼祥
         * gender : 男
         * id_card_number : 32118219740926291X
         * birthday : 1974-09-26
         * race : 汉
         * address : 江苏省扬中市油坊镇邻丰村240号
         * type : 1
         * side : front
         * "issued_by": "北京市公安局海淀分局",
            "side": "back",
            "valid_date": "2010.11.13-2020.11.13"
         */

        private String name;
        private String gender;
        private String id_card_number;
        private String birthday;
        private String race;
        private String address;
        private int type;
        private String side;
        private String issued_by;
        private String valid_date;

        public String getIssued_by() {
            return issued_by;
        }

        public void setIssued_by(String issued_by) {
            this.issued_by = issued_by;
        }

        public String getValid_date() {
            return valid_date;
        }

        public void setValid_date(String valid_date) {
            this.valid_date = valid_date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getId_card_number() {
            return id_card_number;
        }

        public void setId_card_number(String id_card_number) {
            this.id_card_number = id_card_number;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getRace() {
            return race;
        }

        public void setRace(String race) {
            this.race = race;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getSide() {
            return side;
        }

        public void setSide(String side) {
            this.side = side;
        }
    }
}
