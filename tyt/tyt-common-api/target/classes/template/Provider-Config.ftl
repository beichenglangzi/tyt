<?xml version="1.0" encoding="UTF-8"?>
<!--
 - Copyright 1999-2011 Alibaba Group.
 -  
 - Licensed under the Apache License, Version 2.0 (the "License");
 - you may not use this file except in compliance with the License.
 - You may obtain a copy of the License at
 -  
 -      http://www.apache.org/licenses/LICENSE-2.0
 -  
 - Unless required by applicable law or agreed to in writing, software
 - distributed under the License is distributed on an "AS IS" BASIS,
 - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 - See the License for the specific language governing permissions and
 - limitations under the License.
-->
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
	http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
	
	<bean id="${clazzinfo.tableas}ReadService" class="${clazzinfo.serviceImplReadPackageName}.${clazzinfo.classname}ReadServiceImpl" />
	<dubbo:service interface="${clazzinfo.serviceApiReadPackageName}.${clazzinfo.classname}ReadService" ref="${clazzinfo.tableas}ReadService" />
	<bean id="${clazzinfo.tableas}WriteService" class="${clazzinfo.serviceImplWritePackageName}.${clazzinfo.classname}WriteServiceImpl" />
	<dubbo:service interface="${clazzinfo.serviceApiWritePackageName}.${clazzinfo.classname}WriteService" ref="${clazzinfo.tableas}WriteService" />
	
</beans>