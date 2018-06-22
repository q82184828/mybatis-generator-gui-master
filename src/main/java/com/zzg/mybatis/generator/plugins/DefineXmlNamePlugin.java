package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 修改xml的FileName，使之可配置 @author:ChenFei
 * @author chenfei
 * @date 2018-06-22 13:45
 */
public class DefineXmlNamePlugin extends PluginAdapter {
	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
		try {
			Field field = sqlMap.getClass().getDeclaredField("fileName");
			field.setAccessible(true);
			field.set(sqlMap, introspectedTable.getTableConfiguration().getProperty("mapperXmlName"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
