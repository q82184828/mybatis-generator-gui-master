package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * 生成Service实现类
 * @author chenfei
 * @date 2018-06-25 13:14
 */
public class ServiceImplPlugin extends PluginAdapter {

	private ShellCallback shellCallback = null;

	private String modelDir;

	public ServiceImplPlugin() {
		shellCallback = new DefaultShellCallback(false);
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<GeneratedJavaFile>();
		JavaFormatter javaFormatter = context.getJavaFormatter();
		String javaFileEncoding = context.getProperty("javaFileEncoding");
		String serviceTargetDir = context.getJavaClientGeneratorConfiguration().getTargetProject();
		String serviceTargetPackage = context.getProperty("serviceImplTargetPackage");
		String serviceName = context.getProperty("serviceImplName");
		modelDir = serviceTargetPackage + "." + introspectedTable.getTableConfiguration().getDomainObjectName();

		TopLevelClass serviceImplClass = new TopLevelClass(serviceTargetPackage + "." + serviceName);
		GeneratedJavaFile mapperJavafile = null;
		if (stringHasValue(serviceName)) {
			serviceImplClass.addImportedType(new FullyQualifiedJavaType(modelDir));
			String mapperName;
			String daoName;
			if(stringHasValue(introspectedTable.getTableConfiguration().getMapperName())){
				mapperName = serviceTargetPackage + "." + introspectedTable.getTableConfiguration().getMapperName();
				daoName = toLowerCaseFirstOne(introspectedTable.getTableConfiguration().getMapperName());
			} else {
				mapperName = serviceTargetPackage + "." + introspectedTable.getTableConfiguration().getDomainObjectName() + "Mapper";
				daoName = toLowerCaseFirstOne(introspectedTable.getTableConfiguration().getDomainObjectName() + "Mapper");
			}
			serviceImplClass.addImportedType(new FullyQualifiedJavaType(mapperName));
			serviceImplClass.addImportedType(new FullyQualifiedJavaType(serviceTargetPackage + "." + context.getProperty("serviceName")));
			serviceImplClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
			serviceImplClass.addImportedType(new FullyQualifiedJavaType("org.springframework.transaction.annotation.Transactional"));
			serviceImplClass.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));
			serviceImplClass.setVisibility(JavaVisibility.PUBLIC);
			serviceImplClass.addAnnotation("@Service");
			serviceImplClass.addAnnotation("@Transactional(rollbackFor = Exception.class)");

			serviceImplClass.addSuperInterface(new FullyQualifiedJavaType(serviceTargetPackage + "." + context.getProperty("serviceName")));

			addField(serviceImplClass, mapperName, daoName);

			serviceImplClass.addMethod(deleteMethod(daoName));
			serviceImplClass.addMethod(updateByPrimaryKeyMethod(daoName));
			serviceImplClass.addMethod(updateByPrimaryKeySelectiveMethod(daoName));
			serviceImplClass.addMethod(selectByPrimaryKeyMethod(daoName));
			serviceImplClass.addMethod(insertSelectiveMethod(daoName));
			serviceImplClass.addMethod(insertMethod(daoName));

			mapperJavafile = new GeneratedJavaFile(serviceImplClass, serviceTargetDir, javaFileEncoding, javaFormatter);
			try {
				File mapperDir = shellCallback.getDirectory(serviceTargetDir, serviceTargetPackage);
				File mapperFile = new File(mapperDir, mapperJavafile.getFileName());
				// 文件不存在
				if (!mapperFile.exists()) {
					mapperJavaFiles.add(mapperJavafile);
				}
			} catch (ShellException e) {
				e.printStackTrace();
			}
		}
		return mapperJavaFiles;
	}

	private Method deleteMethod(String daoName) {
		Method method = new Method();
		method.setName("deleteByPrimaryKey");
		method.addParameter(new Parameter(new FullyQualifiedJavaType("Integer"), "id"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addBodyLine(String.format("return %s.deleteByPrimaryKey(id);", daoName));
		return method;
	}

	private Method updateByPrimaryKeyMethod(String daoName) {
		Method method = new Method();
		method.setName("updateByPrimaryKey");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(modelDir), "record"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addBodyLine(String.format("return %s.updateByPrimaryKey(record);", daoName));
		return method;
	}

	private Method updateByPrimaryKeySelectiveMethod(String daoName) {
		Method method = new Method();
		method.setName("updateByPrimaryKeySelective");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(modelDir), "record"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addBodyLine(String.format("return %s.updateByPrimaryKeySelective(record);", daoName));
		return method;
	}

	private Method selectByPrimaryKeyMethod(String daoName) {
		Method method = new Method();
		method.setName("selectByPrimaryKey");
		method.addParameter(new Parameter(new FullyQualifiedJavaType("Integer"), "id"));
		method.setReturnType(new FullyQualifiedJavaType(modelDir));
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addBodyLine(String.format("return %s.selectByPrimaryKey(id);", daoName));
		return method;
	}

	private Method insertSelectiveMethod(String daoName) {
		Method method = new Method();
		method.setName("insertSelective");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(modelDir), "record"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addBodyLine(String.format("return %s.insertSelective(record);", daoName));
		return method;
	}

	private Method insertMethod(String daoName) {
		Method method = new Method();
		method.setName("insert");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(modelDir), "record"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PUBLIC);
		method.addBodyLine(String.format("return %s.insert(record);", daoName));
		return method;
	}

	private void addField(TopLevelClass serviceImplClass, String mapperName, String daoName) {
		Field field = new Field();
		field.addAnnotation("@Resource");
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(new FullyQualifiedJavaType(mapperName));
		field.setName(daoName);
		serviceImplClass.addField(field);
	}

	private static String toLowerCaseFirstOne(String s){
		if(Character.isLowerCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}
}
