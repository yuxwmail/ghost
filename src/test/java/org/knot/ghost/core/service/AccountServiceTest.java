package org.knot.ghost.core.service;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.knot.ghost.core.domain.Account;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AccountServiceTest {
	
	static ApplicationContext context = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context =  new ClassPathXmlApplicationContext("applicationContext-ghost.xml");
	}

	@Test
	public void testGetAccountString() {
		
		AccountService service = context.getBean(AccountService.class);
//		service.getAccount("1");
	}

	@Test
	public void testInsertAccount() {
		AccountService service = context.getBean(AccountService.class);
		//service.insertAccount(account)
	}

	@Test
	public void testUpdateAccount() {
		fail("Not yet implemented");
	}
	
	
	private static void testMvel()
	{
	    Account argument = null;
	    argument = new Account();
	    argument.setUsername("2");
	    
	    try {
            Map<String, Object> vrs = new HashMap<String, Object>();
            vrs.putAll(new HashMap<String, Object>());
            vrs.put("$ROOT", argument); // add top object reference for expression
            VariableResolverFactory vrfactory = new MapVariableResolverFactory(vrs);
            if (MVEL.evalToBoolean("username.equals('2')", argument, vrfactory)) {
               System.out.println("true");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
	}
	
	public  static void main(String[] args)
	{
	    
	    //testMvel();
        
		context =  new ClassPathXmlApplicationContext("applicationContext-ghost.xml");
		AccountService service = context.getBean(AccountService.class);
		
		
        
        //datasource1  account1
        Account a = service.getAccountByName("11");
        System.out.println("username:" + a.getUsername());
        
        //datasource1  account
        a = service.getAccountByName("2");
        System.out.println("username:" + a.getUsername());
        
        //datasource2  account1
        a =service.getAccountByName("13");
        System.out.println("username:" + a.getUsername());
        
		/*
		Account acount = new Account();
		
		//datasource1  account1
	    acount.setUsername("11");
		Account a = service.getAccount(acount);
		System.out.println("username:" + a.getUsername());
		
		//datasource1  account
		acount.setUsername("2");
		a = service.getAccount(acount);
		System.out.println("username:" + a.getUsername());
		
		//datasource2  account1
		acount.setUsername("13");
		a =service.getAccount(acount);
		System.out.println("username:" + a.getUsername());
		*/
	}

}
