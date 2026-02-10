package indi.wenyan.interpreter.utils;

import java.util.HashMap;

// TODO: solve the problem of custom ids
public enum WenyanSymbol {;
    public static final HashMap<String, String> SYMBOLS = new HashMap<>();
    public static final String SYMBOL_FORMAT = "「%s」";

    public static String var(String key) {
        return SYMBOLS.get(key);
    }

    private static void addSymbol(String key, String symbol) {
        SYMBOLS.put(key, String.format(SYMBOL_FORMAT, symbol));
    }
    static {
        addSymbol("SemaphoreModule", "信");
        addSymbol("SemaphoreModule.acquire", "獲取");
        addSymbol("SemaphoreModule.release", "釋放");

        addSymbol("BitModule", "位經");
        addSymbol("BitModule.leftShift", "左移");
        addSymbol("BitModule.rightShift", "右移");
        addSymbol("BitModule.zeroFillRightShift", "補零右移");
        addSymbol("BitModule.bitAnd", "位與");
        addSymbol("BitModule.bitOr", "位或");
        addSymbol("BitModule.bitXor", "異或");
        addSymbol("BitModule.bitNand", "與非");
        addSymbol("BitModule.bitNot", "位變");


        addSymbol("CollectionModule", "集");
        addSymbol("CollectionModule.disjoint", "無交");
        addSymbol("CollectionModule.intersection", "交集");
        addSymbol("CollectionModule.difference", "差集");
        addSymbol("CollectionModule.reverse", "反轉");
        addSymbol("CollectionModule.sort", "排序");
        addSymbol("CollectionModule.contains", "包含");
        addSymbol("CollectionModule.max", "最大");
        addSymbol("CollectionModule.min", "最小");

        addSymbol("MathModule", "算經");
        addSymbol("MathModule.PI", "圓周率");
        addSymbol("MathModule.TAU", "倍圓周率");
        addSymbol("MathModule.HALF_PI", "半圓周率");
        addSymbol("MathModule.QUARTER_PI", "四分圓周率");
        addSymbol("MathModule.E", "自然常數");
        addSymbol("MathModule.EULER", "歐拉常數");
        addSymbol("MathModule.GOLDEN_RATIO", "黃金分割數");
        addSymbol("MathModule.SQRT_2", "二之平方根");
        addSymbol("MathModule.LOG_2", "二之對數");
        addSymbol("MathModule.LOG_10", "十之對數");
        addSymbol("MathModule.sin", "正弦");
        addSymbol("MathModule.cos", "餘弦");
        addSymbol("MathModule.asin", "反正弦");
        addSymbol("MathModule.acos", "反餘弦");
        addSymbol("MathModule.tan", "正切");
        addSymbol("MathModule.atan", "反正切");
        addSymbol("MathModule.atan2", "勾股求角");
        addSymbol("MathModule.hypot", "勾股求弦");
        addSymbol("MathModule.log", "對數");
        addSymbol("MathModule.exp", "指數");
        addSymbol("MathModule.pow", "冪");
        addSymbol("MathModule.sqrt", "平方根");
        addSymbol("MathModule.abs", "絕對");
        addSymbol("MathModule.ceil", "取頂");
        addSymbol("MathModule.floor", "取底");
        addSymbol("MathModule.round", "取整");
        addSymbol("MathModule.signum", "正負");

        addSymbol("RandomModule", "易經");
        addSymbol("RandomModule.nextInt", "占數");
        addSymbol("RandomModule.nextDouble", "占分");
        addSymbol("RandomModule.nextTriangle", "占偏");
        addSymbol("RandomModule.nextBoolean", "占爻");

        addSymbol("StringModule", "文");
        addSymbol("StringModule.length", "長");
        addSymbol("StringModule.charAt", "取");
        addSymbol("StringModule.indexOf", "尋");
        addSymbol("StringModule.split", "分");
        addSymbol("StringModule.replace", "換");
        addSymbol("StringModule.reverse", "反");
        addSymbol("StringModule.trim", "去空");
        addSymbol("StringModule.contains", "包含");
        addSymbol("StringModule.startsWith", "起始為");
        addSymbol("StringModule.endsWith", "結束為");

        addSymbol("Vec3Module", "向");
        addSymbol("Vec3Module.object", "向");

        addSymbol("BlockModule", "塊");
        addSymbol("BlockModule.search", "尋");
        addSymbol("BlockModule.get", "取");
        addSymbol("BlockModule.attach", "附");

        addSymbol("CommunicateModule", "通");
        addSymbol("CommunicateModule.self", "我");

        addSymbol("EntityModule", "实");
        addSymbol("EntityModule.inspectRange", "察域之实");
        addSymbol("EntityModule.nearby", "近域之实");
        addSymbol("EntityModule.lineOfSight", "实之视");

        addSymbol("ExplosionModule", "爆");
        addSymbol("ExplosionModule.lightning", "雷");
        addSymbol("ExplosionModule.explode", "爆");
        addSymbol("ExplosionModule.ignite", "燃");
        addSymbol("ExplosionModule.fireball", "火球");

        addSymbol("ItemModule", "仓");
        addSymbol("ItemModule.transfer", "移");
        addSymbol("ItemModule.read", "讀");

        addSymbol("WorldModule", "信");
        addSymbol("WorldModule.signalStrength", "量");
        addSymbol("WorldModule.emitSignal", "輸能");
        addSymbol("WorldModule.trigger", "觸");
        addSymbol("WorldModule.changeWeather", "變天");

        addSymbol("Null", "");
    }
}
