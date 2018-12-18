<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"  
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"   
    xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"   
    xmlns:context="http://www.springframework.org/schema/context"    
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans.xsd  
        http://code.alibabatech.com/schema/dubbo  
        http://code.alibabatech.com/schema/dubbo/dubbo.xsd  
        http://www.springframework.org/schema/context      
        http://www.springframework.org/schema/context/spring-context.xsd    
        ">

	<dubbo:reference id="${clazzinfo.tableas}ReadService" interface="${clazzinfo.serviceApiReadPackageName}.${clazzinfo.classname}ReadService" />
	<dubbo:reference id="${clazzinfo.tableas}WriteService" interface="${clazzinfo.serviceApiWritePackageName}.${clazzinfo.classname}WriteService" />
	
</beans>