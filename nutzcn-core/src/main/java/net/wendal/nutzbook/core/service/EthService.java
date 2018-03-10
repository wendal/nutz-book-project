package net.wendal.nutzbook.core.service;

import java.io.IOException;
import java.math.BigInteger;

import org.nutz.lang.util.NutMap;

public interface EthService {

    String newAccount(String password) throws IOException;

    NutMap sendTransaction(String fromAcc, String toAcc, String password, double wei);

    BigInteger getBanlance(String address) throws IOException;

}