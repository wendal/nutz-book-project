package net.wendal.nutzbook.common.util;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.util.Tag;

@SuppressWarnings("unchecked")
public class ER {

    @SuppressWarnings("rawtypes")
    public static String make(Dao dao, List<Class<?>> list) {
        StringBuilder sb = new StringBuilder();
        String NL = "\r\n";
        List<String> links = new ArrayList<>();
        for (Class klass : list) {
            if (klass.getAnnotation(Table.class) == null)
                continue;
            Entity en = dao.getEntity(klass);
            sb.append(String.format("[%s]", en.getTableName())).append(NL);
            List<MappingField> mf_list = en.getMappingFields();
            for (MappingField mf : mf_list) {
                sb.append("    ");
                if (mf.isId())
                    sb.append("*");
                sb.append(mf.getColumnName());
                sb.append(String.format("    {label:\"%s\"}", dao.getJdbcExpert().evalFieldType(mf)));
                sb.append(NL);
            }
            List<LinkField> link_list = en.getLinkFields(null);
            for (LinkField lf : link_list) {
                switch (lf.getLinkType()) {
                case ONE:
                    links.add(String.format("%s 1--1 %s", en.getTableName(), lf.getLinkedEntity().getTableName()));
                    break;
                case MANY:
                    links.add(String.format("%s ?--* %s", en.getTableName(), lf.getLinkedEntity().getTableName()));
                    break;
                case MANYMANY:
                    links.add(String.format("%s *--* %s", en.getTableName(), lf.getLinkedEntity().getTableName()));
                    break;
                default:
                    break;
                }
            }
        }
        for (String link : links) {
            sb.append(link).append(NL);
        }
        
        return sb.toString();
    }
    

    static String TAB = "    ";
    static String NL = "\r\n";
    
    @SuppressWarnings({"rawtypes", "unused"})
    public static String dot(Dao dao, List<Class<?>> list) {
        StringBuilder sb = new StringBuilder("digraph {").append(NL);
        sb.append(TAB).append("graph [rankdir=LR];").append(NL);
        sb.append(TAB).append("node [label=\"\\N\",shape=plaintext];").append(NL);
        sb.append(TAB).append("edge [color=gray50,minlen=2,style=dashed];").append(NL);
        List<String> links = new ArrayList<>();
        for (Class klass : list) {
            if (klass.getAnnotation(Table.class) == null)
                continue;
            Entity en = dao.getEntity(klass);
            sb.append(TAB).append("\"" + en.getTableName() + "\" [label=<<FONT FACE=\"Helvetica\">").append(NL);
            Tag table = Tag.tag("table").attr("BORDER", "0").attr("CELLBORDER", "1").attr("CELLPADDING", "4").attr("CELLSPACING", "0");
            // 表名称
            {
                Tag tr = Tag.tag("tr");
                Tag td = Tag.tag("td");
                Tag b = Tag.tag("b");
                Tag font = Tag.tag("font");
                font.setText(en.getTableName());
                b.add(font);
                td.add(b);
                tr.add(td);
                table.add(tr);
            }
            // 各字段
            List<MappingField> mf_list = en.getMappingFields();
            for (MappingField mf : mf_list) {
                Tag tr = Tag.tag("tr");
                Tag td = Tag.tag("td");
                td.attr("ALIGN", "LEFT");
                {
                    Tag font = Tag.tag("font");
                    font.setText(mf.getColumnName());
                    td.add(font);
                }{
                    Tag font = Tag.tag("font");
                    font.setText(" : ");
                    td.add(font);
                }
                {
                    Tag font = Tag.tag("font");
                    font.setText("["+dao.getJdbcExpert().evalFieldType(mf)+"]");
                    td.add(font);
                }
                tr.add(td);
                table.add(tr);
            }
            sb.append(table.toString(2)).append("</FONT>>];").append(NL);
//            List<LinkField> link_list = en.getLinkFields(null);
//            for (LinkField lf : link_list) {
//                switch (lf.getLinkType()) {
//                case ONE:
//                    links.add(String.format("%s 1--1 %s", en.getTableName(), lf.getLinkedEntity().getTableName()));
//                    break;
//                case MANY:
//                    links.add(String.format("%s ?--* %s", en.getTableName(), lf.getLinkedEntity().getTableName()));
//                    break;
//                case MANYMANY:
//                    links.add(String.format("%s *--* %s", en.getTableName(), lf.getLinkedEntity().getTableName()));
//                    break;
//                default:
//                    break;
//                }
//            }
        }
//        for (String link : links) {
//            sb.append(link).append(NL);
//        }
        sb.append("}");
        return sb.toString();
    }
}
