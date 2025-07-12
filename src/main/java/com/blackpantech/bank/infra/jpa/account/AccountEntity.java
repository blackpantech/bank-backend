package com.blackpantech.bank.infra.jpa.account;

import com.blackpantech.bank.infra.jpa.transaction.TransactionEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ACCOUNTS")
public class AccountEntity {

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
    private final List<TransactionEntity> transactions = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "BALANCE")
    private float balance;

    protected AccountEntity() {
        // default hibernate constructor
    }

    public AccountEntity(final float balance) {
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(final float balance) {
        this.balance = balance;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

}
