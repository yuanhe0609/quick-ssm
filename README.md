# ssm项目快速搭建 #
## 1.项目配置 ##
### 1)开发环境 ###
* server：tomcat 9.0.93
* maven：maven 3.9.8
* jdk：jdk 22
* mysql：mysql 8.0.28
* mybatis：mybatis 3.5.2
* spring：spring 4.3.6.RELEASE
### 2)技术整合 ###
* fastJSON2
* redis
* rabbitmq
## 2.配置文件 ##
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
* mybatis-config.xml
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC
        "-//mybatis.org//DTD MyBatis Generator Configuration 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd" >
<configuration>
    <!--首先配置全局属性-->
    <settings>
        <!--开启自动填充主键功能，原理时通过jdbc的一个方法getGenerateKeys获取自增主键值-->
        <setting name="useGeneratedKeys" value="true"/>
        <!--使用别名替换列名，默认就是开启的-->
        <setting name="useColumnLabel" value="true"/>
        <!--开启驼峰命名的转换-->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>
</configuration>
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
* spring-dao.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--配置数据库连接池-->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <!--基本信息-->
        <property name = "url" value = "${druid.jdbc_url}" />
        <property name = "username" value = "${druid.username}" />
        <property name = "password" value = "${druid.password}" />
        <property name = "driverClassName" value = "${druid.driver}" />
        <!-- 最大并发连接数 -->
        <property name = "maxActive" value = "${druid.maxActive}" />
        <!-- 初始化连接数量 -->
        <property name = "initialSize" value = "${druid.initialSize}" />
        <!-- 配置获取连接等待超时的时间 -->
        <property name = "maxWait" value = "${druid.maxWait}" />
        <!-- 最小空闲连接数 -->
        <property name = "minIdle" value = "${druid.minIdle}" />
        <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
        <property name = "timeBetweenEvictionRunsMillis" value ="${druid.timeBetweenEvictionRunsMillis}" />
        <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
        <property name = "minEvictableIdleTimeMillis" value ="${druid.minEvictableIdleTimeMillis}" />
        <property name = "validationQuery" value = "${druid.validationQuery}" />
        <property name = "testWhileIdle" value = "${druid.testWhileIdle}" />
        <property name = "testOnBorrow" value = "${druid.testOnBorrow}" />
        <property name = "testOnReturn" value = "${druid.testOnReturn}" />
        <property name = "maxOpenPreparedStatements" value ="${druid.maxOpenPreparedStatements}" />
        <!-- 打开 removeAbandoned 功能 -->
        <property name = "removeAbandoned" value = "${druid.removeAbandoned}" />
        <!-- 1800 秒，也就是 30 分钟 -->
        <property name = "removeAbandonedTimeout" value ="${druid.removeAbandonedTimeout}" />
        <!-- 关闭 Abandoned 连接时输出错误日志 -->
        <property name = "logAbandoned" value = "${druid.logAbandoned}" />
    </bean>

    <!--配置sqlSessionFactory对象-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!--注入数据库连接池-->
        <property name="dataSource" ref="dataSource"/>
        <!--配置mybatis全局配置文件-->
        <property name="configLocation" value="classpath:mybatis-config.xml"/>
        <!--配置entity包,也就是实体类包，自动扫描,用于别名配置-->
        <property name="typeAliasesPackage" value="com.company.project.entity"/>
        <!--配置需要扫描的mapper.xml文件-->
        <property name="mapperLocations" value="classpath*:mapper/*.xml"/>
    </bean>

    <!--配置mapper接口包,动态实现mapper接口，注入到Spring容器-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--注入sqlSessionFactory,请注意不要使用sqlSessionFactoryBean，否则会出现注入异常-->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <!--给出要扫描的mapper接口-->
        <property name="basePackage" value="com.company.project.mapper"/>
    </bean>

</beans>
```
* spring-service
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!--导入datasource-->
    <import resource="spring-dao.xml"/>

    <!--配置自动扫描service包下的注解-->
    <context:component-scan base-package="com.company.project.service"/>

    <!--配置事物-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!--注入数据库连接池-->
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--开启基于注解的申明式事物-->
    <tx:annotation-driven transaction-manager="transactionManager"/>

</beans>
```
