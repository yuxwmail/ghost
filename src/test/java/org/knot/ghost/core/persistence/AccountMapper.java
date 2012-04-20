package org.knot.ghost.core.persistence;

import org.knot.ghost.core.domain.Account;

public interface AccountMapper {

  Account getAccountByUsername(String username);
  
  Account getAccount(Account username);

  void insertAccount(Account account);
  
  void updateAccount(Account account);

}
