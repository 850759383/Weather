package com.hyn;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(2, "com.example.yininghuang.weather.dao");
        Entity entity = schema.addEntity("City");
        entity.setTableName("city");
        entity.addStringProperty("id").primaryKey();
        entity.addStringProperty("cityName");
        entity.addStringProperty("province");
        new DaoGenerator().generateAll(schema, "../Weather/app/src/main/java");
    }
}
