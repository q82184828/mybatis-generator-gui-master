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
 * 生成Service接口
 * @author chenfei
 * @date 2018-06-25 9:46
 */
public class ServiceInterfacePlugin extends PluginAdapter {
	private List<Method> methods = new ArrayList<>();

	private String modelDir;

	private FullyQualifiedJavaType primaryKeyJavaType;

	private ShellCallback shellCallback = null;

	public ServiceInterfacePlugin() {
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
		String serviceTargetPackage = context.getProperty("serviceTargetPackage");
		String serviceName = context.getProperty("serviceName");
		modelDir = context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + introspectedTable.getTableConfiguration().getDomainObjectName();
		primaryKeyJavaType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
		Interface mapperInterface = new Interface(serviceTargetPackage + "." + serviceName);
		GeneratedJavaFile mapperJavafile = null;
		if (stringHasValue(serviceName)) {
			FullyQualifiedJavaType modelJavaType = new FullyQualifiedJavaType(modelDir);
			mapperInterface.addImportedType(modelJavaType);
			mapperInterface.setVisibility(JavaVisibility.PUBLIC);

			mapperInterface.addMethod(deleteMethod());
			mapperInterface.addMethod(insertMethod());
			mapperInterface.addMethod(insertSelectiveMethod());
			mapperInterface.addMethod(selectByPrimaryKeyMethod());
			mapperInterface.addMethod(updateByPrimaryKeySelectiveMethod());
			mapperInterface.addMethod(updateByPrimaryKeyMethod());
			mapperJavafile = new GeneratedJavaFile(mapperInterface, serviceTargetDir, javaFileEncoding, javaFormatter);
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

	private Method updateByPrimaryKeyMethod() {
		Method method = new Method();
		method.setName("updateByPrimaryKey");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(modelDir), "record"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		return method;
	}

	private Method updateByPrimaryKeySelectiveMethod() {
		Method method = new Method();
		method.setName("updateByPrimaryKeySelective");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(modelDir), "record"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		return method;
	}

	private Method selectByPrimaryKeyMethod() {
		Method method = new Method();
		method.setName("selectByPrimaryKey");
		method.addParameter(new Parameter(primaryKeyJavaType, "id"));
		method.setReturnType(new FullyQualifiedJavaType(modelDir));
		return method;
	}

	private Method insertSelectiveMethod() {
		Method method = new Method();
		method.setName("insertSelective");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(modelDir), "record"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		return method;
	}

	private Method insertMethod() {
		Method method = new Method();
		method.setName("insert");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(modelDir), "record"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		return method;
	}

	private Method deleteMethod(){
		Method method = new Method();
		method.setName("deleteByPrimaryKey");
		method.addParameter(new Parameter(primaryKeyJavaType, "id"));
		method.setReturnType(new FullyQualifiedJavaType("int"));
		return method;
	}
}
