# ssm项目快速搭建 #
## 项目配置 ##
### 开发环境 ###
* server：tomcat 9.0.93
* maven：maven 3.9.8
* jdk：jdk 22
* mysql：mysql 8.0.28
* mybatis：mybatis 3.5.2
* spring：spring 4.3.6.RELEASE
### 技术整合 ###
* druid连接池
* fastJSON2
## 配置文件 ##
* web.xml
```
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
                      http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0"
         metadata-complete="true">
    <!--启动后初始页面设置-->
    <welcome-file-list>
        <welcome-file>/jsp/index.jsp</welcome-file>
    </welcome-file-list>
    <!--配置前端控制器-->
    <servlet>
        <servlet-name>springMVC</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>

        <!--配置springmvc的配置文件-->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:spring-*.xml</param-value>
        </init-param>
        <load-on-startup>
            1
        </load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>springMVC</servlet-name>
        <!--直接拦截所有请求，不再采用spring2.0的/*或者*.do方式-->
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```
* jdbc.properties
```
#基本信息
druid.driver=com.mysql.cj.jdbc.Driver
druid.jdbc_url=jdbc:mysql://localhost:3306/springmvc?characterEncoding=utf8
druid.username=root
druid.password=123456
#初始化个数
druid.initialSize=0
#最大连接池数
druid.maxActive=20
#最小连接池数
druid.minIdle=1
#最大等待事件
druid.maxWait=60000
druid.timeBetweenEvictionRunsMillis=60000
druid.minEvictableIdleTimeMillis=300000
druid.validationQuery=SELECT 'x'
druid.testWhileIdle=true
druid.testOnBorrow=false
druid.testOnReturn=false
druid.maxOpenPreparedStatements=20
druid.removeAbandoned=true
druid.removeAbandonedTimeout=1800
druid.logAbandoned=true
```
* spring-mvc.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!--开启自动扫描带web注解的类-->
    <context:component-scan base-package="com.company.project.controller"/>

    <!--开启基于注解的开发-->
    <mvc:annotation-driven/>
    <!--开启对静态资源的过滤-->
    <mvc:default-servlet-handler/>
    <!--配置视图解析-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
</beans>
```
