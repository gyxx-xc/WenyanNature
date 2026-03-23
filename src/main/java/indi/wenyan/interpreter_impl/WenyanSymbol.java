package indi.wenyan.interpreter_impl;

// TODO: solve the problem of custom ids
public enum WenyanSymbol {;
    public static final String FORMATION_CORE = "「眼」";
    public static final String CORE_START = "「啓」";
    public static final String CORE_STATUS = "「狀」";
    public static final String CORE_JOIN = "「歸」";
    public static final String PRINT = "書";
    public static final String SCREEN = "";
    public static final String PISTON = "「移」";
    public static final String PISTON_PUSH = "「推」";
    public static final String PISTON_PULL = "「拉」";
    public static final String CRAFTING = "";
    public static final String CRAFTING_ARGS = "「参」";
    public static final String POWER = "";
    public static final String POWER_BASE = "「天根」";
    public static final String POWER_MOD = "「数极」";
    public static final String POWER_UP = "「天机」";
    public static final String POWER_ANS = "「天意」";
    public static final String BLOCK_NAME = "「名」";
    public static final String BLOCK_SAME_ITEM = "「同物」";
    public static final String BLOCK_SAME_BLOCK = "「同塊」";
    public static final String BLOCK_HAS_ENTITY = "「有實」";
    public static final String BLOCK_DIRECTION = "「向」";
    public static final String ENTITY_POS = "「位」";
    public static final String ENTITY_MOVE = "「移」";
    public static final String ENTITY_LOOK = "「向」";
    public static final String ENTITY_ALIVE = "「活」";
    public static final String ENTITY_NAME = "「名」";
    public static final String ENTITY_HEIGHT = "「高」";
    public static final String VECTOR_TYPE_UP = "「上」";
    public static final String VECTOR_TYPE_DOWN = "「下」";
    public static final String VECTOR_TYPE_EAST = "「東」";
    public static final String VECTOR_TYPE_WEST = "「西」";
    public static final String VECTOR_TYPE_SOUTH = "「南」";
    public static final String VECTOR_TYPE_NORTH = "「北」";
    public static final String VECTOR_Y = "「上下」";
    public static final String VECTOR_X = "「東西」";
    public static final String VECTOR_Z = "「南北」";
    public static final String VECTOR_LENGTH = "「長」";
    public static final String VECTOR_LENGTH_SQR = "「方長」";
    public static final String VECTOR_OFFSET = "「偏移」";
    public static final String VECTOR_TYPE_ZERO = "「零」";

    public static final String SemaphoreModule = "「信」";
    public static final String SemaphoreModule$acquire = "「獲取」";
    public static final String SemaphoreModule$release = "「釋放」";

    public static final String BitModule = "「位經」";
    public static final String BitModule$leftShift = "「左移」";
    public static final String BitModule$rightShift = "「右移」";
    public static final String BitModule$zeroFillRightShift = "「補零右移」";
    public static final String BitModule$bitAnd = "「位與」";
    public static final String BitModule$bitOr = "「位或」";
    public static final String BitModule$bitXor = "「異或」";
    public static final String BitModule$bitNand = "「與非」";
    public static final String BitModule$bitNot = "「位變」";

    public static final String CollectionModule = "「集」";
    public static final String CollectionModule$disjoint = "「無交」";
    public static final String CollectionModule$intersection = "「交集」";
    public static final String CollectionModule$difference = "「差集」";
    public static final String CollectionModule$reverse = "「反轉」";
    public static final String CollectionModule$sort = "「排序」";
    public static final String CollectionModule$contains = "「包含」";
    public static final String CollectionModule$max = "「最大」";
    public static final String CollectionModule$min = "「最小」";

    public static final String MathModule = "「算經」";
    public static final String MathModule$PI = "「圓周率」";
    public static final String MathModule$TAU = "「倍圓周率」";
    public static final String MathModule$HALF_PI = "「半圓周率」";
    public static final String MathModule$QUARTER_PI = "「四分圓周率」";
    public static final String MathModule$E = "「自然常數」";
    public static final String MathModule$EULER = "「歐拉常數」";
    public static final String MathModule$GOLDEN_RATIO = "「黃金分割數」";
    public static final String MathModule$SQRT_2 = "「二之平方根」";
    public static final String MathModule$LOG_2 = "「二之對數」";
    public static final String MathModule$LOG_10 = "「十之對數」";
    public static final String MathModule$sin = "「正弦」";
    public static final String MathModule$cos = "「餘弦」";
    public static final String MathModule$asin = "「反正弦」";
    public static final String MathModule$acos = "「反餘弦」";
    public static final String MathModule$tan = "「正切」";
    public static final String MathModule$atan = "「反正切」";
    public static final String MathModule$atan2 = "「勾股求角」";
    public static final String MathModule$hypot = "「勾股求弦」";
    public static final String MathModule$log = "「對數」";
    public static final String MathModule$exp = "「指數」";
    public static final String MathModule$pow = "「冪」";
    public static final String MathModule$sqrt = "「平方根」";
    public static final String MathModule$abs = "「絕對」";
    public static final String MathModule$ceil = "「取頂」";
    public static final String MathModule$floor = "「取底」";
    public static final String MathModule$round = "「取整」";
    public static final String MathModule$signum = "「正負」";

    public static final String RandomModule = "「易經」";
    public static final String RandomModule$nextInt = "「占數」";
    public static final String RandomModule$nextDouble = "「占分」";
    public static final String RandomModule$nextTriangle = "「占偏」";
    public static final String RandomModule$nextBoolean = "「占爻」";

    public static final String StringModule = "「文」";
    public static final String StringModule$length = "「長」";
    public static final String StringModule$charAt = "「取」";
    public static final String StringModule$indexOf = "「尋」";
    public static final String StringModule$split = "「分」";
    public static final String StringModule$replace = "「換」";
    public static final String StringModule$reverse = "「反」";
    public static final String StringModule$trim = "「去空」";
    public static final String StringModule$contains = "「包含」";
    public static final String StringModule$startsWith = "「起始為」";
    public static final String StringModule$endsWith = "「結束為」";

    public static final String Vec3Module = "「向」";
    public static final String Vec3Module$object = "「向」";

    public static final String BlockModule = "「塊」";
    public static final String BlockModule$search = "「尋」";
    public static final String BlockModule$get = "「取」";
    public static final String BlockModule$attach = "「附」";

    public static final String CommunicateModule = "「通」";
    public static final String CommunicateModule$self = "「我」";

    public static final String EntityModule = "「实」";
    public static final String EntityModule$inspectRange = "「察域之实」";
    public static final String EntityModule$nearby = "「近域之实」";
    public static final String EntityModule$lineOfSight = "「实之视」";

    public static final String ExplosionModule = "「爆」";
    public static final String ExplosionModule$lightning = "「雷」";
    public static final String ExplosionModule$explode = "「爆」";
    public static final String ExplosionModule$ignite = "「燃」";
    public static final String ExplosionModule$fireball = "「火球」";

    public static final String ItemModule = "「仓」";
    public static final String ItemModule$transfer = "「移」";
    public static final String ItemModule$read = "「讀」";

    public static final String WorldModule = "「信」";
    public static final String WorldModule$signalStrength = "「量」";
    public static final String WorldModule$emitSignal = "「輸能」";
    public static final String WorldModule$changeWeather = "「變天」";

    public static final String BlockingQueueModule = "「佇列」";
    public static final String BlockingQueueModule$put = "「入」";
    public static final String BlockingQueueModule$take = "「取」";
    public static final String BlockingQueueModule$offer = "「試入」";
    public static final String BlockingQueueModule$poll = "「試取」";
    public static final String BlockingQueueModule$peek = "「窺」";
    public static final String BlockingQueueModule$size = "「長」";
    public static final String BlockingQueueModule$clear = "「清空」";
}
