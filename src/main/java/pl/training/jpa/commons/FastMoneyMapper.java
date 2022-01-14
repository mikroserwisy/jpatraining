package pl.training.jpa.commons;

import org.javamoney.moneta.FastMoney;
import org.mapstruct.Mapper;

@Mapper
public class FastMoneyMapper {

    public FastMoney toFastMoney(String value) {
        return value != null ? FastMoney.parse(value) : LocalMoney.zero();
    }

    public String toText(FastMoney value) {
        return value != null ? value.toString() : LocalMoney.zero().toString();
    }

}
