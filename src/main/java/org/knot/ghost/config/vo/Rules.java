package org.knot.ghost.config.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.XStream;

/**
 * 规则集合元数据类
 * 
 * @author <a href="mailto:yuxwmail@gmail.com">yuxiaowei</a>
 */
public class Rules {

    private final static Rules _rules = new Rules();

    private Rules(){
    };

    public static Rules Instance() {
        return _rules;
    }

    // 唯一标识
    private String     name;

    // 缺省读数据源key
    private String     defaultReadDataSourceKey;

    // 缺省写数据源key
    private String     defaultWriteDataSourceKey;

    // 规则集合
    // 这里的key采用sqlmap_name的组合方式，方便后面的检索；
    private List<Rule> listRule;

    public List<Rule> getListRule() {
        return listRule;
    }

    public void setListRule(List<Rule> listRule) {
        this.listRule = listRule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trimToEmpty(name);
    }

    public String getDefaultReadDataSourceKey() {
        return defaultReadDataSourceKey;
    }

    public void setDefaultReadDataSourceKey(String defaultReadDataSourceKey) {
        this.defaultReadDataSourceKey = StringUtils.trimToEmpty(defaultReadDataSourceKey);
    }

    public String getDefaultWriteDataSourceKey() {
        return defaultWriteDataSourceKey;
    }

    public void setDefaultWriteDataSourceKey(String defaultWriteDataSourceKey) {
        this.defaultWriteDataSourceKey = StringUtils.trimToEmpty(defaultWriteDataSourceKey);
    }

    // public Map<String, Rule> getMapRule() {
    // return mapRule;
    // }
    //
    // public void setMapRule(Map<String, Rule> mapRule) {
    // this.mapRule = mapRule;
    // }

    public static void main(String[] args) {

        XStream xstream = new XStream();
        xstream.alias("rules", Rules.class);
        xstream.aliasAttribute(Rules.class, "name", "name");
        xstream.aliasAttribute(Rules.class, "defaultReadDataSourceKey", "defaultReadDataSourceKey");
        xstream.aliasAttribute(Rules.class, "defaultWriteDataSourceKey", "defaultWriteDataSourceKey");
        xstream.addImplicitCollection(Rules.class, "listRule", "rule", Rule.class);
        xstream.alias("rule", Rule.class);
        xstream.aliasAttribute(Rule.class, "name", "name");
        xstream.aliasField("targetDataSource", Rule.class, "mapTargetDataSource");
        xstream.addImplicitCollection(Rule.class, "listTargetDataSource", "targetDataSource", ReplacementTables.class);
        xstream.addImplicitCollection(ReplacementTables.class, "listReplacementTable", "replacementTable", ReplacementTable.class);
        xstream.alias("targetDataSource", ReplacementTables.class);
        xstream.aliasAttribute(ReplacementTables.class, "key", "key");
        xstream.alias("replacementTable", ReplacementTable.class);
        xstream.aliasAttribute(ReplacementTable.class, "originalTableName", "originalTableName");
        xstream.aliasAttribute(ReplacementTable.class, "targetTableName", "targetTableName");
        
        
        

        Rule rule4 = new Rule();
        rule4.setName("rule4");
        rule4.setSqlmap("org.knot.ghost.core.persistence.AccountMapper.getAccountByUsername");
        rule4.setExpression("$ROOT.equals('11')");

        List<ReplacementTables> mapTargetDataSource4 = new ArrayList<ReplacementTables>();

        List<ReplacementTable> list4 = new ArrayList<ReplacementTable>();
        ReplacementTable repacementTable4 = new ReplacementTable();
        repacementTable4.setOriginalTableName("account");
        repacementTable4.setTargetTableName("account1,account2");
        list4.add(repacementTable4);

        ReplacementTables replacementTables = new ReplacementTables();
        replacementTables.setListReplacementTable(list4);
        replacementTables.setKey("dataSource1");

        mapTargetDataSource4.add(replacementTables);

        rule4.setListTargetDataSource(mapTargetDataSource4);

        //
        Rule rule5 = new Rule();
        rule5.setName("rule5");
        rule5.setSqlmap("org.knot.ghost.core.persistence.AccountMapper.getAccountByUsername");
        rule5.setExpression("$ROOT.equals('13')");

        List<ReplacementTables> mapTargetDataSource5 = new ArrayList<ReplacementTables>();

        List<ReplacementTable> list5 = new ArrayList<ReplacementTable>();
        ReplacementTable repacementTable5 = new ReplacementTable();
        repacementTable5.setOriginalTableName("account");
        repacementTable5.setTargetTableName("account1");
        list5.add(repacementTable5);

        ReplacementTables replacementTables5 = new ReplacementTables();
        replacementTables5.setListReplacementTable(list5);
        replacementTables5.setKey("dataSource2");

        mapTargetDataSource5.add(replacementTables5);

        rule5.setListTargetDataSource(mapTargetDataSource5);

        // MyBatisRotingRule rotingRule5 = new MyBatisRotingRule(rule5);
        //
        Rule rule6 = new Rule();
        rule6.setName("rule6");
        rule6.setSqlmap("org.knot.ghost.core.persistence.AccountMapper.getAccountByUsername");
        rule6.setExpression("$ROOT.equals('2')");

        List<ReplacementTables> mapTargetDataSource6 = new ArrayList<ReplacementTables>();
        // List<ReplacementTable> list2 = new ArrayList<ReplacementTable>();
        // ReplacementTable repacementTable2 = new ReplacementTable();
        // repacementTable2.setOriginalTableName("account");
        // repacementTable2.setTargetTableName("account");
        // list2.add(repacementTable2);

        ReplacementTables replacementTables6 = new ReplacementTables();
        replacementTables6.setListReplacementTable(null);
        replacementTables6.setKey("dataSource1");

        mapTargetDataSource6.add(replacementTables6);
        rule6.setListTargetDataSource(mapTargetDataSource6);
        //
        // MyBatisRotingRule rotingRule6 = new MyBatisRotingRule(rule6);

        // init Rules
        Rules rules = Rules.Instance();

        List<Rule> mapRule = new ArrayList<Rule>();
        mapRule.add(rule4);
        mapRule.add(rule5);
        mapRule.add(rule6);

        rules.setName("default");
        rules.setDefaultReadDataSourceKey("dataSource1");
        rules.setDefaultWriteDataSourceKey("dataSource2");

        rules.setListRule(mapRule);

        String xml = xstream.toXML(rules);
        System.out.println(xml);
    }
}
