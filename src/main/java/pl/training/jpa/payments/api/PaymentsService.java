package pl.training.jpa.payments.api;

import org.javamoney.moneta.FastMoney;

public interface PaymentsService {

    void process(FastMoney value);

}
