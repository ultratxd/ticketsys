package com.cj.ticketsys.entities

enum class ChannelTypes(val value:Short) : CodeBaseEnum {
    Rack(1),
    Ctrip(2),
    Travel(3),
    Terminal(4),
    Seckill(5),
    LuMaMa(6),
    TongCheng(7),
    MeiTuan(8),
    RackSpecial(9),
    Team(10),
    Menpiao123(11);

    override fun code(): Int {
        return this.value.toInt()
    }

    companion object {
        fun prase(value: Int): ChannelTypes {
            when (value) {
                1 -> return Rack
                2 -> return Ctrip
                3 -> return Travel
                4 -> return Terminal
                5 -> return Seckill
                6 -> return LuMaMa
                7 -> return TongCheng
                8 -> return MeiTuan
                9 -> return RackSpecial
                10 -> return Team
                11 -> return Menpiao123
            }
            throw IllegalArgumentException("value不存在枚举")
        }
    }
}