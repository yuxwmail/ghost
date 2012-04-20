package org.knot.ghost.core.service;

import org.knot.ghost.core.domain.Account;
import org.knot.ghost.core.persistence.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AccountService {

  @Autowired
  private AccountMapper accountMapper;

  public Account getAccountByName(String username) {
    return accountMapper.getAccountByUsername(username);
  }

  public Account getAccount(Account account) {
      return accountMapper.getAccount(account);
    }

  @Transactional
  public void insertAccount(Account account) {
    accountMapper.insertAccount(account);
  }

  @Transactional
  public void updateAccount(Account account) {
    accountMapper.updateAccount(account);
  }
}
