package net.wendal.nutzbook.core.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.nutz.dao.Dao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.Param;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;

import net.wendal.nutzbook.core.service.EthService;

@IocBean(name="ethService")
public class EthServiceImpl implements EthService  {

    private static final Log log = Logs.get();

    @Inject
    protected Web3j web3j;

    @Inject
    protected Admin web3jAdmin;

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected Dao dao;

    public String newAccount(String password) throws IOException {
        NewAccountIdentifier aid = web3jAdmin.personalNewAccount(password).send();
        if (aid.hasError()) {
            throw new RuntimeException(aid.getError().toString());
        }
        return aid.getAccountId();
    }

    public NutMap sendTransaction(String fromAcc, String toAcc, String password, @Param("wei") double wei) {
        NutMap re = new NutMap();
        // 检查转账金额
        if (wei < 0.01) {
            re.put("msg", "起码转账 0.01 eth");
            return re;
        }
        if (wei > 10000) {
            re.put("msg", "最多转账 10000 eth");
            return re;
        }
        BigInteger value = Convert.toWei(new BigDecimal(wei), Convert.Unit.ETHER).toBigInteger();
        Transaction transaction = Transaction.createEtherTransaction(fromAcc, null, null, null, toAcc, value);
        try {
            EthSendTransaction est = web3jAdmin.personalSendTransaction(transaction, password).send();
            String hash = est.getTransactionHash();
            return re.setv("ok", true).setv("hash", hash);
        }
        catch (Exception e) {
            log.warn("转账失败!!!", e);
            return re.setv("msg", e.getMessage());
        }
    }

    public BigInteger getBanlance(String address) throws IOException {
        return web3j.ethGetBalance(address, null).send().getBalance();
    }
}
